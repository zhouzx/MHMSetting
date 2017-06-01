//
//  UtilsPlugin.m
//  mbppConvergedApp
//
//  Created by vndjqmia on 5/24/16.
//
//

#import "MHMSetting.h"

@implementation MHMSetting
/**
 * Launch App by URL
 *
 * @param CDVInvokedUrlCommand* command
 */
- (void) launchMapApp:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* pluginResult = nil;
        
        NSURL *addressUrl=[NSURL URLWithString:[[[NSString alloc] initWithFormat:@"http://maps.apple.com/?q=%@",[command.arguments[0]objectForKey:@"address"]] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
        [[UIApplication sharedApplication] openURL:addressUrl];
        if ([[UIApplication sharedApplication] canOpenURL:addressUrl]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"launch map success."];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

/**
 * call a phone number
 *
 * @param CDVInvokedUrlCommand* command
 */
- (void) callHandler:(CDVInvokedUrlCommand*)command{
    
    [self.commandDelegate runInBackground:^{
        
        if (command.arguments[0]&&command.arguments[1]) {
            NSString *phoneNumber = command.arguments[0];
            NSString *phoneURLString = nil;
            if ([command.arguments[1] isEqualToString:@"IBM Softphone"] ) {
                phoneURLString = [NSString stringWithFormat:@"stel:%@", phoneNumber];
            }else{
                phoneURLString = [NSString stringWithFormat:@"telprompt:%@", phoneNumber];
            }
            NSURL *phoneURL = [NSURL URLWithString:phoneURLString];
            [[UIApplication sharedApplication] openURL:phoneURL];
        }
    }];
}

@end
