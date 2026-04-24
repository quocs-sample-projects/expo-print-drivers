//
// ConnectionBase_Internal.h
// ONeilSDK_iPhone
//
// Created by Quang Phan
// Copyright 2014 Datamax-O'Neil. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "ConnectionBase.h"

/**
 * This class contains all the abstract methods and properties of the ConnectionBase
 * class.
 */
@interface ConnectionBase(Internal) 

/**
 * This will return true if there is data available to be read.  This
 * indicates if the device itself, however we are talking to it, has
 * data to read.
 */
-(bool) isDeviceDataPresent;

/**
 * This will open the current connection if not open.  The base class
 * function handles the starting of the threads used to do the asynchronous
 * communication with the device.  The derived class will do the device
 * specific routines.
 */
-(bool) innerOpen;

/**
 * This will close the current connection, if open, after all existing
 * items have finished printing.  This is the internally called function
 * that is called with the parameter as true if it is used within this
 * framework.  If the user calls close, then that close
 * will pass a false.  This will allow is to wait for the thread to 
 * finish when the user initiates it.
 *  @param isInternalCall Was this called internally
 */
-(void) close: (bool) isInternalCall;

/**
 * This will close the current connection if open.  If this is an 
 * internal call then we just set the isClosing flag.  If it is 
 * called with a false then we set the flag and wait for the thread
 * to complete.  At that point we clear the thread object and return.
 *  @param isInternalCall Was this called internally
 */
-(void) closeBase: (bool) isInternalCall;

/**
 * This will read the existing data from the device filling the byte array
 * object and returning the number of bytes in the array.
 *  @param buffer buffer to store data read
 *  @param length length of buffer
 *
 *  @return the number of bytes in the array
 */
-(int) innerRead: (unsigned char*) buffer bufferLength: (long) length;

/**
 * This will write the provided buffer to the device.
 *  @param buffer buffer containing data to be written
 *  @param length length of buffer
 */
-(void) innerWrite: (unsigned char*) buffer bufferLength: (long) length;

/**
 * This will return true if we have a connection ready to connect.
 */
-(bool) innerPending;

/**
 * This will return true if we have connected to a new client.
 */
-(bool) innerListen;


@end
