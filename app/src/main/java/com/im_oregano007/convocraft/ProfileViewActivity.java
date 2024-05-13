package com.im_oregano007.convocraft;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileViewActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText usernameInput,phoneNumber;
    ProgressBar progressBar;
    Button updateProfileBtn;
    TextView logoutBtn;
    UserModel currentUser;
    String oldUsername;
    ActivityResultLauncher<Intent> imagePicLauncher;
    Uri selectedImageUri;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout dataView;
    ImageButton backBtn;
    Uri shUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        imagePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data!=null && data.getData()!=null){
                    selectedImageUri = data.getData();
                    AndroidUtils.setProfilePic(this,selectedImageUri,profilePic);
                }
            }
        });

        profilePic = findViewById(R.id.profile_picture_das);
        usernameInput = findViewById(R.id.profile_username);
        phoneNumber = findViewById(R.id.profile_phone);
        progressBar = findViewById(R.id.profile_progress_bar);
        logoutBtn = findViewById(R.id.logout_btn);
        updateProfileBtn = findViewById(R.id.profile_update_btn);
        shimmerFrameLayout = findViewById(R.id.shimmer_effect_profile);
        dataView = findViewById(R.id.data_view);
        dataView.setVisibility(View.INVISIBLE);
        backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> onBackPressed());

        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
//                    AndroidUtils.setOnlineStatus(false);
                    FirebaseUtils.logout();
                    AndroidUtils.showToastShort(this,"Logged Out Successfully");
                    Intent intent = new Intent(this, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });


        });

        profilePic.setOnClickListener((v) ->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePicLauncher.launch(intent);
                            return null;
                        }
                    });
        });




    }

    public void onBackPressed() {

        Intent mainIntent;
        mainIntent = new Intent(this, MainActivity.class);

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        super.onBackPressed();

    }

    void updateBtnClick(){
        String newUserName = usernameInput.getText().toString();
        if(newUserName.isEmpty() || newUserName.length() < 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }

        if(selectedImageUri!=null){
            changeInProgress(true);
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        changeInProgress(false);
                    });
        }
        if(!oldUsername.equals(newUserName.trim())){
            currentUser.setUserName(newUserName);
            changeInProgress(true);
            updateToFireStore();
        }




    }

    void updateToFireStore(){
        FirebaseUtils.getCurrentUserDetails().set(currentUser).addOnCompleteListener(task -> {
            changeInProgress(false);
            if(task.isSuccessful()){
                AndroidUtils.showToastShort(this,"Update successfully");
            } else{
                AndroidUtils.showToastShort(this,"Update Failed");
            }
        });
    }

    void getUserData(){
        hideShimmerEffect();


        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri imageUri = task.getResult();
                AndroidUtils.setProfilePic(this,imageUri,profilePic);
            }

        });

        changeInProgress(true);
        FirebaseUtils.getCurrentUserDetails().get().addOnCompleteListener(task -> {
            changeInProgress(false);
            if(task.isSuccessful()){
                currentUser = task.getResult().toObject(UserModel.class);
                oldUsername = currentUser.getUserName();
                usernameInput.setText(oldUsername);
                phoneNumber.setText(currentUser.getPhone());

            }
        });
    }

    void changeInProgress(boolean isProgress){
        if(isProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.INVISIBLE);
        } else{
            progressBar.setVisibility(View.INVISIBLE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    void hideShimmerEffect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        },500);

    }
}