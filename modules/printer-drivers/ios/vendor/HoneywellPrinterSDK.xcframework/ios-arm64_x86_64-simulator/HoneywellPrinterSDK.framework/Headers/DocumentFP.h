//
//  DocumentFP.h
//  DatamaxONeilSDK
//
//  Created by DUCNH49 on 09/01/2022.
//  Copyright © 2022 Honeywell. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Document.h"
#import "ParametersFP.h"

NS_ASSUME_NONNULL_BEGIN

@interface DocumentFP : Document {
    /**
     * Vertical adjustment of the point where printing begins
     */
    int rowOffSet;
    
    /**
     * Horizontal adjustment of the point where printing begins
     */
    int columnOffSet;
    
    /**
     * The number of label copies to be printed
     */
    int printQuantity;
    
    /**
     * This setting help us to see detail commands using
     */
    bool enableLog;
}

@property int rowOffSet;
@property int columnOffSet;
@property int printQuantity;
@property bool enableLog;

/**
 * This will trigger print command
 */
- (void)printDocument;

/**
 * This function will print the provided text to the document object using the provided printing parameter values.
 * @param textString This is the text you wish to print.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters This ParametersFP object specifies any
 * printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeText: (NSString *) textString
            atRow: (int) row
         atColumn: (int) col
      paramObject: (ParametersFP *)parameters;

/**
 * This will cause the specified image that is already stored on the printer to be printed at the given location.
 * @param imageName Name of image stored on printer.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters This ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
-(void) writeImageStored: (NSString*) imageName
                   atRow: (int) row
                atColumn: (int) col
             paramObject: (ParametersFP*) parameters;

/**
 * This will cause the printer to print the specified image data at the given location.
 * @param imageData Image data to be printed.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param width set width of the image to resize to. Use -1 for default width
 * @param height set width of the image to resize to. Use -1 for default width
 * @param parameters This ParametersFP object specifies any
 * printing parameter values you wish to alter for the printing of this item.
 */
-(void) writeImage: (NSData *) imageData
             atRow: (int) row
          atColumn: (int) col
          atWidth: (int) width
          atHeight: (int) height
       paramObject: (ParametersFP*) parameters;

/**
 * This will cause the printer to print the specified image object at the given location.
 * @param imageObject Image object to be printed.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param width set width of the image to resize to. Use -1 for default width
 * @param height set width of the image to resize to. Use -1 for default width
 * @param parameters This ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
-(void) writeImageFromObject: (UIImage*) imageObject
                       atRow: (int) row
                    atColumn:(int) col
                    atWidth:(int)width
                    atHeight:(int)height
                 paramObject: (ParametersFP*) parameters;

/**
 * This will cause the printer to print the specified image file at the given location.
 * @param imagePath File name or path of image to be printed.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param width set width of the image to resize to. Use -1 for default width
 * @param height set width of the image to resize to. Use -1 for default width
 * @param parameters This ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item
 */
-(void)writeImageFromPath: (NSString *) imagePath
                    atRow: (int) row
                 atColumn: (int) col
                  atWidth:(int)width
                 atHeight:(int)height
              paramObject: (ParametersFP *) parameters;

/**
 * This function will print the provided text to the document as a barcode using the font specified and the provided printing parameter values.
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeQRCode: (NSString *) textString
              atRow: (int) row
           atColumn: (int) col
        paramObject: (ParametersFP *) parameters;


/**
 * This function will print the provided text to the document as a barcode using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 * @param otherConfig Other configurations.
 */
- (void)writeBarCode: (NSString *)textString
               atRow: (int)row
            atColumn: (int)col
         paramObject: (ParametersFP *)parameters
         otherConfig: (NSString *) otherConfig;

/**
 * This function will print the provided text to the document as a barcode using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param type Type of barcode
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
-(void)writeBarCode: (BarcodeType) type
         textString: (NSString*) textString
              atRow: (int) row
           atColumn: (int) col
        paramObject: (ParametersFP*) parameters;

/**
 * This function will print the provided text to the document as a barcode DataMatrix using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param barcodeShape barcodeShape shape for barcode.
 * @param barcodeSize barcodeSize barcode size.
 * @param barcodeECLSupport barcodeECLSupport enable/disable ECL support.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeDataMatrix: (NSString *)textString
                         atRow: (int)row
                      atColumn: (int)col
                  barcodeShape: (int)barcodeShape
                   barcodeSize: (int)barcodeSize
             barcodeECLSupport: (int)barcodeECLSupport
                   paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document as a barcode AZTEC using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param errorCorrection error correction.
 * @param menuSymbol menu symbol.
 * @param symbolAppend symbol append.
 * @param isEnableECL barcodeECLSupport enable/disable ECL support.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeAZTEC: (NSString *)textString
                    atRow: (int)row
                 atColumn: (int)col
          errorCorrection: (int)errorCorrection
               menuSymbol: (int)menuSymbol
             symbolAppend: (int)symbolAppend
              isEnableECL: (int)isEnableECL
              paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document as a barcode DotCode using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param reflectanceRevesal print type.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeDotCode: (NSString *)textString
                      atRow: (int)row
                   atColumn: (int)col
         reflectanceRevesal: (int)reflectanceRevesal
                paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document as a barcode ISBT128 using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param flagCharacter flag characters.
 * @param hurmanReableScale hurman readable scale.
 * @param keyboardChecCharacter keyboard check characters.
 * @param processingVerticalData processing of space and veritcal bar in data.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeISBT128: (NSString *)textString
                      atRow: (int)row
                   atColumn: (int)col
              flagCharacter: (int)flagCharacter
          hurmanReableScale: (int)hurmanReableScale
      keyboardChecCharacter: (int)keyboardChecCharacter
     processingVerticalData: (int)processingVerticalData
                paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document as a barcode DotCode using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param dataStruct data struct.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeHIBC: (NSString *)barcodeID
              textString: (NSString *)textString
                   atRow: (int)row
                atColumn: (int)col
              dataStruct: (int)dataStruct
             paramObject: (ParametersFP *)parameters;
/**
 * This function will print the provided text to the document as a barcode GRIDMATRIX using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param reflectanceRevesal print type.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeGridMatrix: (NSString *)textString
                         atRow: (int)row
                      atColumn: (int)col
                 selectableECL: (int)selectableECL
                   isEnableECL: (int)isEnableECL
                   paramObject: (ParametersFP *)parameters;
/**
 * This function will print the provided text to the document as a barcode PDF417 using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodePDF417: (NSString *)textString
                     atRow: (int)row
                  atColumn: (int)col
               paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document as a barcode MicroPDF417 using the font specified and the provided printing parameter values.
 * <p> Valid values: <p> A to Z(bc with human readable text), <p> a to z (bc with non-human readable text)(except P,u,v,z), or <p> Wna:(n= 1-9, a = a-s/A-S, No n is an implied 1)
 * @param textString Text to print as barcode.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param parameters ParametersFP object specifies any printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBarCodeMicroPDF417: (NSString *)textString
                          atRow: (int)row
                       atColumn: (int)col
                    paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided text to the document object using the provided printing parameter values.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param lengh Lengh of Line.
 * @param lineWeight Weigh of line
 * @param parameters This ParametersFP object specifies any
 * printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeLineAtRow: (int) row
              atColumn: (int) col
           lenghOfLine: (int) lengh
            lineWeight: (int) lineWeight
           paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided Box to the document object using the provided printing parameter values.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param heightInDot Height in Dot.
 * @param widthInDot Width in Dot.
 * @param lineWeight Weight of Line.
 * @param textInside Text inside the Box.
 * @param horizontalPadding Horizaontal padding between Box and Text.
 * @param verticalPadding Vertical padding between Box and Text.
 * @param lineDelimiter Delimier of Line.
 * @param hyphenDelimiter Hyphen Delimiter of Line.
 * @param parameters This ParametersFP object specifies any
 * printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeBoxAtRow: (int)row
             atColumn: (int)col
          heightInDot: (int)heightInDot
           widthInDot: (int)widthInDot
           lineWeight: (int)lineWeight
           textInside: (NSString *)textInside
    horizontalPadding: (int)horizontalPadding
      verticalPadding: (int)verticalPadding
        lineDelimiter: (NSString *)lineDelimiter
      hyphenDelimiter: (NSString *)hyphenDelimiter
          paramObject: (ParametersFP *)parameters;

/**
 * This function will print the provided Cricle to the document object using the provided printing parameter values.
 * @param row Row position, starting from zero, to start printing at.
 * @param col Column position, starting from zero, to start printing at.
 * @param heightInDot Height of the ellipse.
 * @param widthInDot Width of the ellipse.
 * @param lineWeightInDot Weight of line
 * @param parameters This ParametersFP object specifies any
 * printing parameter values you wish to alter for the printing of this item.
 */
- (void)writeCricleAtRow: (int)row
                atColumn: (int)col
             heightInDot: (int)heightInDot
              widthInDot: (int)widthInDot
         lineWeightInDot: (int)lineWeightInDot
             paramObject: (ParametersFP *)parameters;

@end
NS_ASSUME_NONNULL_END
