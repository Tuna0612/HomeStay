package com.tuna.homestay.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tuna.homestay.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    public CircleImageView imgUser;
    public TextView tvUser;
    public TextView tvDate;
    public ImageView btnSave;
    public TextView tvAddress;
    public TextView tvPrice;
    public TextView tvAcreage;
    public ImageView imgHomestay;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardView);
        imgUser = itemView.findViewById(R.id.imgUser);
        imgHomestay = itemView.findViewById(R.id.imgHomestay);
        tvUser = itemView.findViewById(R.id.tvUser);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvAddress = itemView.findViewById(R.id.tvAddress);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        tvAcreage = itemView.findViewById(R.id.tvAcreage);
        btnSave = itemView.findViewById(R.id.btnSave);
    }
}
