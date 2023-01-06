#ifndef FIREBASE_APP_INTERFACE_H
#define FIREBASE_APP_INTERFACE_H

#include <hx/CFFI.h>


namespace extension_ios_firebase {

    value sendFirebaseAnalyticsEvent(value eventName, value jsonPayload);
    DEFINE_PRIM(sendFirebaseAnalyticsEvent, 2);
    
    value setUserProperty(value propName, value propValue);
    DEFINE_PRIM(setUserProperty, 2);
    
    value setCurrentScreen(value screenName, value screenClass);
    DEFINE_PRIM(setCurrentScreen, 2);
    
    value setUserID(value userID);
    DEFINE_PRIM(setUserID, 1);

    //static value getInstanceIDToken();
    //DEFINE_PRIM(getInstanceIDToken, 0);
	
	value getRemoteConfig();
    DEFINE_PRIM(getRemoteConfig, 0);

    void init();
}

#endif
