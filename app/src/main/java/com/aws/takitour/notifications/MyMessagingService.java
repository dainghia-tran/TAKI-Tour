package com.aws.takitour.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.aws.takitour.R;
import com.aws.takitour.views.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import io.reactivex.annotations.NonNull;

public class MyMessagingService extends FirebaseMessagingService {
    private  static final String CHANNEL_ID = "my_noti_channel";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
   
    }
//    @Override
//    public void onNewToken(String token) {
//        super.onNewToken(token);
//        Log.d("TAG", "Refreshed token: " + token);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                if (!task.isSuccessful()) {
//                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                    return;
//                }
//
//                // Get new FCM registration token
//                String token = task.getResult();
//
//                // Log and toast
//                String msg = getString(R.string.msg_token_fmt, token);
//                Log.d("TAG", msg);
//            }
//        });
//        sendRegistrationToServer(token);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // TODO: Implement this method to send token to your app server.
//
//    }

    public void sendNotification(String title, String message){
        Intent intent = new Intent(this, MainActivity.class);
        int notificationID = 5 + (int)(Math.random() * ((1000 - 0) + 1));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"MyNotifications")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        // Create channel to send Notifications
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName = "MyNotifications";
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID,channelName,
                            NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService (NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

}
