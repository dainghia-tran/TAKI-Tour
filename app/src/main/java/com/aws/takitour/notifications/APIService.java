package com.aws.takitour.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAA5IDb_3E:APA91bHGeUbncTMaH5q52TbLEiiF7suxzLQUQtLAZ_ItotF8ya551FWPRih1iI883AG-nSbPTYMgZJQ78CKpEkdpuSfa7HARLPnt_FPiJs2YZMBpqc3XYVizo1EBVnH0Yu4E7YFY6OkU"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
