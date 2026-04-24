//
//  PrinterState_DPL.h
//  DatamaxONeilSDK_iPhone
//
//  Created by Quang Phan on 1/13/14.
//  Copyright (c) 2014 Datamax-O'Neil. All rights reserved.
//

#import "ConnectionBase.h"

/**
 * This is the base class for the printer state code. This will handle
 * configuration queries and settings and all of the shared routines for the
 * sub-classes that each handle a specific printer query.
 */
@interface PrinterState_FP: NSObject {
    /**
     * Query string
     */
    NSString *query;
    
    /**
     * Query to retrieve data
     */
    NSData *queryData;

    /**
     * Connection to the printer
     */
    ConnectionBase *connection;
    
    /**
     * This setting help us to see detail commands using
     */
    bool enableLog;
}

@property bool enableLog;

/**
 * This will re-query the printer to update the values for this set of
 * parameters. All current values will be replaced with the values on the
 * printer.
 * <p>
 * Initially the FP  objects are empty. Use this to <I>update</I>
 * them with the latest values from the printer. Unlink the other version of
 * Update which takes the printer response string, this will handle all of
 * the printer querying for you.
 *
 * @param complete callback when get the result.
 */
- (void)excute: (void(^)(NSString *))complete;
- (void)excuteData: (void(^)(NSString *))complete;
/**
 * This function will log detail command if set YES before
 */
- (void)logging:(NSString *)content;
@end

