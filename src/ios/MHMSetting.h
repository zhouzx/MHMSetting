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

@interface MHMSetting : CDVPlugin

- (void) launchMapApp:(CDVInvokedUrlCommand*)command;
- (void) callHandler:(CDVInvokedUrlCommand*)command;

@end
