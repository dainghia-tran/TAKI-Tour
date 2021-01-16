package com.aws.takitour.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Notification;
import com.aws.takitour.notifications.Data;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.aws.takitour.views.LoginActivity.myDBReference;


public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.NotificationViewHolder> {
    Context context;
    private List<Data> notificationList;
    private final Handler handler = new Handler();

    public NotificationRVAdapter(Context context, List<Data> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Data notification = notificationList.get(position);

        if (notification != null) {
            holder.tvSender.setText(notification.getSender());
            holder.tvTitle.setText(notification.getTitle());
            holder.tvBody.setText(notification.getBody());
        }
        holder.layoutNotificationCard.setOnClickListener(v -> {
            if (notification.getType() == 0) {
                //TODO start activity
            } else if (notification.getType() == 1) {
                // Init diaglog
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_sos_notification);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Link UI diaglog with code
                TextView tvNotiFrom = dialog.findViewById(R.id.tv_sos_from);
                TextView tvNotiBody = dialog.findViewById(R.id.tv_sos_body);
                ImageView imageView = dialog.findViewById(R.id.img_sos);
                Button btnCheckSenderLocation = dialog.findViewById(R.id.btn_check_sender_location);

                // Get sender name, long, lat from database
                String senderEmail = notification.getSender();
                myDBReference.child("users").child(senderEmail.replace(".", ",")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String longitude = snapshot.child("longitude").getValue(String.class);
                        String latitude = snapshot.child("latitude").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        tvNotiFrom.setText("Gừi từ: " + name);
                        btnCheckSenderLocation.setOnClickListener(v -> {
                            // Create a Uri from an intent string. Use the result to create an Intent.
                            Uri gmmIntentUri = Uri.parse(("google.navigation:q="+latitude +","+longitude));

                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            // Make the Intent explicit by setting the Google Maps package
                            mapIntent.setPackage("com.google.android.apps.maps");

                            // Attempt to start an activity that can handle the Intent
                            context.startActivity(mapIntent);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                tvNotiBody.setText("Thông điệp: " + notification.getBody());
                Glide.with(context).load(notification.getImageLink()).into(imageView);

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvTitle;
        TextView tvBody;
        LinearLayout layoutNotificationCard;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_noti_sender);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvBody = itemView.findViewById(R.id.tv_noti_description);
            layoutNotificationCard = itemView.findViewById(R.id.layout_noti_card);
        }
    }
}
