package com.aws.takitour.models;

import android.util.Log;
import android.widget.Toast;

import com.aws.takitour.notifications.APIService;
import com.aws.takitour.notifications.Client;
import com.aws.takitour.notifications.Data;
import com.aws.takitour.notifications.MyResponse;
import com.aws.takitour.notifications.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendTestNoti implements Serializable {
    private APIService apiService;
    private final String TAG = "SendTestNoti";
    public SendTestNoti() {
        this.apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

    }
    public void sendNoti(String uToken, String title, String message){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Data data = new Data(currentUser.getEmail(), title,message, uToken);
        Sender sender = new Sender(data,uToken);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.code()==200){
                    if(response.body().success !=1){
                        Log.d(TAG, "fail to send msg");
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.d(TAG, "fail to send msg again");
            }
        });


        }
}
