package com.aws.takitour.adapters;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Picture;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LibraryRVAdapter extends RecyclerView.Adapter<LibraryRVAdapter.LibraryViewHolder> {
    private Context context;
    private List<Picture> pictureList;

    public LibraryRVAdapter(Context context, List<Picture> pictureList) {
        this.context = context;
        this.pictureList = pictureList;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_library, parent, false);    //inflate layout
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Picture picture = pictureList.get(position);

        if (picture != null) {
            Glide.with(context).load(picture.getPic().get(0)).into(holder.picture);
            holder.owner.setText(picture.getOwner());
        }


    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder{
        private TextView owner;
        private ImageView picture;
        LinearLayout item;
        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            owner = itemView.findViewById(R.id.tv_owner);
            picture = itemView.findViewById(R.id.img_picture);
            item = itemView.findViewById((R.id.picture_item_layout));
        }
    }
}
