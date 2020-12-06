package com.aws.takitour.models;

import android.util.Log;

import com.aws.takitour.notifications.APIService;
import com.aws.takitour.notifications.Client;
import com.aws.takitour.notifications.Data;
import com.aws.takitour.notifications.MyResponse;
import com.aws.takitour.notifications.Sender;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.*;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;

public class Notification implements Serializable {
    private String sender;
    private String body;
    private String title;
    APIService apiService;

    public Notification(){
    }

    public Notification(String sender, String title, String body) {
        this.sender = sender;
        this.body = body;
        this.title = title;
        this.apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    }
    public void sendNotification(String targetToken){
        Data data = new Data(this.sender, this.title, this.body);
        Sender sendWorker = new Sender(data, targetToken);
        apiService.sendNotification(sendWorker).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Log.d("TAG", "Failed");
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
