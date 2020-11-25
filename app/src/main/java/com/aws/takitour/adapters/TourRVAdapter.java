package com.aws.takitour.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;
import com.aws.takitour.views.TourDashboard;
import com.bumptech.glide.Glide;

import java.util.List;

public class TourRVAdapter extends RecyclerView.Adapter<TourRVAdapter.ViewHolder> {
    private Context context;
    private List<Tour> tourList;

    public TourRVAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_tour_card, parent, false);    //inflate layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        if (tour != null) {
            Glide.with(context).load(tour.getCoverImage().get(0)).into(holder.image);
            holder.tourName.setText(tour.getName());
            holder.rating.setText(String.valueOf(tour.getOverallRating()));
            holder.cost.setText(tour.getPrice());
            holder.detail.setText(tour.getDescription());
            holder.tourGuideName.setText((tour.getTourGuide()));
        }
        holder.tourCard.setOnClickListener(v->{
            Intent tourDashboard = new Intent(context, TourDashboard.class);
            tourDashboard.putExtra("TOUR_ID", tour.getId());
            context.startActivity(tourDashboard);
        });
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tourName;
        TextView rating;
        TextView duration;
        TextView cost;
        TextView detail;
        TextView tourGuideName;
        ConstraintLayout tourCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tourCard = itemView.findViewById(R.id.tour_card_layout);
            image = itemView.findViewById(R.id.img_tour_image);
            tourName = itemView.findViewById(R.id.tv_tour_name);
            rating = itemView.findViewById(R.id.tv_tour_rating);
            duration = itemView.findViewById(R.id.tv_tour_duration);
            cost = itemView.findViewById(R.id.tv_tour_cost);
            detail = itemView.findViewById(R.id.tv_tour_detail);
            tourGuideName = itemView.findViewById((R.id.tv_tour_guide_name));
        }
    }
}
