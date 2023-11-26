package com.im_oregano007.convocraft;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;


public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput,phoneNumber;
    ProgressBar progressBar;
    Button updateProfileBtn;
    TextView logoutBtn;
    UserModel currentUser;
    String oldUsername;


    public ProfileFragment() {
        // Required empty public constructor
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

        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseUtils.logout();
            AndroidUtils.showToastShort(getContext(),"Logged Out Successfully");
            Intent intent = new Intent(getContext(), SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    void updateBtnClick(){
        String newUserName = usernameInput.getText().toString();
        if(newUserName.isEmpty() || newUserName.length() < 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
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
}