package com.im_oregano007.convocraft;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {

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


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
           if(result.getResultCode() == Activity.RESULT_OK){
               Intent data = result.getData();
               if(data!=null && data.getData()!=null){
                    selectedImageUri = data.getData();
                    AndroidUtils.setProfilePic(getContext(),selectedImageUri,profilePic);
               }
           }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_picture_das);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneNumber = view.findViewById(R.id.profile_phone);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_effect_profile);
        dataView = view.findViewById(R.id.data_view);
        dataView.setVisibility(View.INVISIBLE);

        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUtils.logout();
                    AndroidUtils.showToastShort(getContext(),"Logged Out Successfully");
                    Intent intent = new Intent(getContext(), SplashScreen.class);
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

        return view;
    }

    void updateBtnClick(){
        String newUserName = usernameInput.getText().toString();
        if(newUserName.isEmpty() || newUserName.length() < 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }

        if(selectedImageUri!=null){
            changeInProgress(true);
            currentUser.setUserName(newUserName);
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFireStore();
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
                AndroidUtils.showToastShort(getContext(),"Update successfully");
            } else{
                AndroidUtils.showToastShort(getContext(),"Update Failed");
            }
        });
    }

    void getUserData(){
        hideShimmerEffect();

        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri imageUri = task.getResult();
                AndroidUtils.setProfilePic(getContext(),imageUri,profilePic);
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