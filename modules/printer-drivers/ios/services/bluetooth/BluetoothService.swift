import Foundation
import ExternalAccessory
import CoreBluetooth

/// On iOS, Classic Bluetooth SPP is only reachable through Apple's MFi program
/// via the ExternalAccessory framework. The "address" concept on Android maps
/// to `EAAccessory.serialNumber` on iOS, and "paired devices" maps to
/// `EAAccessoryManager.shared().connectedAccessories` filtered by the supported
/// protocol strings declared in `Info.plist` under
/// `UISupportedExternalAccessoryProtocols`.
///
/// Bluetooth power state (on/off) is observed via `CBCentralManager` because
/// the ExternalAccessory framework does not expose radio state directly.
final class BluetoothService: NSObject {

    // MARK: - Singleton

    private static var instance: BluetoothService?
    private static let instanceLock = NSLock()

    static func getInstance(eventHandler: BluetoothEventHandler) -> BluetoothService {
        instanceLock.lock()
        defer { instanceLock.unlock() }
        if let existing = instance {
            return existing
        }
        let created = BluetoothService(eventHandler: eventHandler)
        instance = created
        NSLog("[BluetoothService] --> created")
        return created
    }

    static func clearInstance() {
        instanceLock.lock()
        defer { instanceLock.unlock() }
        instance?.stop()
        instance?.teardownObservers()
        instance = nil
        NSLog("[BluetoothService] --> instance cleared")
    }

    // MARK: - Properties

    private weak var eventHandler: BluetoothEventHandler?

    private let stateQueue = DispatchQueue(label: "com.printerdrivers.bluetoothservice.state")
    private var _currentState: Int = BluetoothConnectionState.NONE
    private(set) var currentState: Int {
        get { stateQueue.sync { _currentState } }
        set { stateQueue.sync { _currentState = newValue } }
    }

    /// Currently open EASession (nil when not connected).
    private var session: EASession?
    /// Currently selected accessory (nil when not connected).
    private var accessory: EAAccessory?
    /// Pending outbound bytes (written as the output stream signals space).
    private var outBuffer = Data()
    /// Accumulated inbound bytes (flushed to eventHandler on each read).
    private let inputBufferSize = 1024

    /// CBCentralManager used purely to observe Bluetooth radio on/off state.
    private var centralManager: CBCentralManager?
    private var isBluetoothPoweredOn: Bool = false

    // MARK: - Init

    private init(eventHandler: BluetoothEventHandler) {
        self.eventHandler = eventHandler
        super.init()
        setupObservers()
    }

    deinit {
        teardownObservers()
    }

    /// Reset service to LISTEN state. Closes any open session.
    func start() {
        NSLog("[BluetoothService] --> start")
        closeSessionInternal(notifyDisconnected: false)
        currentState = BluetoothConnectionState.LISTEN
    }

    /// Stop the service and close any open session.
    ///
    /// iOS note: unlike Android (which closes the RFCOMM socket and drops the
    /// Bluetooth ACL link), this only tears down the app's `EASession`. The
    /// MFi accessory remains paired with the OS and may still appear as
    /// connected in Settings → Bluetooth. There is no public API to force-
    /// disconnect a paired MFi accessory — the user must power off the
    /// printer or forget the device in Settings. The Woosim SDK also exposes
    /// no power-off / sleep command for the WSP-i350, so this is the best we
    /// can do at the app layer.
    func stop() {
        NSLog("[BluetoothService] --> stop")
        let wasConnected = currentState == BluetoothConnectionState.CONNECTED
        closeSessionInternal(notifyDisconnected: false)
        currentState = BluetoothConnectionState.NONE
        if wasConnected {
            DispatchQueue.main.async { [weak self] in
                self?.eventHandler?.onDeviceDisconnected()
            }
        }
    }

    /// Open an EASession with the accessory whose serialNumber matches `address`.
    /// The `secure` flag is ignored on iOS — MFi sessions are always secure.
    func connect(address: String, secure: Bool = true) {
        NSLog("[BluetoothService] --> connect to serial: \(address)")

        // Always cancel an existing session before opening a new one.
        closeSessionInternal(notifyDisconnected: false)

        currentState = BluetoothConnectionState.CONNECTING

        guard let (targetAccessory, protocolString) = findAccessory(forSerial: address) else {
            connectionFailed(error: "Accessory not found or protocol not supported: \(address)")
            return
        }

        guard openSession(targetAccessory, protocolString) else {
            connectionFailed(error: "Failed to open session with \(targetAccessory.name)")
            return
        }

        accessory = targetAccessory
        currentState = BluetoothConnectionState.CONNECTED

        let name = displayName(for: targetAccessory)
        let serial = targetAccessory.serialNumber
        DispatchQueue.main.async { [weak self] in
            self?.eventHandler?.onDeviceConnected(deviceName: name, deviceAddress: serial)
        }
    }

    /// Write data to the connected accessory.
    func write(_ data: Data) {
        guard currentState == BluetoothConnectionState.CONNECTED else {
            NSLog("[BluetoothService] --> write() called but not connected")
            return
        }
        guard session != nil else { return }
        outBuffer.append(data)
        pumpWrite()
    }

    /// Return the list of currently connected MFi accessories whose protocol
    /// strings intersect with the app's `UISupportedExternalAccessoryProtocols`.
    /// Each entry is `(name, serialNumber)`.
    func getPairedDevices() -> [(name: String, address: String)] {
        let supported = supportedProtocolStrings()
        let all = EAAccessoryManager.shared().connectedAccessories
        return all.compactMap { acc in
            let hasMatch = acc.protocolStrings.contains(where: { supported.contains($0) })
            guard hasMatch else { return nil }
            return (displayName(for: acc), acc.serialNumber)
        }
    }

    /// Compose a user-facing name. `EAAccessory.name` is the MFi-level name
    /// (often a generic value like "BT Printer"), not the Bluetooth GAP name
    /// shown in Settings. Prefer the model number when available.
    private func displayName(for accessory: EAAccessory) -> String {
        let model = accessory.modelNumber.trimmingCharacters(in: .whitespaces)
        let name = accessory.name.trimmingCharacters(in: .whitespaces)

        if !model.isEmpty {
            return model
        }
        if !name.isEmpty {
            return name
        }
        return "Unknown printer"
    }

    /// Whether Bluetooth hardware is present on the device.
    /// iOS does not expose this directly; all supported devices have Bluetooth,
    /// so we return true once CoreBluetooth has reported any non-`.unsupported` state.
    func isBluetoothAvailable() -> Bool {
        guard let central = centralManager else { return true }
        return central.state != .unsupported
    }

    /// Whether Bluetooth is currently powered on (via CoreBluetooth observation).
    func isBluetoothEnabled() -> Bool {
        return isBluetoothPoweredOn
    }

    // MARK: - Session handling

    private func openSession(_ accessory: EAAccessory, _ protocolString: String) -> Bool {
        accessory.delegate = self
        let newSession = EASession(accessory: accessory, forProtocol: protocolString)
        guard let newSession = newSession else {
            return false
        }
        newSession.inputStream?.delegate = self
        newSession.inputStream?.schedule(in: .main, forMode: .common)
        newSession.inputStream?.open()
        newSession.outputStream?.delegate = self
        newSession.outputStream?.schedule(in: .main, forMode: .common)
        newSession.outputStream?.open()
        self.session = newSession
        return true
    }

    private func closeSessionInternal(notifyDisconnected: Bool) {
        if let session = session {
            session.inputStream?.close()
            session.inputStream?.remove(from: .main, forMode: .common)
            session.inputStream?.delegate = nil
            session.outputStream?.close()
            session.outputStream?.remove(from: .main, forMode: .common)
            session.outputStream?.delegate = nil
        }
        session = nil
        accessory = nil
        outBuffer.removeAll(keepingCapacity: false)

        if notifyDisconnected {
            DispatchQueue.main.async { [weak self] in
                self?.eventHandler?.onDeviceDisconnected()
            }
        }
    }

    private func connectionFailed(error: String) {
        if currentState == BluetoothConnectionState.NONE { return }
        currentState = BluetoothConnectionState.NONE
        DispatchQueue.main.async { [weak self] in
            self?.eventHandler?.onConnectionFailed(error: error)
        }
        // Restart listening mode like the Android service does.
        start()
    }

    private func connectionLost() {
        if currentState == BluetoothConnectionState.NONE { return }
        currentState = BluetoothConnectionState.NONE
        DispatchQueue.main.async { [weak self] in
            self?.eventHandler?.onConnectionLost()
        }
        start()
    }

    private func pumpWrite() {
        guard let output = session?.outputStream else { return }
        while output.hasSpaceAvailable && !outBuffer.isEmpty {
            let bytesWritten = outBuffer.withUnsafeBytes { raw -> Int in
                guard let base = raw.baseAddress?.assumingMemoryBound(to: UInt8.self) else { return -1 }
                return output.write(base, maxLength: outBuffer.count)
            }
            if bytesWritten < 0 {
                NSLog("[BluetoothService] --> write error")
                connectionLost()
                return
            } else if bytesWritten == 0 {
                break
            } else {
                outBuffer.removeFirst(bytesWritten)
            }
        }
    }

    private func pumpRead() {
        guard let input = session?.inputStream else { return }
        var chunk = Data()
        let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: inputBufferSize)
        defer { buffer.deallocate() }
        while input.hasBytesAvailable {
            let read = input.read(buffer, maxLength: inputBufferSize)
            if read > 0 {
                chunk.append(buffer, count: read)
            } else {
                break
            }
        }
        if !chunk.isEmpty {
            DispatchQueue.main.async { [weak self] in
                self?.eventHandler?.onDataReceived(data: chunk)
            }
        }
    }

    // MARK: - Accessory lookup

    private func findAccessory(forSerial serial: String) -> (EAAccessory, String)? {
        let supported = supportedProtocolStrings()
        for acc in EAAccessoryManager.shared().connectedAccessories where acc.serialNumber == serial {
            if let match = acc.protocolStrings.first(where: { supported.contains($0) }) {
                return (acc, match)
            }
        }
        return nil
    }

    private func supportedProtocolStrings() -> [String] {
        return (Bundle.main.object(forInfoDictionaryKey: "UISupportedExternalAccessoryProtocols") as? [String]) ?? []
    }

    // MARK: - Observers

    private func setupObservers() {
        // MFi accessory notifications
        EAAccessoryManager.shared().registerForLocalNotifications()
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleAccessoryDidConnect(_:)),
            name: .EAAccessoryDidConnect,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleAccessoryDidDisconnect(_:)),
            name: .EAAccessoryDidDisconnect,
            object: nil
        )

        // CoreBluetooth purely for on/off state
        centralManager = CBCentralManager(delegate: self, queue: nil, options: [
            CBCentralManagerOptionShowPowerAlertKey: false
        ])
    }

    private func teardownObservers() {
        NotificationCenter.default.removeObserver(self, name: .EAAccessoryDidConnect, object: nil)
        NotificationCenter.default.removeObserver(self, name: .EAAccessoryDidDisconnect, object: nil)
        EAAccessoryManager.shared().unregisterForLocalNotifications()
        centralManager?.delegate = nil
        centralManager = nil
    }

    @objc private func handleAccessoryDidConnect(_ note: Notification) {
        // We don't auto-open sessions here; JS layer decides via connect().
        emitBluetoothStateChanged()
    }

    @objc private func handleAccessoryDidDisconnect(_ note: Notification) {
        guard let disconnected = note.userInfo?[EAAccessoryKey] as? EAAccessory else { return }
        if let current = accessory, current.connectionID == disconnected.connectionID {
            let wasConnected = currentState == BluetoothConnectionState.CONNECTED
            closeSessionInternal(notifyDisconnected: false)
            currentState = BluetoothConnectionState.NONE
            if wasConnected {
                DispatchQueue.main.async { [weak self] in
                    self?.eventHandler?.onConnectionLost()
                }
            }
        }
        emitBluetoothStateChanged()
    }

    private func emitBluetoothStateChanged() {
        let available = isBluetoothAvailable()
        let enabled = isBluetoothEnabled()
        DispatchQueue.main.async { [weak self] in
            self?.eventHandler?.onBluetoothStateChanged(isAvailable: available, isEnabled: enabled)
        }
    }
}

// MARK: - EAAccessoryDelegate

extension BluetoothService: EAAccessoryDelegate {
    func accessoryDidDisconnect(_ accessory: EAAccessory) {
        if self.accessory?.connectionID == accessory.connectionID {
            let wasConnected = currentState == BluetoothConnectionState.CONNECTED
            closeSessionInternal(notifyDisconnected: false)
            currentState = BluetoothConnectionState.NONE
            if wasConnected {
                DispatchQueue.main.async { [weak self] in
                    self?.eventHandler?.onConnectionLost()
                }
            }
        }
    }
}

// MARK: - StreamDelegate

extension BluetoothService: StreamDelegate {
    func stream(_ aStream: Stream, handle eventCode: Stream.Event) {
        switch eventCode {
        case .openCompleted:
            break
        case .hasBytesAvailable:
            pumpRead()
        case .hasSpaceAvailable:
            pumpWrite()
        case .errorOccurred:
            NSLog("[BluetoothService] --> stream error")
            connectionLost()
        case .endEncountered:
            NSLog("[BluetoothService] --> stream end encountered")
            connectionLost()
        default:
            break
        }
    }
}

// MARK: - CBCentralManagerDelegate (radio on/off observation)

extension BluetoothService: CBCentralManagerDelegate {
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        let previouslyOn = isBluetoothPoweredOn
        switch central.state {
        case .poweredOn:
            isBluetoothPoweredOn = true
        case .poweredOff, .unauthorized, .unsupported, .resetting, .unknown:
            isBluetoothPoweredOn = false
        @unknown default:
            isBluetoothPoweredOn = false
        }
        if previouslyOn != isBluetoothPoweredOn {
            emitBluetoothStateChanged()
        }
    }
}
