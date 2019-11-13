package com.tuna.homestay.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tuna.homestay.R;
import com.tuna.homestay.activity.LoginActivity;
import com.tuna.homestay.activity.ProfileActivity;
import com.tuna.homestay.model.entity.Constant;

public class AccountFragment extends Fragment {
    private TextView tvUser;
    private ImageView imgUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference colRef;
    private String name,linkAvt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_account, container, false);
        tvUser = view.findViewById(R.id.tvUser);
        imgUser = view.findViewById(R.id.imgUser);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //Nếu đã có người đăng nhập
            String id = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
            colRef = mFirestore.collection(Constant.TAI_KHOAN);
            colRef.whereEqualTo(Constant.ID_USER,id)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot qds : task.getResult()){
                            name = qds.getString(Constant.NAME);
                            linkAvt = qds.getString(Constant.LINK_AVATAR);

                            tvUser.setText(name);
                            Glide.with(getContext()).load(linkAvt).into(imgUser);
                        }
                    }
                }
            });
        }else {
            tvUser.setText("Đăng nhập");
            imgUser.setImageResource(R.drawable.default_avatar);
        }
    }
}
