package com.tuna.homestay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuna.homestay.R;
import com.tuna.homestay.adapter.viewholder.LoadingViewHolder;
import com.tuna.homestay.adapter.viewholder.PostViewHolder;
import com.tuna.homestay.model.Post;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("đ-MM-yyyy HH:mm:ss");
    private static final int ITEM_LOADING = 0;
    private static final int ITEM_TIN_DANG = 1;
    private Context context;
    private ArrayList<Post> postList;
    private AdapterListener adapterListener;
    private boolean hasLoadingItem = false;

    public interface AdapterListener {
        void OnClickItem(int position);

        void OnClickButtonSave(int position);

        void OnClickAvatar(int position);
    }

    public PostAdapter(Context context, ArrayList<Post> postList, AdapterListener adapterListener) {
        this.context = context;
        this.postList = postList;
        this.adapterListener = adapterListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
            return new PostViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PostViewHolder) {
            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
            String idUser = postList.get(position).getIdUser();
            DocumentReference docRef = fireStore.collection("taikhoan").document(idUser);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap.exists()) {
                            String linkAvt = docSnap.getString("linkavatar");
                            if (!linkAvt.equals("nonono")) {
                                Glide.with(context).load(linkAvt).into(((PostViewHolder) holder).imgUser);
                            }
                            String name = docSnap.getString("hoten");
                            ((PostViewHolder) holder).tvUser.setText(name);
                        }
                    }
                }
            });

            //Set Text DateTime
            Date date = postList.get(position).getDate().toDate();
            String ngaydang = sdf.format(date);
            ((PostViewHolder) holder).tvDate.setText(ngaydang);

            //Set Text Địa Chỉ
            final String address = postList.get(position).getCommune() + ", " + postList.get(position).getDistrict() + ", " + postList.get(position).getProvince();
            ((PostViewHolder) holder).tvAddress.setText(address);

            //Set Text Giá phòng
            ((PostViewHolder) holder).tvPrice.setText("Giá: " + NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(postList.get(position).getPrice()));

            //Set Text Diện Tích
            ((PostViewHolder) holder).tvAcreage.setText("Diện tích: " + postList.get(position).getAcreage() + "m²");

            //Load Ảnh Homestay
            Glide.with(context).load(postList.get(position).getImgHomestay()).into(((PostViewHolder) holder).imgHomestay);

            //Set Sự kiện click cái item
            ((PostViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.OnClickItem(position);
                }
            });

            ((PostViewHolder) holder).btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.OnClickButtonSave(position);
                }
            });

            ((PostViewHolder) holder).imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.OnClickAvatar(position);
                }
            });

        } else if (holder instanceof LoadingViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        if (hasLoadingItem) {
            return postList.size() + 1;
        } else {
            return postList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == postList.size()) {
            return ITEM_LOADING;
        } else {
            return ITEM_TIN_DANG;
        }
    }

    public void showLoadingItem(boolean isShow) {
        if (isShow) {
            hasLoadingItem = true;
            notifyItemInserted(postList.size());
        } else {
            hasLoadingItem = false;
            notifyItemRemoved(postList.size());
        }
    }
}
