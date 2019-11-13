package com.tuna.homestay.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tuna.homestay.R;
import com.tuna.homestay.activity.DetailHomeStay;
import com.tuna.homestay.activity.LoginActivity;
import com.tuna.homestay.activity.ProfileActivity;
import com.tuna.homestay.activity.UploadHomeStayActivity;
import com.tuna.homestay.adapter.EndlessScrollListener;
import com.tuna.homestay.adapter.PostAdapter;
import com.tuna.homestay.model.Post;
import com.tuna.homestay.model.entity.CheckConnection;
import com.tuna.homestay.model.entity.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    Context mContext;
    private RecyclerView rcHomeStay;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore fireStore;
    private ArrayList<Post> postList = new ArrayList<>();
    private PostAdapter adapter;
    private boolean hasNextItem = true;
    private EndlessScrollListener onScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        rcHomeStay = view.findViewById(R.id.rcHomeStay);
        fab = view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        adapter = new PostAdapter(getContext(), postList, new PostAdapter.AdapterListener() {
            @Override
            public void OnClickItem(int position) {
                Intent intent = new Intent(getContext(), DetailHomeStay.class);
                intent.putExtra("id", postList.get(position).id);
                startActivity(intent);
            }

            @Override
            public void OnClickButtonSave(int position) {
                if (CheckConnection.haveNetworkConnection(getContext())) {
                    if (mCurrentUser != null) {
                        //Nếu có người đăng nhập rồi
                        String idPost = postList.get(position).id;
                        String idUser = mCurrentUser.getUid();
                        savePost(idPost, idUser);
                    } else {
                        //Chưa có người đăng nhập, show dialog yêu cầu đăng nhập
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert));
                        builder.setMessage("Bạn cần đăng nhập trước !")
                                .setCancelable(true)
                                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Chú ý");
                        dialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), "Hãy kết nối internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnClickAvatar(int position) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("idUser", postList.get(position).getIdUser());
                startActivity(intent);
            }
        });
        rcHomeStay.setAdapter(adapter);

        //Load more RecyclerView
        onScroll = new EndlessScrollListener((LinearLayoutManager) rcHomeStay.getLayoutManager()) {
            @Override
            public void onLoadMore() {
                loaData(false);
            }
        };
        rcHomeStay.addOnScrollListener(onScroll);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getContext())) {
                    if (mCurrentUser != null) {
                        //Nếu có người đăng nhập rồi
                        startActivity(new Intent(getContext(), UploadHomeStayActivity.class));
                    } else {
                        //Chưa có người đăng nhập, show dialog yêu cầu đăng nhập
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert));
                        builder.setMessage("Bạn cần đăng nhập trước !")
                                .setCancelable(true)
                                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Chú ý");
                        dialog.show();
                    }
                } else {
                    Toast.makeText(getContext(), "Hãy kết nối internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePost(final String idPost, final String idUser) {
        Toast.makeText(getContext(), "Đã lưu", Toast.LENGTH_SHORT).show();

        //Check xem đã có tin trên FireStore chưa
        fireStore.collection(Constant.TIN_LUU).whereEqualTo(Constant.TIN_LUU_ID_POST, idPost)
                .whereEqualTo(Constant.TIN_LUU_ID_USER, idUser)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constant.TIN_LUU_ID_USER, idUser);
                    map.put(Constant.TIN_LUU_ID_POST, idPost);
                    fireStore.collection(Constant.TIN_LUU).add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.e("Post", "Lưu Thành Công");
                        }
                    });
                } else {
                    Log.e("Post", "Đã lưu rồi");
                }
            }
        });

    }

    private void initData() {
        fireStore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        loaData(true);
    }

    private void loaData(final boolean isRefresh) {
        if(!isRefresh && !hasNextItem){
            return;
        }

        CollectionReference collectionRef = fireStore.collection(Constant.HOMESTAY);
        Query query;

        if(isRefresh){
            query = collectionRef.orderBy("date",Query.Direction.DESCENDING).limit(Constant.LIMIT_ITEM);
        }else {
            adapter.showLoadingItem(true);
            Timestamp pivotTime;
            if(postList.isEmpty()){
                pivotTime = new Timestamp(new Date());
            }else {
                pivotTime = postList.get(postList.size()-1).getDate();
            }
            query = collectionRef.whereLessThanOrEqualTo("date",pivotTime).orderBy("date",Query.Direction.DESCENDING).limit(Constant.LIMIT_ITEM);
        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(isRefresh){
                        postList.clear();
                        Post post = null;
                        for (QueryDocumentSnapshot qds:task.getResult()){
                            post = qds.toObject(Post.class).withId(qds.getId());
                            postList.add(post);
                        }
                        adapter.notifyDataSetChanged();
                        hasNextItem = postList.size()==Constant.LIMIT_ITEM;
                    }else {
                        ArrayList<Post> morePost = new ArrayList<>();
                        Post post = null;
                        for (QueryDocumentSnapshot qds :task.getResult()){
                            post = qds.toObject(Post.class).withId(qds.getId());
                            morePost.add(post);
                        }
                        morePost.remove(0);
                        hasNextItem = morePost.size() == Constant.LIMIT_ITEM;

                        int oldSize = postList.size();
                        postList.addAll(morePost);
                        adapter.notifyItemRangeInserted(oldSize,morePost.size());
                        adapter.showLoadingItem(false);
                    }

                }else {
                    Log.e("homestay", "Error getting documents: ", task.getException());
                    if (isRefresh) {

                    } else {
                        adapter.showLoadingItem(false);
                    }
                }
                onScroll.setLoaded();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }
}
