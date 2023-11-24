package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText inputUsername;
    Button startBtn;
    ProgressBar usernameProgressBar;

    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        inputUsername = findViewById(R.id.login_username);
        startBtn = findViewById(R.id.login_start_btn);
        usernameProgressBar = findViewById(R.id.username_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        startBtn.setOnClickListener((v -> {
            setUsername();
        }));


    }

    void setUsername(){
        changeInProgress(true);
        String userName = inputUsername.getText().toString();
        if(userName.isEmpty() || userName.length() < 3){
            inputUsername.setError("Username length should be at least 3 chars");
            changeInProgress(false);
            return;
        }
        if(userModel != null){
            userModel.setUserName(userName);
        } else{
            userModel = new UserModel(userName, phoneNumber, Timestamp.now());
        }
        FirebaseUtils.getCurrentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUsernameActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        
    }

    void getUsername(){
        changeInProgress(true);
        FirebaseUtils.getCurrentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel != null){
                        inputUsername.setText(userModel.getUserName());
                    }
                }
            }
        });
    }


    void changeInProgress(boolean isProgress){
        if(isProgress){
            usernameProgressBar.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.INVISIBLE);
        } else{
            usernameProgressBar.setVisibility(View.INVISIBLE);
            startBtn.setVisibility(View.VISIBLE);
        }
    }
}