//
//  ParametersFP.h
//  DatamaxONeilSDK
//
//  Created by DUCNH49 on 09/01/2022.
//  Copyright © 2022 Honeywell. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Parameters.h"
#import "FP_Definations.h"

NS_ASSUME_NONNULL_BEGIN

@interface ParametersFP : Parameters {
    /**
     Alignment to use when drawing text
     */
    AlignmentFP alignment;
    
    /**
     Direction to use when drawing text
     */
    DirectionFP printDirection;
    
    /**
     Set of character
     */
    NSString *characterSet;
    
    /**
     Font name to use when drawing text
     */
    NSString *fontName;
    
    /**
     Set Font Bold to use when drawing text
     */
    bool isFontBold;
    
    /**
     Set Font size to use when drawing text
     */
    int fontSize;
    
    /**
     Set Font width to use when drawing text
     */
    int fontWidth;
    
    /**
     Set Font angle to use when drawing text
     */
    int slant;
    
    /**
     Scale image to size at x axis when drawing
     */
    int scaleImageX;
    
    /**
     Scale image to size at y axis when drawing
     */
    int scaleImageY;
        
    /**
     Set ratio wide bar when drawing
     */
    int barcodeRatioWideBar;
    
    /**
     Set ratio narrow when drawing
     */
    int barcodeRatioNarrowBar;
    
    /**
     Specific height of barcode
     */
    int barcodeHeight;
        
    /**
     Margtin barcode bottom to top text
     */
    int barcodeFontOffset;
    
    /**
     Mag of barcode font width
     */
    int horizontalMultilier;
    
    /**
     Mag of barcode font height
     */
    int verticalMultilier;
    
    /**
     Enlargement of barcode
     */
    int barcodeEnlargement;
    
    /**
     Ratio width and height of barcode
     */
    int widthToHeightRatio;
    
    /**
     Defination whre is barcode will insert
     */
    int barFontInsertPoint;
    
    /**
     Padding horizontal between Insertpoint and Barcode
     */
    int barFontHorizontalInsertOffset;
    
    /**
     Padding vertical between Insertpoint and Barcode
     */
    int barFontVeriticalInsertOffset;
    
    /**
     Enable or disable draw barcode guard
     */
    int barcodeGuard;
    
    /**
     Character exclusion in bar code [parenthese, space]
     */
    bool barcodeCharacterExclusion;
    
    /**
     Printting of bar code interpretation
     */
    bool barFontEnable;
    
    /**
     Barcode height of seperator partten
     */
    int barcodeHeightSeperator;
    
    /**
     Barcode height of each row
     */
    int barcodeHeightOfEachRow;
    
    /**
    Element size in dots
     */
    int elementSizeInDots;
    
    /**
    Element width in dots
     */
    int elementWidthInDots;
    
    /**
    Element height in dots
     */
    int elementHeightInDots;

    /**
    Set QRCode model
     */
    int qrcodeModel;
    
    /**
    Set security level
     */
    int securityLevel;
    
    /**
    Padding between linear and 2D barcode
     */
    int barcodeSeperatorCharacterOfLinearAnd2D;
    
    /**
    Barcode 2D hurman readable field
     */
    bool barcode2DHurmanReadable;
    
    /**
    Barcode aspect ratio width e.g PDF417
     */
    int barcodeAspectRatioWidth;
    
    /**
    Barcode  aspect ratio height: e.g PDF417
     */
    int barcodeAspectRatioHeight;
    
    /**
    Barcode number of rows e.g PDF417
     */
    int barcodeNumberOfRow;
    
    /**
    Barcode number row/column in 2D e.g EAN128CC
     */
    int barcodeNumberRowPerColumnIn2D;
    
    /**
    Barcode number of column e.g PDF417
     */
    int barcodeNumberOfCol;
    
    /**
    Barcode truncate e.g PDF417
     */
    int barcodeTruncate;
    
    /**
    Barcode segment per row e.g RSS14SE
     */
    int barcodeSegmentPerRow;
}

@property AlignmentFP alignment;
@property DirectionFP printDirection;
@property NSString *characterSet;
@property NSString *fontName;
@property bool isFontBold;
@property int fontSize;
@property int fontWidth;
@property int slant;
@property int scaleImageX;
@property int scaleImageY;
@property int barcodeRatioWideBar;
@property int barcodeRatioNarrowBar;
@property int barcodeHeight;
@property int barcodeFontOffset;
@property int horizontalMultilier;
@property int verticalMultilier;
@property int barcodeEnlargement;
@property int widthToHeightRatio;
@property int barFontInsertPoint;
@property int barFontHorizontalInsertOffset;
@property int barFontVeriticalInsertOffset;
@property int barcodeGuard;
@property bool barcodeCharacterExclusion;
@property bool barFontEnable;
@property int elementSizeInDots;
@property int qrcodeModel;
@property int securityLevel;
@property int barcodeHeightSeperator;
@property int barcodeHeightOfEachRow;
@property int barcodeSeperatorCharacterOfLinearAnd2D;
@property bool barcode2DHurmanReadable;
@property int barcodeAspectRatioWidth;
@property int barcodeAspectRatioHeight;
@property int barcodeNumberOfRow;
@property int barcodeNumberOfCol;
@property int barcodeTruncate;
@property int elementWidthInDots;
@property int elementHeightInDots;
@property int barcodeNumberRowPerColumnIn2D;
@property int barcodeSegmentPerRow;

@end

NS_ASSUME_NONNULL_END
