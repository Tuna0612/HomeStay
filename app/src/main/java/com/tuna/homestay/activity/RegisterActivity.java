package com.tuna.homestay.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tuna.homestay.R;
import com.tuna.homestay.model.entity.CheckConnection;
import com.tuna.homestay.model.entity.Constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    private EditText edName;
    private EditText edEmail;
    private EditText edPass;
    private EditText edConfirmPass;
    private Button btnSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConnection.haveNetworkConnection(RegisterActivity.this)){
                    String name = edName.getText().toString().trim();
                    String email = edEmail.getText().toString().trim();
                    String pass = edPass.getText().toString().trim();
                    String cfpass = edConfirmPass.getText().toString().trim();

                    if(name.isEmpty()){
                        edName.setError("Hãy nhập vào đầy đủ !");
                    }else if(email.isEmpty()){
                        edEmail.setError("Hãy nhập vào đầy đủ !");
                    }else if(pass.isEmpty()){
                        edPass.setError("Hãy nhập vào đầy đủ !");
                    }else if(pass.length()<6){
                        edPass.setError("Password phải lớn hơn 6 ký tự");
                    }else if(cfpass.isEmpty()){
                        edConfirmPass.setError("Hãy nhập vào đầy đủ !");
                    }else if(!cfpass.equals(pass)){
                        edConfirmPass.setError("Không khớp với mật khẩu đã nhập");
                    }else {
                        createUser(name,email,pass);
                        showDialog(true);
                    }
                }else{
                    Toasty.warning(RegisterActivity.this,"Kiểm tra lại kết nối internet", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }

    private void createUser(final String name,final String email,final String pass) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Nếu đăng ks thành công
                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid();
                    Map<String,Object> data = new HashMap<>();
                    data.put(Constant.LINK_AVATAR,"https://scontent.fhan3-1.fna.fbcdn.net/v/t1.0-9/p960x960/45192024_2235753036643489_3860215978856022016_o.jpg?_nc_cat=102&_nc_oc=AQnObxe-l362DDKKH93IYvJsloBBxE_x85f64A9TJAfLC6Ms-CrTsD5loJlU1OW14-k&_nc_ht=scontent.fhan3-1.fna&oh=fb8f7ecfa1651d74cf17ace968d5acd0&oe=5E5560B3");
                    data.put(Constant.ID_USER,id);
                    data.put(Constant.NAME,name);
                    data.put(Constant.EMAIL,email);
                    data.put(Constant.ADDRESS,"empty");
                    data.put(Constant.PHONE,"empty");
                    data.put(Constant.SIGN_UP,"false");
                    mFirestore.collection(Constant.TAI_KHOAN).document(id).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Save Token vào database
                                String refreshToken = FirebaseInstanceId.getInstance().getToken();
                                String id= FirebaseAuth.getInstance().getCurrentUser().getUid();

                                Map<String,Object> data = new HashMap<>();
                                data.put(Constant.TOKEN, Arrays.asList(refreshToken));
                                FirebaseFirestore.getInstance().collection(Constant.TAI_KHOAN).document(id).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toasty.success(RegisterActivity.this,"Đăng ký thành công !",Toast.LENGTH_SHORT,true).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }else {
                                            Log.e("HomeStayTNT", task.getException().getMessage());
                                        }
                                    }
                                });
                            }else {
                                Toasty.error(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT,true).show();
                            }
                        }
                    });
                }else {
                    Toasty.error(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }

    private void initView() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        edConfirmPass = findViewById(R.id.edConfirmPass);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void showDialog(boolean load){
        if(load) {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Signing up...");
            progressDialog.show();
        }else{
            if(progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
