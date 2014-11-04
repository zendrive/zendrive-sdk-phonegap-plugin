//
//  ZendriveCordovaPlugin.h
//

#import <Cordova/CDV.h>

@interface ZendriveCordovaPlugin : CDVPlugin

- (void)setup:(CDVInvokedUrlCommand*)command;
- (void)teardown:(CDVInvokedUrlCommand*)command;

- (void)startDrive:(CDVInvokedUrlCommand*)command;
- (void)stopDrive:(CDVInvokedUrlCommand*)command;

- (void)startSession:(CDVInvokedUrlCommand*)command;
- (void)stopSession:(CDVInvokedUrlCommand*)command;

@end