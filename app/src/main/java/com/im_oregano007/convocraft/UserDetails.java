package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class UserDetails extends AppCompatActivity {

    TextView usernameToolbar, mainUsername, mobile, activeStatus;
    ImageButton backBtn;
    ImageView otherUserProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        setOnlineStatus(true);
        usernameToolbar = findViewById(R.id.userDetails_username);
        mainUsername = findViewById(R.id.userDetail_Username2);
        mobile = findViewById(R.id.userDetail_mobile);
        activeStatus = findViewById(R.id.userDetail_onlineStatus);
        otherUserProfilePic = findViewById(R.id.profile_picture_userDetails);
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        UserModel otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
        updateDetails(otherUser);
    }
// trying to solve a prblm
//    @Override
//    protected void onDestroy() {
//        setOnlineStatus(false);
//        super.onDestroy();
//    }

    void updateDetails(UserModel otherUser){
        usernameToolbar.setText(otherUser.getUserName());
        mainUsername.setText(otherUser.getUserName());
        mobile.setText(otherUser.getPhone());
//        String isActive = otherUser.getOnlineStatus();
//        Log.i("username",otherUser.getUserName());
//        Log.i("Active status",isActive);
        if(otherUser.getOnlineStatus()!=null){
            activeStatus.setText(otherUser.getOnlineStatus());
        } else {
            activeStatus.setText("Offline");
        }

        FirebaseUtils.getOtherUserProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful()){
                Uri imageUri = t.getResult();
                AndroidUtils.setProfilePic(this,imageUri,otherUserProfilePic);
            }

        });
    }

    void setOnlineStatus(boolean isOnline){
        AndroidUtils.setOnlineStatus(isOnline);
    }

}