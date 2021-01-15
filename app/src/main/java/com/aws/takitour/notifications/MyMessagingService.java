package com.aws.takitour.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.aws.takitour.views.MainActivity;
import com.aws.takitour.views.TourDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.aws.takitour.views.LoginActivity.myDBReference;

public class MyMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMessagingService";
    private static final String CHANNEL_ID = "my_noti_channel";
    private Data newNoti;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notiTitle = null;
        String notiBody = null;
        String notiFrom = null;
        String notiImage = null;
        int notiType = -1;
        String noTiReceiveToken = null;
        if (remoteMessage.getData().size() > 0) {
            notiTitle = remoteMessage.getData().get("title");
            notiBody = remoteMessage.getData().get("body");
            notiFrom = remoteMessage.getData().get("user");
            noTiReceiveToken = remoteMessage.getData().get("receiveToken");
            notiImage = remoteMessage.getData().get("imageLink");
            notiType = Integer.parseInt(remoteMessage.getData().get("type"));

            if(notiImage!=null){
                newNoti = new Data(notiFrom, notiTitle, notiBody, noTiReceiveToken, notiImage);
            }else{
                newNoti = new Data(notiFrom, notiTitle, notiBody, noTiReceiveToken);
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
        Intent intent = new Intent(this, TourDashboard.class);
        intent.putExtra("TOUR_ID", currentNoti.getUser().substring(6,12));
        intent.putExtra("TOUR_NAME", "");
        int notificationID = 5 + (int) (Math.random() * ((1000 - 0) + 1));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(currentNoti.getTitle())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{0, 500, 1000})
                .setContentText(currentNoti.getBody())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        // Create channel to shoe Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "MyNotifications";
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, channelName,
                            NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0 /*ID of notification */, notificationBuilder.build());
        }
    }

}
