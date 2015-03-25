//
//  ZendriveCordovaPlugin.m
//

#import "ZendriveCordovaPlugin.h"
#import <ZendriveSDK/Zendrive.h>

static NSString * const autoDriveDetectionOn = @"AUTO_ON";
static NSString * const autoDriveDetectionOff = @"AUTO_OFF";

@implementation ZendriveCordovaPlugin

- (void)setup:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString *applicationKey = [command argumentAtIndex:0];
        NSString *driverId = [command argumentAtIndex:1];

        ZendriveDriveDetectionMode mode =
        [self getDriveDetectionModeFromString:
         [command argumentAtIndex:2 withDefault:autoDriveDetectionOn]];

        if (mode < 0) {
            CDVPluginResult* pluginResult =
            [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                              messageAsString:@"Invalid string sent as drive "
             @"detection mode"];
            [self.commandDelegate sendPluginResult:pluginResult
                                        callbackId:command.callbackId];
            return;
        }

        ZendriveConfiguration *configuration = [[ZendriveConfiguration alloc] init];
        configuration.applicationKey = applicationKey;
        configuration.driverId = driverId;
        configuration.operationMode = ZendriveOperationModeDriverAnalytics;
        configuration.driveDetectionMode = mode;

        [Zendrive
         setupWithConfiguration:configuration
         delegate:nil
         completionHandler:^(BOOL success, NSError *error) {
             CDVPluginResult* pluginResult = nil;
             if(error == nil){
                 pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
             }
             else {
                 pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                  messageAsString:error.localizedFailureReason];
             }
             [self.commandDelegate sendPluginResult:pluginResult
                                         callbackId:command.callbackId];
         }];
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
    NSString *trackingId = [command argumentAtIndex:0];
    [Zendrive stopDrive:trackingId];
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

- (void)setDriveDetectionMode:(CDVInvokedUrlCommand *)command{
    CDVPluginResult *pluginResult;
    NSString *modeString = [command argumentAtIndex:0];
    ZendriveDriveDetectionMode mode = [self getDriveDetectionModeFromString:modeString];

    if (mode < 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:@"Invalid string sent as drive "
                        @"detection mode"];
    } else {
        [Zendrive setDriveDetectionMode:mode];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (ZendriveDriveDetectionMode)getDriveDetectionModeFromString:(NSString *)str {
    if ([str isEqualToString:autoDriveDetectionOff]) {
        return ZendriveDriveDetectionModeAutoOFF;
    } else if ([str isEqualToString:autoDriveDetectionOn]) {
        return ZendriveDriveDetectionModeAutoON;
    }
    return -1;
}

@end
