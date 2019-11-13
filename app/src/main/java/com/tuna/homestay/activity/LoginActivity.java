package com.tuna.homestay.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tuna.homestay.R;
import com.tuna.homestay.model.entity.CheckConnection;
import com.tuna.homestay.model.entity.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edEmail;
    private EditText edPass;
    private TextView forgotPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        forgotPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void initView() {
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        forgotPassword = findViewById(R.id.forgot_password);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                Login(edEmail.getText().toString().trim(),edPass.getText().toString().trim());
                finish();
                break;
            case R.id.btnSignUp:
                Intent intent1 = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.forgot_password:
                Intent intent2 = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void Login(String email,String pass){
        if(CheckConnection.haveNetworkConnection(this)){
            if(!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass)){
                showLoading(true);
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            CheckandSaveTokentoDB();
                        }else {
                            String error = task.getException().getMessage();
                            Toasty.error(LoginActivity.this,error,Toast.LENGTH_SHORT,true).show();
                        }
                    }
                });
            }else {
                Toasty.info(LoginActivity.this, "Hãy nhập đầy đủ", Toast.LENGTH_SHORT, true).show();
            }
        }else {
            Toasty.warning(LoginActivity.this,"Kiểm tra lại kết nối internet",Toast.LENGTH_SHORT,true).show();
        }
    }

    private void CheckandSaveTokentoDB() {
        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Lấy token cho vào mảng
        FirebaseFirestore.getInstance().collection(Constant.TAI_KHOAN).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docSnap = task.getResult();
                if(docSnap.exists()){
                    Map<String,Object> map = docSnap.getData();
                    ArrayList<String> arr = (ArrayList<String>) map.get(Constant.TOKEN);
                    String refreshToken = FirebaseInstanceId.getInstance().getToken();

                    //check Token
                    int check = 0;
                    for(String s:arr){
                        if(s.equals(refreshToken)){
                            check = 1;
                            Log.e(Constant.TOKEN,"Đã có token rồi");
                            break;
                        }
                    }

                    if(check==0){
                        Log.e(Constant.TOKEN,"Chưa có token, hãy thêm vào db");
                        arr.add(refreshToken);

                        Map<String,Object> data = new HashMap<>();
                        data.put(Constant.TOKEN,arr);

                        FirebaseFirestore.getInstance().collection(Constant.TAI_KHOAN).document(id).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showLoading(false);
                                }else {
                                    Log.e("HomeStayTNT", task.getException().getMessage());
                                }
                            }
                        });
                    }else {
                        showLoading(false);
                    }
                }else {
                    Log.e("HomeStayTNT", "No such document");
                }
            }
        });


    }

    public void showLoading(boolean load){
        if(load){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loging in.......");
            progressDialog.setTitle("Please wait.");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }else {
            if(progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
