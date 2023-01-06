#include "FirebaseAppDelegate.h"

#import <objc/runtime.h>
#import <UIKit/UIKit.h>
#import <Firebase.h>

// MESSAGING, disabled
/*
#import <FirebaseMessaging/FirebaseMessaging.h>

// Implement UNUserNotificationCenterDelegate to receive display notification via APNS for devices running iOS 10 and above.
#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
#import <UserNotifications/UserNotifications.h>
@interface FirebaseAppDelegate () <UNUserNotificationCenterDelegate, FIRMessagingDelegate>
@end
#endif
*/


@interface NMEAppDelegate : NSObject <UIApplicationDelegate>
@end

// Copied from Apple's header in case it is missing in some cases (e.g. pre-Xcode 8 builds).
#ifndef NSFoundationVersionNumber_iOS_9_x_Max
#define NSFoundationVersionNumber_iOS_9_x_Max 1299
#endif

@implementation NMEAppDelegate(FirebaseAppDelegate)
/*
    -(BOOL)application:(UIApplication *)application willFinishLaunchingWithOptions:(NSDictionary *) launchOptions
    {
        [FIRApp configure];
        NSLog(@"willFinishLaunchingWithOptions Firebase");
        return YES;
    }
 */
@end

@implementation FirebaseAppDelegate

//NSString *const kGCMMessageIDKey = @"gcm.message_id";
//NSString* firebaseInstanceIdToken = @"";

// FIRRemoteConfig *remoteConfig;

+ (instancetype)sharedInstance
{
  static FirebaseAppDelegate *_sharedInstance;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    _sharedInstance = [[self alloc] _init];
  });
  return _sharedInstance;
}

- (instancetype)_init
{
  NSLog(@"FirebaseAppDelegate: _init");
  return self;
}

- (instancetype)init
{
  return nil;
}

// MESSAGING, disabled
/*
-(BOOL)application:(UIApplication *)application willFinishLaunchingWithOptions:(NSDictionary *) launchOptions
{
    NSLog(@"FirebaseAppDelegate: willFinishLaunchingWithOptions");
    [FIRApp configure];
    [FIRMessaging messaging].delegate = self;

    // Push Notification Permission
    // This code will request permission from the user to accept Push Notifications on their device.
    // It is displayed when the app is launched. We will want some better control around when and how we ask users to enable push
    if (floor(NSFoundationVersionNumber) <= NSFoundationVersionNumber_iOS_9_x_Max) {
        NSLog(@"FirebaseAppDelegate: Requesting <= iOS 9 Notifications");
        UIUserNotificationType allNotificationTypes = (UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge);
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:allNotificationTypes categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:settings];
    } else {
        // iOS 10 or later
        NSLog(@"FirebaseAppDelegate: ELSE Requesting >= iOS 10 Notifications");
        #if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
            // For iOS 10 display notification (sent via APNS)
            NSLog(@"FirebaseAppDelegate: Requesting >= iOS 10 Notifications");
            [UNUserNotificationCenter currentNotificationCenter].delegate = self;
            UNAuthorizationOptions authOptions = UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
            [[UNUserNotificationCenter currentNotificationCenter] requestAuthorizationWithOptions:authOptions completionHandler:^(BOOL granted, NSError * _Nullable error){
                NSLog(@"FirebaseAppDelegate: requestAuthorizationWithOptions granted = %d error = %@", granted, error);
            }];
        #endif
    }
    [[UIApplication sharedApplication] registerForRemoteNotifications];
	
	self.remoteConfig = [FIRRemoteConfig remoteConfig];
	FIRRemoteConfigSettings *remoteConfigSettings = [[FIRRemoteConfigSettings alloc] initWithDeveloperModeEnabled:YES];
	self.remoteConfig.configSettings = remoteConfigSettings;
	[self.remoteConfig setDefaultsFromPlistFileName:@"RemoteConfigDefaults"];

    // Token may be null if it has not been generated yet.
    NSLog(@"FirebaseAppDelegate: FCM registration token: %@", [FIRMessaging messaging].FCMToken);

    return YES;
}

- (void)messaging:(nonnull FIRMessaging *)messaging didRefreshRegistrationToken:(nonnull NSString *)fcmToken {
    // Note that this callback will be fired everytime a new token is generated, including the first
    // time. So if you need to retrieve the token as soon as it is available this is where that
    // should be done.
    
    NSLog(@"FirebaseAppDelegate: didRefreshRegistrationToken FCM registration token: %@", fcmToken);
    
    // If necessary send token to application server.
    
    firebaseInstanceIdToken = fcmToken;
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"FirebaseAppDelegate: Unable to register for remote notifications: %@", error);
}

// This function is added here only for debugging purposes, and can be removed if swizzling is enabled.
// If swizzling is disabled then this function must be implemented so that the APNs device token can be paired to
// the FCM registration token.
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSLog(@"FirebaseAppDelegate: didRegisterForRemoteNotificationsWithDeviceToken token: %@", deviceToken);
    
    
    firebaseInstanceIdToken = [[NSString alloc] initWithData:deviceToken encoding:NSUTF8StringEncoding];
    
    // With swizzling disabled you must set the APNs device token here.
    // [Messaging messaging].APNSToken = deviceToken;
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    // If you are receiving a notification message while your app is in the background,
    // this callback will not be fired till the user taps on the notification launching the application.
    
    // Print message ID.
    if (userInfo[kGCMMessageIDKey]) {
        NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
    }
    
    // Print full message.
    NSLog(@"%@", userInfo);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    // If you are receiving a notification message while your app is in the background,
    // this callback will not be fired till the user taps on the notification launching the application.
    
    // Print message ID.
    if (userInfo[kGCMMessageIDKey]) {
        NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
    }
    
    // Print full message.
    NSLog(@"%@", userInfo);
    
    completionHandler(UIBackgroundFetchResultNewData);
}

- (NSString*)getInstanceIDToken {
    return firebaseInstanceIdToken;
}

*/

- (BOOL)setUserID:(NSString *)userID
{
    NSLog(@"FirebaseAppDelegate: setUserID id= %@", userID);
    
    [FIRAnalytics setUserID:userID];
    return YES;
}

- (BOOL)setCurrentScreen:(NSString *)screenName screenClass:(NSString *)screenClass
{
    NSLog(@"FirebaseAppDelegate: setScreen name= %@, class= %@", screenName, screenClass);
    
    [FIRAnalytics setScreenName:screenName screenClass:screenClass];
    return YES;
}

- (BOOL)setUserProperty:(NSString *)propName propValue:(NSString *)propValue
{
    NSLog(@"FirebaseAppDelegate: setUserProperty key= %@, val= %@", propName, propValue);
    
    [FIRAnalytics setUserPropertyString:propValue forName:propName];
    return YES;
}

- (BOOL)sendFirebaseAnalyticsEvent:(NSString *)eventName jsonPayload:(NSString *)jsonPayload
{
    NSLog(@"FirebaseAppDelegate: sendFirebaseAnalyticsEvent name= %@, parameter= %@", eventName, jsonPayload);

    NSData * jsonData = [jsonPayload dataUsingEncoding:NSUTF8StringEncoding];
    NSError * error = nil;
    NSDictionary * parsedData = [NSJSONSerialization JSONObjectWithData:jsonData options:kNilOptions error:&error];

    [FIRAnalytics logEventWithName:eventName parameters:parsedData];
    return YES;
}

- (void)getRemoteConfig
{
    /*long expirationDuration = 3600;
    // If your app is using developer mode, expirationDuration is set to 0, so each fetch will
    // retrieve values from the Remote Config service.
    if (self.remoteConfig.configSettings.isDeveloperModeEnabled) {
        expirationDuration = 0;
    }

    [self.remoteConfig fetchWithExpirationDuration:expirationDuration completionHandler:^(FIRRemoteConfigFetchStatus status, NSError *error) {
        if (status == FIRRemoteConfigFetchStatusSuccess)
		{
            [self.remoteConfig activateFetched];
        }
		else
		{
        }
        [self displayWelcome];
    }];*/
}

@end
