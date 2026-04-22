Pod::Spec.new do |s|
  s.name           = 'PrinterDrivers'
  s.version        = '1.0.0'
  s.summary        = 'A sample project summary'
  s.description    = 'A sample project description'
  s.author         = ''
  s.homepage       = 'https://docs.expo.dev/modules/'
  s.platforms      = {
    :ios => '15.1',
    :tvos => '15.1'
  }
  s.source         = { git: '' }
  s.static_framework = true

  s.dependency 'ExpoModulesCore'
  s.vendored_libraries = "vendor/woosim302/libwoosim302.a"
  s.preserve_paths     = "vendor/woosim302/libwoosim302.a", "vendor/woosim302/woosim302.swiftmodule"

  # Swift/Objective-C compatibility
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
    # Swift looks for <ModuleName>.swiftmodule directly inside each include
    # path, so point at the folder that *contains* woosim302.swiftmodule.
    'SWIFT_INCLUDE_PATHS' => '$(PODS_TARGET_SRCROOT)/vendor/woosim302',
    'LIBRARY_SEARCH_PATHS' => '$(inherited) $(PODS_TARGET_SRCROOT)/vendor/woosim302',
    'OTHER_LDFLAGS'       => '$(inherited) -lwoosim302',
    # Required because the SDK ships an objc category on NSData/NSString internally
    'OTHER_SWIFT_FLAGS'   => '$(inherited) -Xcc -fmodules'
  }

  s.source_files = "**/*.{h,m,mm,swift,hpp,cpp}"
end
