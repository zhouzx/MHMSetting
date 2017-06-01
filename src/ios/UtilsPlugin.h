//
//  UtilsPlugin.h
//  mbppConvergedApp
//
//  Created by vndjqmia on 5/24/16.
//
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>
#import <NewRelicAgent/NewRelic.h>

@interface UtilsPlugin : CDVPlugin

- (void) launchMapApp:(CDVInvokedUrlCommand*)command;
- (void) callHandler:(CDVInvokedUrlCommand*)command;
- (void) smsHandler:(CDVInvokedUrlCommand*)command;
- (void) emailHandler:(CDVInvokedUrlCommand*)command;

- (void) newRelicRecordAccessTimesFacetUserID:(CDVInvokedUrlCommand*)command;
- (void) newRelicRecordWatchLogin:(CDVInvokedUrlCommand*)command;
- (void) newRelicRecordEvent:(CDVInvokedUrlCommand*)command;
- (void) newRelicSendFeedback:(CDVInvokedUrlCommand*)command;
@end
