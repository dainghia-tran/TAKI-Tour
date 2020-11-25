package com.aws.takitour.adapters;

import android.content.Context;
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

import java.util.List;

public class TourRVAdapter extends RecyclerView.Adapter<TourRVAdapter.ViewHolder> {
    private Context context;
    private List<Tour> tourList;
    private Dialog dialog;




    public TourRVAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_tour_card,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_tour_details);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = dialog.findViewById(R.id.img_image_detail);
                TextView tourNameDetail = dialog.findViewById(R.id.tv_tour_name_detail);
                TextView ratingDetail = dialog.findViewById(R.id.tv_rating_detail);
                TextView durationDetail = dialog.findViewById(R.id.tv_duration_detail);
                TextView costDetail = dialog.findViewById(R.id.tv_cost_detail);
                TextView shortDescriptionDetail = dialog.findViewById(R.id.tv_short_description_detail);
                TextView tourGuideNameDetail = dialog.findViewById(R.id.tv_tour_guide_name_detail);
                TextView introduction = dialog.findViewById(R.id.tv_introduction);

                Tour tour = tourList.get(viewHolder.getAdapterPosition());

                tourNameDetail.setText(tour.getName());
                ratingDetail.setText(String.valueOf(tour.getOverallRating()));
                durationDetail.setText(tour.getDescription());
                costDetail.setText(tour.getPrice());
                shortDescriptionDetail.setText(tour.getDescription());
                tourGuideNameDetail.setText((tour.getTourGuide()));

                dialog.show();
            }
        });
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        holder.tourName.setText(tour.getName());
        holder.rating.setText(String.valueOf(tour.getOverallRating()));
        holder.cost.setText(tour.getPrice());
        holder.detail.setText(tour.getDescription());
        holder.tourGuideName.setText((tour.getTourGuide()));
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private ConstraintLayout tourCard;
        private ImageView image;
        private TextView tourName;
        private TextView rating;
        private TextView duration;
        private TextView cost;
        private TextView detail;
        private TextView tourGuideName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tourCard = itemView.findViewById(R.id.tour_card);
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
