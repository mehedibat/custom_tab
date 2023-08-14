package com.example.custom_tabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;
import android.widget.Toast;

import com.example.custom_tabs.service.TimerService;
import com.example.custom_tabs.utils.PostMessageBroadcastReceiver;
import com.example.custom_tabs.utils.ServiceUtil;
import com.google.androidbrowserhelper.trusted.TwaLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Timer;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
private static final String TAG = MainActivity.class.getSimpleName();
    //private final String webAppUrl = "https://192.168.30.193/";
    private final String webAppUrl = "https://stage-insightdb.ba-systems.com/";
    private CustomTabsSession mSession;
    private CustomTabsClient mClient;

    private boolean mValidated = false;
    boolean isOnPauseEnable = false;

    @Override
    protected void onResume() {
        super.onResume();
        isOnPauseEnable = true;
        MyApp.getInstance().setActivityVisible(true);
        Log.i(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (isOnPauseEnable) {
//            isOnPauseEnable = false;
//            return;
//        }
        //MyApp.getInstance().setActivityVisible(false);
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");


        //MyApp.getInstance().setActivityVisible(false);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        //MyApp.getInstance().setActivityVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG,"onCreate Kobi");
//        CustomTabsIntent intent = new CustomTabsIntent.Builder()
//                .setUrlBarHidingEnabled(true)
//                .setShowTitle(true)
//                .build();
//        intent.launchUrl(MainActivity.this, Uri.parse(webAppUrl));



        bindCustomTabsService();
    }


    private void lunchTrustWebService() {
        new TrustedWebActivityIntentBuilder(Uri.parse(webAppUrl)).build(mSession)
                .launchTrustedWebActivity(MainActivity.this);
//        TwaLauncher twaLauncher = new TwaLauncher(this);
//        twaLauncher.launch(Uri.parse(webAppUrl));
    }

    @Override
    protected void onStart() {
        super.onStart();
        ServiceUtil.startForegroundService(this, TimerService.class);
    }


    private final CustomTabsCallback customTabsCallback =
            new CustomTabsCallback() {
                @Override
                public void onPostMessage(@NonNull String message, @Nullable Bundle extras) {
                    super.onPostMessage(message, extras);
                    if (message.contains("ACK")) {
                        return;
                    }
                    Log.i(TAG, "Got message: " + message);
                }


                // Listens for navigation, requests the postMessage channel when one completes.
                @Override
                public void onNavigationEvent(int navigationEvent, @Nullable Bundle extras) {
                    if (navigationEvent != NAVIGATION_FINISHED) {
                        return;
                    }

                    if (!mValidated) {
                        Log.i(TAG, "Not starting PostMessage as validation didn't succeed.");
                    }

                }


                @Override
                public void extraCallback(@NonNull String callbackName, @Nullable Bundle args) {
                    super.extraCallback(callbackName, args);
                    // initial 'onWarmupCompleted','onBottomBarScrollStateChanged' , 'onVerticalScrollEvent'
                    // When pause then call 'onVerticalScrollEvent'
                    if ("onVerticalScrollEvent".equals(callbackName)) {
                        System.out.println("callbackName  => onVerticalScrollEvent");
                        //MyApp.getInstance().setActivityVisible(false);
                    }
                }

            };


    private void bindCustomTabsService() {
        String packageName = CustomTabsClient.getPackageName(this, null);
        CustomTabsClient.bindCustomTabsService(this, packageName,
                new CustomTabsServiceConnection() {
                    @Override
                    public void onCustomTabsServiceConnected(@NonNull ComponentName name,
                                                             @NonNull CustomTabsClient client) {
                        mClient = client;

                        // Note: validateRelationship requires warmup to have been called.
                        client.warmup(0L);

                        mSession = mClient.newSession(customTabsCallback);

                        lunchTrustWebService();
                        registerBroadcastReceiver();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        mClient = null;

                    }
                });
    }


    @SuppressLint("WrongConstant")
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PostMessageBroadcastReceiver.POST_MESSAGE_ACTION);
        ContextCompat.registerReceiver(this, new PostMessageBroadcastReceiver(mSession),
                intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }
}