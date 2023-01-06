#include "FirebaseAppInterface.h"
#include <string>

namespace extension_ios_firebase {

    void init() {}

    value sendFirebaseAnalyticsEvent(value eventName, value jsonPayload) {}
    
    value setUserProperty(value propName, value propValue) {}
    
    value setCurrentScreen(value screenName, value screenClass) {}
    
    value setUserID(value userID) {}
    
	  value getRemoteConfig() {}
}
