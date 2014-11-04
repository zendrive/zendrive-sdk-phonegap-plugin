//
//  ZendriveCordovaPlugin.m
//

#import "ZendriveCordovaPlugin.h"
#import <ZendriveSDK/Zendrive.h>

@implementation ZendriveCordovaPlugin

- (void)setup:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString *applicationId = [command argumentAtIndex:0];
        NSString *userId = [command argumentAtIndex:1];

        NSError *error = nil;
        [Zendrive setupWithApplicationId:applicationId userId:userId
                                delegate:nil driverAttributes:nil error:&error];

        CDVPluginResult* pluginResult = nil;
        if(error == nil){
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }
        else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:error.localizedDescription];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

-(void)teardown:(CDVInvokedUrlCommand *)command{
    [Zendrive teardown];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)startDrive:(CDVInvokedUrlCommand*)command{
    [Zendrive startDrive:[command argumentAtIndex:0]];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)stopDrive:(CDVInvokedUrlCommand*)command{
    [Zendrive stopDrive];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)startSession:(CDVInvokedUrlCommand*)command{
    [Zendrive startSession:[command argumentAtIndex:0]];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)stopSession:(CDVInvokedUrlCommand*)command{
    [Zendrive stopSession];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
