package com.aws.takitour.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aws.takitour.R;
import com.aws.takitour.views.TourDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMessagingService";
    private static final String CHANNEL_ID = "my_noti_channel";
    private Data newNoti;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notiTourId = null;
        String notiTitle = null;
        String notiBody = null;
        String notiSender = null;
        String notiImage = null;
        String noTiReceiveToken = null;
        if (remoteMessage.getData().size() > 0) {
            notiTourId = remoteMessage.getData().get("tourId");
            notiSender = remoteMessage.getData().get("sender");
            notiTitle = remoteMessage.getData().get("title");
            notiBody = remoteMessage.getData().get("body");
            noTiReceiveToken = remoteMessage.getData().get("receiveToken");
            notiImage = remoteMessage.getData().get("imageLink");
            if(notiImage!=null){
                newNoti = new Data(notiTourId, notiSender, notiTitle, notiBody, noTiReceiveToken, notiImage);
            }else{
                newNoti = new Data(notiTourId, notiSender, notiTitle, notiBody, noTiReceiveToken);
            }

            ref.child(currentUser.getEmail().replace(".", ",")).child("notifications").child(remoteMessage.getMessageId()).setValue(newNoti);
        }
        if (notiTitle != null && notiBody != null) {
            showNotification(newNoti);
        }
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Token token = new Token(newToken);
        // save new FCM token to "users" collection
        if(currentUser != null){
            ref.child(currentUser.getEmail().replace(".", ",")).child("token").setValue(token.getToken());
        }
    }

    public void showNotification(Data currentNoti) {
        // Set up Pending Intent after clicking the push notification
        Intent intent = new Intent(this, TourDashboard.class);
        intent.putExtra("TOUR_ID", currentNoti.getTourId());
        intent.putExtra("TOUR_NAME", "");
        int notificationID = 5 + (int) (Math.random() * ((1000 - 0) + 1));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Vibrator viberateManager = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        // Declare SOS sound and vibratimeTime
        MediaPlayer mPlayer = null;
        int viberateTime = 0;
        if(currentNoti.getType() == 1) {
            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sos);
            viberateTime = 1000;
        }
        else{
            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pristine);
        }

        // Init notification manager
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel to show Notifications
        String channelName = "MyNotifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, channelName,
                            NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            notificationManager.createNotificationChannel(mChannel);
        }
        // Set up notification builder
        NotificationCompat.Builder mNotiBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        mNotiBuilder
                .setContentTitle(currentNoti.getTitle())
                .setContentText(currentNoti.getBody())
                .setSmallIcon(R.drawable.ic_outline_notifications_24)
                .setAutoCancel(true)
                .setColor(Color.YELLOW)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(notificationID, mNotiBuilder.build());
            viberateManager.vibrate(viberateTime);
            mPlayer.start();
        }
    }

}
