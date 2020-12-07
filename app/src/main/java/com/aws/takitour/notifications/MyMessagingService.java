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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aws.takitour.R;
import com.aws.takitour.models.User;
import com.aws.takitour.views.MainActivity;
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
        String noTiReceiveToken = null;
        if (remoteMessage.getData().size() > 0) {
            notiTitle = remoteMessage.getData().get("title");
            notiBody = remoteMessage.getData().get("body");
            notiFrom = remoteMessage.getData().get("user");
            noTiReceiveToken = remoteMessage.getData().get("receiveToken");
            newNoti = new Data(notiFrom, notiTitle, notiBody, noTiReceiveToken);

            List<User> users = new ArrayList<>();

            ref.child(currentUser.getEmail().replace(".", ",")).child("notifications").child(remoteMessage.getMessageId()).setValue(newNoti);
        }
        if (notiTitle != null && notiBody != null) {
            showNotification(notiTitle, notiBody);
        }
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Token token = new Token(newToken);
        // save new FCM token to "users" collection
        ref.child(currentUser.getEmail().replace(".", ",")).child("token").setValue(token.getToken());
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        int notificationID = 5 + (int) (Math.random() * ((1000 - 0) + 1));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        // Create channel to shoe Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "MyNotifications";
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, channelName,
                            NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0 /*ID of notification */, notificationBuilder.build());
        }
    }

}
