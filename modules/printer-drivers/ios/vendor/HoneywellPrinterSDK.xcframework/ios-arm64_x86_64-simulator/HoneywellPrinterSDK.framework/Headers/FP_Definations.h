//
//  FP_Definations.h
//  DatamaxONeilSDK
//
//  Created by DUCNH49 on 13/01/2022.
//  Copyright © 2022 Honeywell. All rights reserved.
//

#ifndef FP_Definations_h
#define FP_Definations_h
#endif /* FP_Definations_h */

/**
 * Alignment to use when drawing text
 */
typedef NS_ENUM(NSInteger, AlignmentFP) {
    /** Text will be aligned on the bottom-left starting at the x position specified. */
    AlignmentFPBottomLeft,
    
    /** Text will be aligned on the bottom-cecnter starting at the x position specified. */
    AlignmentFPBottomCenter,
    
    /** Text will be aligned on the bottom-right starting at the x position specified. */
    AlignmentFPBottomRight,
    
    /** Text will be aligned on the center-left starting at the x position specified. */
    AlignmentFPCenterLeft,
    
    /** Text will be aligned on the centerstarting at the x position specified. */
    AlignmentFPCenter,

    /** Text will be aligned on the center-right starting at the x position specified. */
    AlignmentFPCenterRight,
    
    /** Text will be aligned on the top-left starting at the x position specified. */
    AlignmentFPTopLeft,
    
    /** Text will be aligned on the top-center starting at the x position specified. */
    AlignmentFPTopCenter,
    
    /** Text will be aligned on the top-right starting at the x position specified. */
    AlignmentFPTopRight,
};

typedef NS_ENUM(NSInteger, DirectionFP) {
    /** Text will be aligned on the bottom-left starting at the x position specified. */
    DirectionFPRight,
    
    /** Text will be aligned on the bottom-cecnter starting at the x position specified. */
    DirectionFPBottom,
    
    /** Text will be aligned on the bottom-right starting at the x position specified. */
    DirectionFPLeft,
    
    /** Text will be aligned on the center-left starting at the x position specified. */
    DirectionFPTop,
};

typedef NS_ENUM(NSInteger, BarcodeType) {
    bCODE39,
    bCODE39A,
    bCODE39C,
    bCODE128,
    bCODE128A,
    bCODE128B,
    bCODE128C,
    bEAN8,
    bEAN8CC,
    bEAN13,
    bEAN13CC,
    bEAN128,
    bEAN128A,
    bEAN128B,
    bEAN128C,
    bEAN128CCAB,
    bEAN128CCC,
    bUPCA,
    bUPCACC,
    bUPCE,
    bUPCEC,
    bRSS14,
    bRSS14C,
    bRSS14T,
    bRSS14TC,
    bRSS14S,
    bRSS14SC,
    bRSS14SCO,
    bRSS14SCOC,
    bRSS14L,
    bRSS14LC,
    bRSS14E,
    bRSS14EC,
    bRSS14ES,
    bRSS14ESC,
    bMaxiCode,
    bUSPS4CB,
    bCodaba,
    bINT2OF5A,
    bINT2OF5,
    bUCC128,
    bUPCSSC,
    bCODE11,
    bCODE16K,
    bCODE49,
    bCODE93,
    bDUN,
    bADDON5,
    bC2OF5IND,
    bC2OF5INDC,
    bC2OF5MAT,
    bMSI,
    bPLANET,
    bPLESSEY,
    bPOSTNET,
    bC2OF5,
    bADDON2,
};

