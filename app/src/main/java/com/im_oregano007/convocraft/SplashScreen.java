package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class SplashScreen extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtils.isLoggedIn()){
                    intent = new Intent(SplashScreen.this,MainActivity.class);
                } else{
                    intent = new Intent(SplashScreen.this,LoginPhoneNumberActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },1000);
    }
}