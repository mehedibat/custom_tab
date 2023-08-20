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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.custom_tabs.model.CustomTabViewModel;
import com.example.custom_tabs.network.SSEEventData;
import com.example.custom_tabs.network.STATUS;
import com.example.custom_tabs.service.TimerService;
import com.example.custom_tabs.utils.PostMessageBroadcastReceiver;
import com.example.custom_tabs.utils.ServiceUtil;
import com.google.androidbrowserhelper.trusted.TwaLauncher;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity  {
private static final String TAG = MainActivity.class.getSimpleName();
    //private final String webAppUrl = "https://192.168.30.193/";
    private final String webAppUrl = "https://stage-insightdb.ba-systems.com/";
    private CustomTabsSession mSession;
    private CustomTabsClient mClient;



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
        MyApp.getInstance().setActivityVisible(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG,"onCreate Kobi");
        //bindCustomTabsService();
        lunchTrustWebService();
    }


    private void lunchTrustWebService() {
//        new TrustedWebActivityIntentBuilder(Uri.parse(webAppUrl)).build(mSession)
//                .launchTrustedWebActivity(MainActivity.this);
//        TwaLauncher twaLauncher = new TwaLauncher(this);
//        twaLauncher.launch(Uri.parse(webAppUrl));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ServiceUtil.startForegroundService(this, TimerService.class);
        getDataFromServer();
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

                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        mClient = null;

                    }
                });
    }



    public void getDataFromServer() {

        CustomTabViewModel viewModel =  new ViewModelProvider(this).get(CustomTabViewModel.class);

        viewModel.fetchSSEEvents();
        viewModel.getSSEEvents().observe(this, new Observer<SSEEventData>() {
            @Override
            public void onChanged(@Nullable SSEEventData sseEventData) {
                // Update UI based on the new data
                if (sseEventData != null) {
                    STATUS status = sseEventData.getStatus();
                    String image = sseEventData.getFirstName();
                    System.out.println("***********************************************************************");
                    System.out.println("Id => " + sseEventData.getId());
                    System.out.println("firstname => " + sseEventData.getFirstName());
                    System.out.println("lastname => " + sseEventData.getLastname());
                    // Update UI components accordingly
                }
            }
        });

    }


}