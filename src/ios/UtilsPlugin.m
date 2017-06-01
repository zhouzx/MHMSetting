//
//  UtilsPlugin.m
//  mbppConvergedApp
//
//  Created by vndjqmia on 5/24/16.
//
//

#import "UtilsPlugin.h"

@implementation UtilsPlugin
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

/**
 * sms
 *
 * @param CDVInvokedUrlCommand* command
 */
- (void) smsHandler:(CDVInvokedUrlCommand*)command{
    
    [self.commandDelegate runInBackground:^{
        
        if (command.arguments[0]) {
            NSString *phoneNumber = command.arguments[0];
            NSString *phoneURLString = [NSString stringWithFormat:@"sms:%@", phoneNumber];
            NSURL *phoneURL = [NSURL URLWithString:phoneURLString];
            [[UIApplication sharedApplication] openURL:phoneURL];
        }
    }];
}

/**
 * send email
 *
 * @param CDVInvokedUrlCommand* command
 */
- (void) emailHandler:(CDVInvokedUrlCommand*)command{
    
    [self.commandDelegate runInBackground:^{
        if (command.arguments[0]) {
            NSString *email = command.arguments[0];
            NSString *emailURLString = [NSString stringWithFormat:@"mailto:%@", email];
            emailURLString = [emailURLString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            NSURL *emailURL = [NSURL URLWithString:emailURLString];
            [[UIApplication sharedApplication] openURL:emailURL];
        }
    }];
}

- (void) newRelicRecordEvent:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString *event = [NSString stringWithString:[command.arguments[0] objectForKey:@"event"]];
    NSString *method = [NSString stringWithString:[command.arguments[0] objectForKey:@"method"]];
    NSNumber *state = [NSNumber numberWithFloat:[[command.arguments[0] objectForKey:@"state"] integerValue]];
    NSString *userID = [NSString stringWithString:[command.arguments[0] objectForKey:@"userID"]];
    
    BOOL eventRecorded = [NewRelic recordEvent:event
                                    attributes:@{@"method": method,@"UserID": userID, @"state":state}];
    //    NSLog(@"记录%@--event:%@--method:%@--state:%@",eventRecorded ?@"成功":@"失败", event,method,state);
    if (eventRecorded) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"record event success!"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/**
 *  According to userID， record user access times for different method
 *
 *  @param command:[userID，event]
 */
- (void) newRelicRecordAccessTimesFacetUserID:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString *event = [NSString stringWithString:[command.arguments[0] objectForKey:@"event"]];
    NSString *userID = [NSString stringWithString:[command.arguments[0] objectForKey:@"userID"]];
    BOOL eventRecorded = [NewRelic recordEvent:@"AccessTimes"
                                    attributes:@{@"UserID": userID,@"event":event}];
    if (eventRecorded) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) newRelicRecordWatchLogin:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString *event = [NSString stringWithString:[command.arguments[0] objectForKey:@"event"]];
    NSString *userID = [NSString stringWithString:[command.arguments[0] objectForKey:@"userID"]];
    NSString *watchOsVersion = [NSString stringWithString:[command.arguments[0] objectForKey:@"watchOsVersion"]];
    BOOL eventRecorded = [NewRelic recordEvent:@"AccessTimes"
                                    attributes:@{@"UserID": userID,@"event":event,@"watchOsVersion":watchOsVersion}];
    if (eventRecorded) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/**
 *  send feedback by new relic
 *
 *  @param command [userID,category,content]
 */
- (void) newRelicSendFeedback:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString *userID = [NSString stringWithString:[command.arguments[0] objectForKey:@"userID"]];
    NSString *content = [NSString stringWithString:[command.arguments[0] objectForKey:@"content"]];
    NSString *category = [NSString stringWithString:[command.arguments[0] objectForKey:@"category"]];
    BOOL eventRecorded = [NewRelic recordEvent:@"feedback"
                                    attributes:@{@"UserID": userID,@"feedbackCategory":category,@"feedbackContent":content}];
    if (eventRecorded) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
