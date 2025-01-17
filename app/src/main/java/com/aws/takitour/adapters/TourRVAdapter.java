package com.aws.takitour.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aws.takitour.R;
import com.aws.takitour.models.Participant;
import com.aws.takitour.models.Tour;
import com.aws.takitour.notifications.APIService;
import com.aws.takitour.notifications.Client;
import com.aws.takitour.notifications.Data;
import com.aws.takitour.notifications.MyResponse;
import com.aws.takitour.notifications.Sender;
import com.aws.takitour.views.TourDashboard;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static com.aws.takitour.views.LoginActivity.myDBReference;

public class TourRVAdapter extends RecyclerView.Adapter<TourRVAdapter.ViewHolder> {
    private static final String TAG = "TourRVAdapter";
    private Context context;

    private List<Tour> tourList;
    private final Handler handler = new Handler();
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
            holder.duration.setText(("Từ " + tour.getStartDate() + " đến " + tour.getEndDate()));
            holder.cost.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(Double.valueOf(tour.getPrice())));

            new Thread(() -> {
                myDBReference.child("users")
                        .child(tour.getTourGuide().replace(".", ","))
                        .child("name")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                handler.post(() -> {
                                    holder.tourGuideName.setText(snapshot.getValue(String.class));
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }).start();
        }
        holder.tourCard.setOnClickListener(v -> {
            new Thread(() -> {
                myDBReference.child("tours")
                        .child(tour.getId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Participant> participants = new ArrayList<>();
                                for (DataSnapshot data : snapshot.child("participants").getChildren()) {
                                    participants.add(data.getValue(Participant.class));
                                }
                                String tourGuide = snapshot.child("tourGuide").getValue(String.class);
                                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                if (tourGuide.equals(currentUserEmail)) {
                                    handler.post(() -> {
                                        Intent intent = new Intent(context, TourDashboard.class);
                                        intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.putExtra("TOUR_ID", tour.getId());
                                        intent.putExtra("TOUR_NAME", tour.getName());
                                        context.startActivity(intent);
                                    });
                                    return;
                                }

                                if (participants.size() != 0) {
                                    for (Participant participant : participants) {
                                        String participantEmail = participant.getEmail();
                                        if (participantEmail != null && participantEmail.equals(currentUserEmail)) {
                                            handler.post(() -> {
                                                Intent intent = new Intent(context, TourDashboard.class);
                                                intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                intent.putExtra("TOUR_ID", tour.getId());
                                                intent.putExtra("TOUR_NAME", tour.getName());
                                                context.startActivity(intent);
                                            });
                                            return;
                                        }
                                    }
                                }
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_tour_details);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView imageView = dialog.findViewById(R.id.img_image_detail);
                                TextView tourNameDetail = dialog.findViewById(R.id.tv_tour_name_detail);

                                TextView durationDetail = dialog.findViewById(R.id.tv_duration_detail);
                                TextView costDetail = dialog.findViewById(R.id.tv_price_detail);
                                TextView shortDescriptionDetail = dialog.findViewById(R.id.tv_short_description_detail);
                                TextView tourGuideNameDetail = dialog.findViewById(R.id.tv_tour_guide_name_detail);
                                EditText code = dialog.findViewById(R.id.edt_code_detail);
                                Button confirm = dialog.findViewById(R.id.btn_confirm_detail);

                                Glide.with(context).load(tour.getCoverImage().get(0)).into(imageView);
                                tourNameDetail.setText(tour.getName());
                                durationDetail.setText(("Từ " + tour.getStartDate() + " đến " + tour.getEndDate()));
                                costDetail.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(Double.valueOf(tour.getPrice())));
                                shortDescriptionDetail.setText(tour.getDescription());
                                tourGuideNameDetail.setText((tour.getTourGuide()));

                                new Thread(() -> {
                                    myDBReference.child("users")
                                            .child(tour.getTourGuide().replace(".", ","))
                                            .child("name")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    handler.post(() -> {
                                                        tourGuideNameDetail.setText(snapshot.getValue(String.class));
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }).start();
                                dialog.show();

                                confirm.setOnClickListener(v1 -> {
                                    if (code.getText().toString().equals(tour.getId())) {
                                        Intent intentTourDashboard = new Intent(context, TourDashboard.class);
                                        intentTourDashboard.putExtra("TOUR_ID", tour.getId());
                                        intentTourDashboard.putExtra("TOUR_NAME", tour.getName());
                                        intentTourDashboard.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intentTourDashboard.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        handler.post(() -> {
                                            dialog.dismiss();
                                            context.startActivity(intentTourDashboard);
                                            updateUserTourList(tour.getId());
                                        });
                                    } else {
                                        handler.post(()->{
                                            Toast.makeText(context, "Mã đã nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }).start();
        });
    }

    public void updateUserTourList(String tourId) {

        new Thread(() -> {
            myDBReference.child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String newParticipantToken = snapshot.child("token").getValue(String.class);
                            Participant newParticipant = new Participant(FirebaseAuth.getInstance().getCurrentUser().getEmail(), snapshot.child("name").getValue(String.class));
                            myDBReference.child("tours")
                                    .child(tourId)
                                    .child("participants")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                    .setValue(newParticipant);
                            myDBReference.child("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                                    .child("tourList")
                                    .child(tourId)
                                    .setValue(tourId);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }).start();
    }


    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tourName;
        TextView duration;
        TextView cost;
        TextView tourGuideName;
        ConstraintLayout tourCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tourCard = itemView.findViewById(R.id.tour_card_layout);
            image = itemView.findViewById(R.id.img_tour_image);
            tourName = itemView.findViewById(R.id.tv_tour_name);
            duration = itemView.findViewById(R.id.tv_tour_duration);
            cost = itemView.findViewById(R.id.tv_tour_cost);
            tourGuideName = itemView.findViewById((R.id.tv_tour_guide_name));
        }
    }
}
