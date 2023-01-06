package extension.java;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.*;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.installations.FirebaseInstallations;
// import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
// import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
// import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.initialization.InitializationStatus;

// import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
// import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;
import java.util.Queue;
import java.util.List;
import java.util.Arrays;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect.OnControlStatusChangeListener;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.provider.Settings.Secure;
import java.security.MessageDigest;

import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.pm.PackageManager.NameNotFoundException;
import java.lang.NullPointerException;




public class Firebase extends Extension {
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public InterstitialAd interstitial=null;
    public AdView banner = null;
    public RewardedAd rewardedAd = null;
    public RelativeLayout rl = null;
    public AdRequest adReq = null;
    private static FirebaseAnalytics mFirebaseAnalytics;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private static Boolean failInterstitial=false;
    private static Boolean loadingInterstitial=false;
    private static Boolean isRewardedAvailable=false;
    private static String interstitialId=null;

    private static Boolean failBanner=false;
    private static Boolean loadingBanner=false;
    private static Boolean mustBeShowingBanner=false;
    private static String bannerId=null;
    private static String rewardedId=null;

    private static Firebase instance=null;
    private static Boolean testingAds=false;
    private static Boolean tagForChildDirectedTreatment=false;
    private static int gravity=Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

    private static HaxeObject callback=null;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String LEAVING = "LEAVING";
    public static final String FAILED = "FAILED";
    public static final String CLOSED = "CLOSED";
    public static final String DISPLAYING = "DISPLAYING";
    public static final String LOADED = "LOADED";
    public static final String LOADING = "LOADING";


    static final String TAG = "FIREBASE-EXTENSION";

    public static Firebase getInstance(){
        if(instance==null && bannerId!=null) instance = new Firebase();
        if(bannerId==null){
            Log.e("Firebase","You tried to get Instance without calling INIT first on Firebase class!");
        }
        return instance;
    }

    // private static FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static Map<String, String> getPayloadFromJson(String jsonString) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> payload = new Gson().fromJson(jsonString, type);
        return payload;
    }


    private static Bundle getFirebaseAnalyticsBundleFromJson(String jsonString) {
        Map<String, String> payloadMap = getPayloadFromJson(jsonString);

        Bundle payloadBundle = new Bundle();
        for (Map.Entry<String, String> entry : payloadMap.entrySet()) {
            payloadBundle.putString(entry.getKey(), entry.getValue());
        }

        return payloadBundle;
    }

    // Get token
    /*
    * The registration token may change when:
    * The app deletes Instance ID
    * The app is restored on a new device
    * The user uninstalls/reinstall the app
    * The user clears app data.*/
    // public static String getInstanceIDToken()
    // {
    //     final String token = FirebaseInstallations.getInstance().getId();
    //     Log.d(TAG, "getInstanceId success: " + token);

    //     return token;

        
    //     // FirebaseInstanceId.getInstance().getInstanceId()
    //     // .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
    //     //     @Override
    //     //     public void onComplete(@NonNull Task<InstanceIdResult> task) {
    //     //         if (!task.isSuccessful()) {
    //     //             Log.d(TAG, "getInstanceId failed", task.getException());
    //     //             return;
    //     //         }

    //     //         Log.d(TAG, "getInstanceId success", task.getResult().getToken());
    //     //     }
    //     // });
        
    // }


    public static void sendFirebaseAnalyticsEvent(String eventName, String jsonPayload) {
        Log.d(TAG, "Firebase.java: sendFirebaseAnalyticsEvent name= " + eventName + ", payload= " + jsonPayload);

        //Application mainApp = Extension.mainActivity.getApplication();
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(mainApp);

        Bundle payloadBundle = getFirebaseAnalyticsBundleFromJson(jsonPayload);
        mFirebaseAnalytics.logEvent(eventName, payloadBundle);
    }

    public static void setUserProperty(String propName, String propValue) {
        Log.d(TAG, "Firebase.java: setUserProperty name= " + propName + ", value= " + propValue);

        //Application mainApp = Extension.mainActivity.getApplication();
        //FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(mainApp);


        mFirebaseAnalytics.setUserProperty(propName, propValue);
    }

    public static void setCurrentScreen(String screenName, String screenClass) {
        Log.d(TAG, "Firebase.java: setScreen name= " + screenName + ", class= " + screenClass);

        //Application mainApp = Extension.mainActivity.getApplication();
        //FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(mainApp);

        mFirebaseAnalytics.setCurrentScreen(Extension.mainActivity, screenName, screenClass);
    }

    public static void setUserID(String userID) {
        Log.d(TAG, "Firebase.java: setUserID id= " + userID);

        //Application mainApp = Extension.mainActivity.getApplication();
        //FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(mainApp);


        mFirebaseAnalytics.setUserId(userID);
    }
	
	
	public static void getRemoteConfig(HaxeObject callback) {
    }
		
		

    /**
     * Called when the activity is starting.
     */
    public void onCreate (Bundle savedInstanceState) {

        Log.d(TAG, "Firebase extension onCreate ");

        
        Extension.mainActivity.runOnUiThread(new Runnable() {
            public void run() { 


                try {
	                Application mainApp = Extension.mainActivity.getApplication();
	                ApplicationInfo app = mainContext.getPackageManager().getApplicationInfo(mainContext.getPackageName(),PackageManager.GET_META_DATA);
	                Bundle bundle = app.metaData;

	                FirebaseApp.initializeApp(Extension.mainContext);
                    Log.d(TAG, "FirebaseApp initialized ");
	                mFirebaseAnalytics = FirebaseAnalytics.getInstance(mainApp);
	                // MobileAds.initialize(Extension.mainContext,bundle.getString("com.google.android.gms.ads.APPLICATION_ID"));
                    

                


                } 
                catch (NameNotFoundException e) {
                    e.printStackTrace();
                } 
                catch (NullPointerException e) {
                    e.printStackTrace();         
                }

                // Use an activity context to get the rewarded video instance.
                
                
                
             }
        }); 
        
        

        // Handle possible data accompanying notification message.
        Intent intent = null;
		try {
			PackageManager pm = mainContext.getPackageManager();
			if(pm != null) {
				String packageName = mainContext.getPackageName();
				intent = pm.getLaunchIntentForPackage(packageName);
				intent.addCategory(Intent.CATEGORY_LAUNCHER); // Should already be set, but just in case
			}
		} catch (Exception e) {
			Log.d(TAG, "Failed to get application launch intent");
		}
        
        Bundle intentBundle = null;
        if (intent != null && intent.getExtras() != null) {
            intentBundle = intent.getExtras();
            for (String key : intentBundle.keySet()) {
                Object value = intentBundle.get(key);
                Log.d(TAG, "Launch intent Key: " + key + " Value: " + value);
            }
        }

        // subscribe for new messages
        // FirebaseMessaging.getInstance().subscribeToTopic("news")
        // .addOnCompleteListener(new OnCompleteListener<Void>() {
        //     @Override
        //     public void onComplete(@NonNull Task<Void> task) {
        //         String msg = "Successful Subscribed messages from Firebase";
        //         if (!task.isSuccessful()) {
        //             msg = "Failed Subscribed messages from Firebase";
        //         }
        //         Log.d(TAG, msg);
        //     }
        // });


        // Firebase.getInstanceIDToken();
		
		/*
		mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                //.setDeveloperModeEnabled(BuildConfig.DEBUG)
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        //mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
		*/
    }


    /**
     * Perform any final cleanup before an activity is destroyed.
     */
    public void onDestroy () {

        rewardedAd = null;
        interstitial=null;

        if (banner != null)
            banner.destroy();
        adReq = null;
        rl = null;
        instance = null;
        banner = null;
        
        super.onDestroy();
    }


    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.
     */
    public void onPause () {

        if ( banner != null)
            banner.pause();
        super.onPause();
    }


    /**
     * Called after {@link #onStop} when the current activity is being
     * re-displayed to the user (the user has navigated back to it).
     */
    public void onRestart () {



    }


    /**
     * Called after {@link #onRestart}, or {@link #onPause}, for your activity
     * to start interacting with the user.
     */
    public void onResume () {

        super.onResume();

         // Resume the AdView.
        if ( banner != null)
            banner.resume();

    }


    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when
     * the activity had been stopped, but is now again being displayed to the
     * user.
     */
    public void onStart () {

        Log.d(TAG, "Firebase.java: onStart ");

    }


    /**
     * Called when the activity is no longer visible to the user, because
     * another activity has been resumed and is covering this one.
     */
    public void onStop () {



    }

    private void initInterstitial() {
        reloadInterstitial();

        Log.d(TAG, "Firebase.java: init Interstitial admob ");
    }

    public static void init(final String bannerId, final String interstitialId, final String rewardedId, String gravityMode, final boolean testingAds, final boolean tagForChildDirectedTreatment, HaxeObject callback){
        Firebase.interstitialId=interstitialId;
        Firebase.bannerId = bannerId;
        Firebase.rewardedId = rewardedId;
        Firebase.testingAds=testingAds;
        Firebase.callback=callback;
        Firebase.tagForChildDirectedTreatment=tagForChildDirectedTreatment;
        if(gravityMode.equals("TOP")){
            Firebase.gravity=Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        }
        
        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                
                Firebase.getInstance();
                
                MobileAds.initialize(mainActivity, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                            RequestConfiguration.Builder requestConfigurationBuilder = MobileAds.getRequestConfiguration().toBuilder();

                            if(testingAds){
                                        String android_id = Secure.getString(mainActivity.getContentResolver(), Secure.ANDROID_ID);
                                        String deviceId = md5(android_id).toUpperCase();
                                        Log.d(TAG,"DEVICE ID: "+deviceId);
                                        // builder.addTestDevice(deviceId);
                                        List<String> devicesIds = Arrays.asList(deviceId);
                                        requestConfigurationBuilder.setTestDeviceIds(devicesIds);
                                        requestConfigurationBuilder.setTestDeviceIds(Arrays.asList("7FFFF697C37DBA3561C4B17C6BBC34E7"));
                                    }

                            
                            if(tagForChildDirectedTreatment){
                                Log.d(TAG,"Enabling COPPA support.");
                                requestConfigurationBuilder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G);
                                requestConfigurationBuilder.setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);

                            }
                            RequestConfiguration requestConfiguration = requestConfigurationBuilder.build();
                            MobileAds.setRequestConfiguration(requestConfiguration);
                             
                            if(bannerId!=null ){
                                Firebase.getInstance().reinitBanner();
                            }

                            if ( interstitialId != null ){
                                Firebase.getInstance().initInterstitial();
                            }

                            if (rewardedId != null) {
                                Firebase.getInstance().createAndLoadRewardedAd();

                            }
                        }


                    });
                

                
                
            }
        }); 


        
    }

    private static void reportInterstitialEvent(final String event){
        if(callback == null) return;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                callback.call1("_onInterstitialEvent",event);
            }
        });
    }


    private static void reportRewardedEvent(final String event) {
        if(callback==null) return;

        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                callback.call1("_onRewardedEvent",event);
            }
        });
    }

    private static void getReward(final String rewardType, final int rewardAmount)
    {
        if(callback==null) return;

        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                callback.call2("_onGetReward",rewardType, rewardAmount);
            }
        });
    }

    public static void showBanner() {
        if(bannerId==null) return;
        mustBeShowingBanner=true;
        if(failBanner){
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (getInstance()== null) return;

                    getInstance().reloadBanner();}
            });
            return;
        }
        Log.d(TAG,"Show Banner");
        
        if (getInstance() != null ) {
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    getInstance().reshowBanner();
                }
            });
        }
    }

    public void reshowBanner() {
        if (rl == null || banner == null) return;
        rl.removeView(getInstance().banner);
        rl.addView(getInstance().banner);
        rl.bringToFront();
        banner.setVisibility(View.VISIBLE);
    }


    public static void hideBanner() {
        if(bannerId=="") return;
        mustBeShowingBanner=false;
        Log.d(TAG,"Hide Banner");
        if (getInstance().banner == null ) { 
            return;
        }

        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                getInstance().banner.setVisibility(View.INVISIBLE); }
        });
        
    }

    public static void onResize(){
        Log.d(TAG,"On Resize");

        if (getInstance()== null) return;

        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                if (Firebase.getInstance().bannerId != null )
                    Firebase.getInstance().reinitBanner(); 
            }
        });
    }

    private void reinitBanner(){
        if(loadingBanner) return;   
        if(banner==null){ // if this is the first time we call this function
            rl = new RelativeLayout(mainActivity);
            rl.setGravity(gravity);
        } else {
            ViewGroup parent = (ViewGroup) rl.getParent();
            parent.removeView(rl);
            rl.removeView(banner);
            banner.destroy();
        }

        banner = new AdView(mainActivity);
        banner.setAdUnitId(bannerId);
        banner.setAdSize(AdSize.BANNER);
        banner.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Firebase.getInstance().loadingBanner=false;  
                Log.d(TAG,"Received Banner OK!");
                if(Firebase.getInstance().mustBeShowingBanner){
                    Firebase.getInstance().showBanner();
                }else{
                    Firebase.getInstance().hideBanner();
                }               
            }
            public void onAdFailedToLoad(int errorcode) {
                Firebase.getInstance().loadingBanner=false;
                Firebase.getInstance().failBanner=true;
                Log.d(TAG,"Fail to get Banner: "+errorcode);              
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);                  
        mainActivity.addContentView(rl, params);
        rl.addView(banner);
        rl.bringToFront();
        reloadBanner();
    }

    public static boolean showInterstitial() {
        Log.d("Firebase","Show Interstitial: Begins");
        if(loadingInterstitial) return false;
        if(failInterstitial){
            // mainActivity.runOnUiThread(new Runnable() {
            //     public void run() { getInstance().reloadInterstitial();}
            // }); 
            getInstance().reloadInterstitial();
            Log.d(TAG,"Show Interstitial: Interstitial not loaded... reloading.");
            return false;
        }

        if(interstitialId==null) {
            Log.d(TAG,"Show Interstitial: InterstitialID is empty... ignoring.");
            return false;
        }

        if (Firebase.getInstance().interstitial != null ){
            mainActivity.runOnUiThread(new Runnable() {
                public void run() { 
                    getInstance().interstitial.show(mainActivity);
                }
            });
            getInstance().reloadInterstitial();
            Log.d(TAG,"Show Interstitial: Compelte.");
        }
        
        return true;
    }

    public void reloadInterstitial(){
        if(interstitialId==null) return;
        if(loadingInterstitial) return;
        Log.d(TAG,"Reload Interstitial");
        reportInterstitialEvent(Firebase.LOADING);
        loadingInterstitial=true;
        failInterstitial=false;
        mainActivity.runOnUiThread(new Runnable() {
                public void run() { 
                    InterstitialAd.load(mainActivity, interstitialId, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                 @Override
                 public void onAdLoaded(@NonNull InterstitialAd ad) {
                    Firebase.getInstance().loadingInterstitial=false;
                    Firebase.getInstance().failInterstitial=false;
                    reportInterstitialEvent(Firebase.LOADED);
                    Log.d(TAG,"Received Interstitial!");
                    interstitial = ad;
                 }

                 @Override
                 public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    Firebase.getInstance().loadingInterstitial=false;    
                    Firebase.getInstance().failInterstitial=true;
                    reportInterstitialEvent(Firebase.FAILED);
                    interstitial = null;
                    Log.d(TAG,"Fail to get Interstitial: "+adError.toString());
                 }
             });
                }
        }); 
        
        
    }

    private void reloadBanner(){
        if(bannerId==null) return;
        if(loadingBanner) return;
        if (banner == null) return;
        Log.d(TAG,"Reload Banner");
        loadingBanner=true;
        failBanner=false;
        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                banner.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void createAndLoadRewardedAd() {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() { 
                RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedAd adT) {
                        // Ad successfully loaded.
                        isRewardedAvailable = true;
                        Log.d(TAG, "onRewardedAdLoaded:Received rewarded ad");
                        rewardedAd = adT;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Ad failed to load.
                        isRewardedAvailable = false;
                        Log.d(TAG, "onRewardedAdFailedToLoad " + adError.toString());
                        Firebase.getInstance().createAndLoadRewardedAd();
                    }
                };
                RewardedAd.load(mainActivity, rewardedId, new AdRequest.Builder().build(), adLoadCallback);
            }
        });
    }


    public static void showRewarded()
    {
    	Log.d(TAG, "Calling rewarded video.");
    	mainActivity.runOnUiThread(new Runnable() {
    		public void run() { 
		        if (isRewardedAvailable==true) {
		            OnUserEarnedRewardListener adCallback = new OnUserEarnedRewardListener() {

		                @Override
		                public void onUserEarnedReward(@NonNull RewardItem reward) {
		                    // User earned reward.
		                    Firebase.getInstance().getReward(reward.getType(), reward.getAmount());
		                    Toast.makeText(mainContext, "onRewarded! currency: " + reward.getType() + "  amount: " +reward.getAmount(), Toast.LENGTH_SHORT).show();
		                    Log.d(TAG, "onUserEarnedReward");
		                }
		            };
		            getInstance().rewardedAd.show(mainActivity, adCallback);
		            Log.d(TAG, "Showing rewarded video.");
		        } else {
		            Log.d(TAG, "The rewarded ad wasn't loaded yet.");
		            reportRewardedEvent("Rewarded Video not available at the moment");
		        }
	    	}
	    });
    }



    private static String md5(String s)  {
        MessageDigest digest;
        try  {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(),0,s.length());
            String hexDigest = new java.math.BigInteger(1, digest.digest()).toString(16);
            if (hexDigest.length() >= 32) return hexDigest;
            else return "00000000000000000000000000000000".substring(hexDigest.length()) + hexDigest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}