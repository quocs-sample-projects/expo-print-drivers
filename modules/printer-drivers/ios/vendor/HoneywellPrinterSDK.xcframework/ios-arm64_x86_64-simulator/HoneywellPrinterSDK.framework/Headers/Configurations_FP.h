//
//  PrintSettings_DPL.h
//  DatamaxONeilSDK_iPhone
//
//  Created by Quang Phan on 1/14/14.
//  Copyright (c) 2014 Datamax-O'Neil. All rights reserved.
//

#import "PrinterState_FP.h"
#import <UIKit/UIKit.h>

@interface Configurations_FP : PrinterState_FP

/**
 * The default constructor will initialize the class with default values.
 * <p>
 * Initially all of the _IsValid parameters will be false because no data has been processed.  To 'populate' the values, the object must invoke <see cref="PrinterState_DPL.Update(int)">Update</see> with the query response string.
 * @param conn connection to printer
 */
- (id)initWithConnection: (ConnectionBase *) conn;
- (void)getConfigFrom: (NSString *)command complete: (void(^)(NSString *))complete;
- (void)setConfigFrom: (NSString *)command withValue: (NSString*) value complete: (void(^)(NSString *))complete;
- (void)getAllConfigurations: (void(^)(NSArray *))complete;
- (NSString *)setConfigFormatFrom: (NSString *)command withValue: (NSString*) value;

// MARK: - Media configurations
- (void)getMediaType: (void(^)(NSString *))complete;
- (void)setMediaType: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getPrintMethod: (void(^)(NSString *))complete;
- (void)setPrintMethod: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getPrintAreaMarginX: (void(^)(NSString *))complete;
- (void)setPrintAreaMarginX: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getPrintAreaWidth: (void(^)(NSString *))complete;
- (void)setPrintAreaWidth: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getPrintAreaLength: (void(^)(NSString *))complete;
- (void)setPrintAreaLength: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getClipDefault: (void(^)(NSString *))complete;
- (void)setClipDefault: (NSString *)value complete: (void(^)(NSString *))complete;
- (void)getPrintMode: (void(^)(NSString *))complete;
- (void)setPrintMode: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getLabelTopAdjust: (void(^)(NSString *))complete;
- (void)setLabelTopAdjust: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getLabelRestAdjust: (void(^)(NSString *))complete;
- (void)setLabelRestAdjust: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getMediaCalibrationMode: (void(^)(NSString *))complete;
- (void)setMediaCalibrationMode: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getLengthSlowMode: (void(^)(NSString *))complete;
- (void)setLengthSlowMode: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getPowerUpAction: (void(^)(NSString *))complete;
- (void)setPowerUpAction: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getHeadDownAction: (void(^)(NSString *))complete;
- (void)setHeadDownAction: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getHoldFeedButtonAction: (void(^)(NSString *))complete;
- (void)setHoldFeedButtonAction: (NSString *) value complete: (void(^)(NSString *))complete;

// MARK: - Print qual configurations
- (void)getPrintSpeed: (void(^)(NSString *))complete;
- (void)setPrintSpeed: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getMediaSensitivity: (void(^)(NSString *))complete;
- (void)setMediaSensitivity: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getDarkness: (void(^)(NSString *))complete;
- (void)setDarkness: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getContrast: (void(^)(NSString *))complete;
- (void)setContrast: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getLabelRotation: (void(^)(NSString *))complete;
- (void)setLabelRotation: (NSString *) value complete: (void(^)(NSString *))complete;

// MARK: - System configurations
- (void)getSystemName: (void(^)(NSString *))complete;
- (void)setSystemName: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getSystemLocation: (void(^)(NSString *))complete;
- (void)setSystemLocation: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getSystemContact: (void(^)(NSString *))complete;
- (void)setSystemContact: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getDisplayLanguage: (void(^)(NSString *))complete;
- (void)setDisplayLanguage: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getTime: (void(^)(NSString *))complete;
- (void)setTime: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getDate: (void(^)(NSString *))complete;
- (void)setDate: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getCommonLanguage: (void(^)(NSString *))complete;
- (void)setCommonLanguage: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getBrightness: (void(^)(NSString *))complete;
- (void)setBrightness: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getTimeFormat: (void(^)(NSString *))complete;
- (void)setTimeFormat: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getDateFormat: (void(^)(NSString *))complete;
- (void)setDateFormat: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getMediaLowDiameter: (void(^)(NSString *))complete;
- (void)setMediaLowDiameter: (NSString *) value complete: (void(^)(NSString *))complete;
- (void)getRibbonLowDiameter: (void(^)(NSString *))complete;
- (void)setRibbonLowDiameter: (NSString *) value complete: (void(^)(NSString *))complete;

// MARK: - Resources
- (void)getFont: (void(^)(NSString *))complete;
- (void)getImages: (void(^)(NSString *))complete;
- (void)getEncoding: (void(^)(NSArray *))complete;
- (void)installFontWith: (NSData *)fontData name: (NSString *)name complete: (void(^)(NSString *))complete;
- (void)installImageWith: (NSData *)imageData name: (NSString *)name complete: (void(^)(NSString *))complete;
-(void)installImageWithPath: (NSString *)imagePath name: (NSString *)name complete: (void(^)(NSString *))complete;
- (void)installEncodingWith: (NSData *)encodingData name: (NSString *)name complete: (void(^)(NSString *))complete;

// MARK: - Printer Stat
- (void)getPrinterStatList: (void(^)(NSString *))complete;
- (void)getPrinterStatListFromPath: (NSString *)path complete: (void(^)(NSString *))complete;

// MARK: - Printer Informations
- (void)getRibbonLowSensorStatus: (void(^)(NSString *))complete;
- (void)getPaperLowSensorStatus: (void(^)(NSString *))complete;
- (void)getTphResolution: (void(^)(NSString *))complete;
- (void)getTphDots: (void(^)(NSString *))complete;
- (void)getPrinterSerialNumber: (void(^)(NSString *))complete;
- (void)getPrinterModelNumber: (void(^)(NSString *))complete;
- (void)getPrintJobErrorCode: (void(^)(NSString *))complete;

@end
