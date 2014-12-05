//
//  ZendriveCordovaPlugin.m
//

#import "ZendriveCordovaPlugin.h"
#import <ZendriveSDK/Zendrive.h>

@implementation ZendriveCordovaPlugin

- (void)setup:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString *applicationKey = [command argumentAtIndex:0];
        NSString *driverId = [command argumentAtIndex:1];

        ZendriveConfiguration *configuration = [[ZendriveConfiguration alloc] init];
        configuration.applicationKey = applicationKey;
        configuration.driverId = driverId;
        configuration.operationMode = ZendriveOperationModeDriverAnalytics;

        NSError *error = nil;
        [Zendrive setupWithConfiguration:configuration delegate:nil error:&error];

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
    NSString *trackingId = [command argumentAtIndex:0];
    [Zendrive startDrive:trackingId];
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
