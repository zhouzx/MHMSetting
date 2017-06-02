//
//  UtilsPlugin.m
//  mbppConvergedApp
//
//  Created by vndjqmia on 5/24/16.
//
//

#import "MHMSetting.h"

@implementation MHMSetting


-(void)openPreferencesSettings:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    
    if (&UIApplicationOpenSettingsURLString != NULL) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
        NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        [[UIApplication sharedApplication] openURL:url];
    }
    else {
      // Present some dialog telling the user to open the settings app.
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:NO];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

@end
