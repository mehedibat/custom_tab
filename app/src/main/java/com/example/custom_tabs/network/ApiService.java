package com.example.custom_tabs.network;

import androidx.lifecycle.LiveData;

import com.example.custom_tabs.model.CounterBean;
import com.example.custom_tabs.model.CustomTabModel;
import com.example.custom_tabs.model.Employee;

import java.util.List;


import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

//    @GET
//    @Streaming
//    Flowable<List<CustomTabModel>> openSseConnectionData();

    @GET("api/v1/admin/counter-user/counter")
    Flowable<Response<List<CounterBean>>> getCounterList();

    @GET("events/eb315c23-781b-4c99-84d0-5d97607849c0/{sessionId}")
    Flowable<Employee> openSseConnectionData(@Path("sessionId") String sessionId);

    @GET("oneevent/eb315c23-781b-4c99-84d0-5d97607849c0/{sessionId}")
    Single<ResponseBody> getEmployee(@Path("sessionId") String sessionId);
}
