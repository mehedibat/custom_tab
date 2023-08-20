package com.example.custom_tabs.network;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlinx.coroutines.flow.MutableStateFlow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import timber.log.Timber;

public class SSERepository {

    private static final String EVENTSURL = "http://192.168.30.159:5555/events/eb315c23-781b-4c99-84d0-5d97607849c0/eb315c23-781b-4c99-00d0-5d97607849c0";

    private OkHttpClient sseClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build();

    private Request sseRequest = new Request.Builder()
            .url(EVENTSURL)
            .header("Accept", "application/json")
            .addHeader("Accept", "text/event-stream")
            .build();



    private MutableLiveData<SSEEventData> sseEventsLiveData = new MutableLiveData<>();

    public LiveData<SSEEventData> getSseEventsLiveData() {
        return sseEventsLiveData;
    }

    public void fetchSSEEvents() {
        initEventSource();
    }

    private EventSourceListener sseEventSourceListener = new EventSourceListener() {
        @Override
        public void onClosed(EventSource eventSource) {
            super.onClosed(eventSource);
            System.out.println("onClosed: " + eventSource);
            SSEEventData event = new SSEEventData(STATUS.CLOSED);
            emitSseEventData(event);
        }

        @Override
        public void onEvent(EventSource eventSource, String id, String type, String data) {
            super.onEvent(eventSource, id, type, data);
            System.out.println("onEvent: " + data);
            if (!data.isEmpty()) {
                SSEEventData userData = new Gson().fromJson(data, SSEEventData.class);
                SSEEventData sseEventDataFromResponseData = createSSEEventDataFromResponseData(userData);
                emitSseEventData(sseEventDataFromResponseData);
            }
        }

        @Override
        public void onFailure(EventSource eventSource, Throwable t, Response response) {
            super.onFailure(eventSource, t, response);
            if (t != null) {
                t.printStackTrace();
            }
            System.out.println("onFailure: " + (t != null ? t.getMessage() : ""));
            SSEEventData event = new SSEEventData(STATUS.ERROR);
            emitSseEventData(event);
        }

        @Override
        public void onOpen(EventSource eventSource, Response response) {
            super.onOpen(eventSource, response);
            System.out.println("onOpen: " + eventSource);
            SSEEventData event = new SSEEventData(STATUS.OPEN);
            emitSseEventData(event);
        }
    };


    private void initEventSource() {
        EventSources.createFactory(sseClient)
                .newEventSource(sseRequest, sseEventSourceListener);
    }

    private void emitSseEventData(SSEEventData eventData) {
        sseEventsLiveData.postValue(eventData);
    }

    private SSEEventData createSSEEventDataFromResponseData(SSEEventData userData) {
        return new SSEEventData(STATUS.SUCCESS,
                userData.getId(),
                userData.getFirstName(),
                userData.getLastname());
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message
                -> Log.d("OkHttp:\n %s", message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }


}
