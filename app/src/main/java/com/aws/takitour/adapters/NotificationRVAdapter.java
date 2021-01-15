package com.aws.takitour.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Notification;
import com.aws.takitour.notifications.Data;
import com.bumptech.glide.Glide;

import java.util.List;


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
            holder.tvSender.setText(notification.getUser());
            holder.tvTitle.setText(notification.getTitle());
            holder.tvBody.setText(notification.getBody());
        }
        holder.layoutNotificationCard.setOnClickListener(v->{
            if(notification.getType() == 0){
                //TODO start activity
            }else{
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_picture_item);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView imageView = dialog.findViewById(R.id.img_picture_dialog);

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
