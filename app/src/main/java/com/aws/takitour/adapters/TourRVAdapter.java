package com.aws.takitour.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Tour;

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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.tour_card,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        holder.tourName.setText(tour.getName());
        holder.rating.setText((int) tour.getOverallRating());
        holder.cost.setText(tour.getPrice());
        holder.detail.setText(tour.getDescription());
        holder.tourGuideName.setText((tour.getTourGuide()));
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{

        ImageView image;
        TextView tourName;
        TextView rating;
        TextView duration;
        TextView cost;
        TextView detail;
        TextView tourGuideName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.img_tour_image);
            tourName = itemView.findViewById(R.id.tv_tour_name);
            rating = itemView.findViewById(R.id.tv_tour_rating);
            duration =itemView.findViewById(R.id.tv_tour_duration);
            cost = itemView.findViewById(R.id.tv_tour_cost);
            detail = itemView.findViewById(R.id.tv_tour_detail);
            tourGuideName = itemView.findViewById((R.id.tv_tour_guide_name));
        }
    }
}
