package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText inputUsername;
    Button startBtn;
    ProgressBar usernameProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        inputUsername = findViewById(R.id.login_username);
        startBtn = findViewById(R.id.login_start_btn);
        usernameProgressBar = findViewById(R.id.username_progress_bar);

    }
}