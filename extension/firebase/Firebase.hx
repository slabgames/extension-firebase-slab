package extension.firebase;

import openfl.Lib;


#if (openfl < "4.0.0")
import openfl.utils.JNI;
#else
import lime.system.JNI;
#end

import msignal.Signal;

class RemoteConfigCallback
{
	public var crossPromoModelCallback:Signal1<String>;
	
	public function new(listener:String->Void)
	{
		crossPromoModelCallback = new Signal1<String>();
		crossPromoModelCallback.add(listener);
	}

	public function setJSON(json:String):Void
	{
		crossPromoModelCallback.dispatch(json);
	}
}

class Firebase {

	
	public static function sendAnalyticsEvent (eventName:String, payload:String):Void {

		#if (ios || android)
			extension_firebase_send_analytics_event(eventName, payload);
		#else
			trace("sendAnalyticsEvent not implemented on this platform.");
		#end
	}

	public static function getInstanceIDToken ():String {

		#if (ios || android)
			return extension_firebase_get_instance_id_token();
		#else
			trace("getInstanceIDToken not implemented on this platform.");
		return null;
		#end
	}

	public static function setCurrentScreen (screenName:String, screenClass:String = null):Void {

		#if (ios || android)
			extension_firebase_set_current_screen(screenName, screenClass);
		#else
			trace("setCurrentScreen not implemented on this platform.");
		#end
	}

	public static function setUserProperty (propName:String, propValue:String = null):Void {

		#if (ios || android)
			extension_firebase_set_user_property(propName, propValue);
		#else
			trace("setUserProperty not implemented on this platform.");
		#end
	}

	public static function setUserID (userID:String):Void {

		#if (ios || android)
			extension_firebase_set_user_id(userID);
		#else
			trace("setUserID not implemented on this platform.");
		#end
	}
	
	public static function getRemoteConfig(callback:RemoteConfigCallback):Void {
		#if (android)
			return extension_firebase_get_remote_config(callback);
		#else
			trace("setUserID not implemented on this platform.");
			return null;
		#end
	}

	private static var initialized:Bool=false;
	private static var testingAds:Bool=false;
	private static var childDirected:Bool=true;

	////////////////////////////////////////////////////////////////////////////

	private static var __init:String->String->String->String->Bool->Bool->Dynamic->Void = function(bannerId:String, interstitialId:String, rewardedId:String, gravityMode:String, testingAds:Bool, tagForChildDirectedTreatment:Bool, callback:Dynamic){};
	private static var __showBanner:Void->Void = function(){};
	private static var __hideBanner:Void->Void = function(){};
	private static var __showInterstitial:Void->Bool = function(){ return false; };
	private static var __onResize:Void->Void = function(){};
	private static var __refresh:Void->Void = function(){};
	private static var __showRewarded:Void->Void = function(){};

	////////////////////////////////////////////////////////////////////////////

	private static var lastTimeInterstitial:Int = -60*1000;
	private static var displayCallsCounter:Int = 0;
	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	public static function showInterstitial(minInterval:Int=60, minCallsBeforeDisplay:Int=0):Bool {
		displayCallsCounter++;
		if( (Lib.getTimer()-lastTimeInterstitial)<(minInterval*1000) ) return false;
		if( minCallsBeforeDisplay > displayCallsCounter ) return false;
		displayCallsCounter = 0;
		lastTimeInterstitial = Lib.getTimer();
		try{
			return __showInterstitial();
		}catch(e:Dynamic){
			trace("ShowInterstitial Exception: "+e);
		}
		return false;
	}

	public static function showRewardedAd():Void
	{
		try {
			__showRewarded();
		} catch(e:Dynamic) {
			trace("showRewarded Exception: " + e);
		}
	}

	public static function tagForChildDirectedTreatment(){
		if ( childDirected ) return;
		if ( initialized ) {
			var msg:String;
			msg = "FATAL ERROR: If you want to set tagForChildDirectedTreatment, you must enable them before calling INIT!.\n";
			msg+= "Throwing an exception to avoid displaying ads withtou tagForChildDirectedTreatment.";
			trace(msg);
			throw msg;
			return;
		}
		childDirected = true;		
	}
	
	public static function enableTestingAds() {
		if ( testingAds ) return;
		if ( initialized ) {
			var msg:String;
			msg = "FATAL ERROR: If you want to enable Testing Ads, you must enable them before calling INIT!.\n";
			msg+= "Throwing an exception to avoid displaying read ads when you want testing ads.";
			trace(msg);
			throw msg;
			return;
		}
		testingAds = true;
	}

	public static function initAndroid(bannerId:String, interstitialId:String, rewardedId:String, gravityMode:GravityMode, testing:Bool, isChild:Bool){
		#if android
		if(initialized) return;
		initialized = true;
		try{
			// JNI METHOD LINKING
			__init = JNI.createStaticMethod("extension/java/Firebase", "init", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLorg/haxe/nme/HaxeObject;)V");
			__showBanner = JNI.createStaticMethod("extension/java/Firebase", "showBanner", "()V");
			__hideBanner = JNI.createStaticMethod("extension/java/Firebase", "hideBanner", "()V");
			__showInterstitial = JNI.createStaticMethod("extension/java/Firebase", "showInterstitial", "()Z");
			__onResize = JNI.createStaticMethod("extension/java/Firebase", "onResize", "()V");
			__showRewarded = JNI.createStaticMethod("extension/java/Firebase", "showRewarded","()V");

			var gravityString:String = (gravityMode==GravityMode.TOP)?"TOP":"BOTTOM";

			if (testing == true){
				bannerId = "ca-app-pub-3940256099942544/6300978111";
				interstitialId = "ca-app-pub-3940256099942544/1033173712";
				// interstitial_video = "ca-app-pub-3940256099942544/8691691433";
				rewardedId = "ca-app-pub-3940256099942544/5224354917";
			}
			__init(bannerId,interstitialId,rewardedId, gravityString ,testing, isChild, getInstance());
		}catch(e:Dynamic){
			trace("Android INIT Exception: "+e);
		}
		#end
	}
	
	public static function initIOS(bannerId:String, interstitialId:String, gravityMode:GravityMode){
		#if ios
		if(initialized) return;
		initialized = true;
		try{
			// CPP METHOD LINKING
			__init = cpp.Lib.load("adMobEx","admobex_init",6);
			__showBanner = cpp.Lib.load("adMobEx","admobex_banner_show",0);
			__hideBanner = cpp.Lib.load("adMobEx","admobex_banner_hide",0);
			__showInterstitial = cpp.Lib.load("adMobEx","admobex_interstitial_show",0);
			__refresh = cpp.Lib.load("adMobEx","admobex_banner_refresh",0);

			__init(bannerId,interstitialId,(gravityMode==GravityMode.TOP)?'TOP':'BOTTOM',testingAds, childDirected, getInstance()._onInterstitialEvent);
		}catch(e:Dynamic){
			trace("iOS INIT Exception: "+e);
		}
		#end
	}
	
	public static function showBanner() {
		try {
			__showBanner();
		} catch(e:Dynamic) {
			trace("ShowAd Exception: "+e);
		}
	}
	
	public static function hideBanner() {
		try {
			__hideBanner();
		} catch(e:Dynamic) {
			trace("HideAd Exception: "+e);
		}
	}
	
	public static function onResize() {
		try{
			__onResize();
		}catch(e:Dynamic){
			trace("onResize Exception: "+e);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	public static inline var LEAVING:String = "LEAVING";
	public static inline var FAILED:String = "FAILED";
	public static inline var CLOSED:String = "CLOSED";
	public static inline var DISPLAYING:String = "DISPLAYING";
	public static inline var LOADED:String = "LOADED";
	public static inline var LOADING:String = "LOADING";
	public static inline var REWARDED:String = "REWARDED";

	////////////////////////////////////////////////////////////////////////////

	public static var onInterstitialEvent:String->Void = null;
	public static var onRewardedEvent:String->Void = null;
	public static var onGetReward:String->Int->Void = null;	
	private static var instance:Firebase = null;

	private static function getInstance():Firebase{
		if (instance == null) instance = new Firebase();
		return instance;
	}

	////////////////////////////////////////////////////////////////////////////

	private function new(){}

	public function _onInterstitialEvent(event:String){
		if(onInterstitialEvent != null) onInterstitialEvent(event);
		else trace("Interstitial event: "+event+ " (assign AdMob.onInterstitialEvent to get this events and avoid this traces)");
	}

	public function _onRewardedEvent(event:String){
		if(onRewardedEvent != null) onRewardedEvent(event);
		else trace("Rewarded event: "+event+ " (assign AdMob.onRewardedEvent to get this events and avoid this traces)");
	}
	
	public function _onGetReward(rewardType:String, rewardAmount:Int)
	{
		if(onGetReward!=null)
			onGetReward(rewardType, rewardAmount);
		else trace("Get Reward not assigned");
	}

	#if (ios)
	private static var extension_firebase_send_analytics_event = Lib.load ("firebase", "sendFirebaseAnalyticsEvent", 2);
	private static var extension_firebase_set_current_screen = Lib.load ("firebase", "setCurrentScreen", 2);
	private static var extension_firebase_set_user_property = Lib.load ("firebase", "setUserProperty", 2);
	private static var extension_firebase_get_instance_id_token = Lib.load ("firebase", "getInstanceIDToken", 0);
	private static var extension_firebase_set_user_id = Lib.load ("firebase", "setUserID", 1);
	#end

	#if (android)
	private static var extension_firebase_send_analytics_event = JNI.createStaticMethod("extension.java.Firebase", "sendFirebaseAnalyticsEvent", "(Ljava/lang/String;Ljava/lang/String;)V");
	private static var extension_firebase_set_current_screen = JNI.createStaticMethod("extension.java.Firebase", "setCurrentScreen", "(Ljava/lang/String;Ljava/lang/String;)V");
	private static var extension_firebase_set_user_property = JNI.createStaticMethod("extension.java.Firebase", "setUserProperty", "(Ljava/lang/String;Ljava/lang/String;)V");
	private static var extension_firebase_get_instance_id_token = JNI.createStaticMethod("extension.java.Firebase", "getInstanceIDToken", "()Ljava/lang/String;");
	private static var extension_firebase_set_user_id = JNI.createStaticMethod("extension.java.Firebase", "setUserID", "(Ljava/lang/String;)V");
	private static var extension_firebase_get_remote_config = JNI.createStaticMethod("extension.java.Firebase", "getRemoteConfig", "(Lorg/haxe/nme/HaxeObject;)V");
	#end
	
	
}