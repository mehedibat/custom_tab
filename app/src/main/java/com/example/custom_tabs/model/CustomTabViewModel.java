package com.example.custom_tabs.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.custom_tabs.network.ApiService;
import com.example.custom_tabs.network.SSEEventData;
import com.example.custom_tabs.network.SSERepository;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class CustomTabViewModel extends ViewModel {


    private SSERepository repository = new SSERepository();
    private LiveData<SSEEventData> sseEventsLiveData = repository.getSseEventsLiveData();



    public LiveData<SSEEventData> getSSEEvents() {
        return sseEventsLiveData;
    }

    public void fetchSSEEvents() {
        repository.fetchSSEEvents();
    }



    private void getEmployee(ApiService apiService) {
        apiService.getEmployee(UUID.randomUUID().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> System.out.println("This is response: " + response),
                        error -> error.printStackTrace()
                );
    }

    private final MediatorLiveData<Employee> liveData = new MediatorLiveData<>();
    private void getEvents(ApiService apiService) {

        final LiveData<Employee> source = LiveDataReactiveStreams
                .fromPublisher(
                        apiService.openSseConnectionData(UUID.randomUUID().toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(subscription -> System.out.println("subscription"))
                                .onErrorReturn(throwable -> {
                                    System.out.println("error "+throwable.getMessage());
                                    return new Employee();
                                })
                                .map(employee -> {
                                    System.out.println("Employee");
                                    return employee;
                                })
                );
        liveData.addSource(source, resource -> {
            System.out.println("resource");
            System.out.println(resource);
            liveData.setValue(resource);
//            liveData.removeSource(source);
        });


//        apiService.openSseConnectionData(UUID.randomUUID().toString())
//                .toObservable()
//                .subscribeOn(Schedulers.io())
////                .timeout(15, TimeUnit.SECONDS)
////                .retry()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Employee>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        System.out.println("onSubscribe" + d);
//                    }
//
//                    @Override
//                    public void onNext(Employee employee) {
//                        System.out.println("onNext => " + employee);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        System.out.println("onError");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        System.out.println("onComplete");
//                    }
//                });
    }








}
