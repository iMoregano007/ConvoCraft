package com.im_oregano007.convocraft;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;
import com.im_oregano007.convocraft.adapters.GroupMembersDetailsAdapter;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UserDetails extends AppCompatActivity {

    TextView usernameToolbar, mainUsername, mobile, activeStatus, usernameHolder;
    ImageButton backBtn;
    ImageView otherUserProfilePic;
    boolean isGroupChat;
    String chatroomId;
    RecyclerView groupMembersRecyclerV;
    GroupMembersDetailsAdapter adapter;
    LinearLayout groupDetails;
    Uri selectedImageUri;
    ActivityResultLauncher<Intent> imagePicker;
    Button updatePhotoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        isGroupChat = getIntent().getBooleanExtra("isGroupChat",false);
        chatroomId = getIntent().getStringExtra("chatroomID");
        updatePhotoBtn = findViewById(R.id.updatePhotoBtn);


        if(isGroupChat){
            imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if(data!=null && data.getData()!=null){
                        selectedImageUri = data.getData();
                        AndroidUtils.setProfilePic(UserDetails.this,selectedImageUri,otherUserProfilePic);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        setOnlineStatus(true);
        usernameToolbar = findViewById(R.id.userDetails_username);
        mainUsername = findViewById(R.id.userDetail_Username2);
        mobile = findViewById(R.id.userDetail_mobile);
        activeStatus = findViewById(R.id.userDetail_onlineStatus);
        otherUserProfilePic = findViewById(R.id.profile_picture_userDetails);
        backBtn = findViewById(R.id.back_button);
        usernameHolder = findViewById(R.id.usernameColumn);
        groupDetails = findViewById(R.id.groupDetails);
        groupMembersRecyclerV = findViewById(R.id.groupMembersRecyclerView);


        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if(isGroupChat){
            String groupName = getIntent().getStringExtra("groupName");
            usernameToolbar.setText(groupName);
            mainUsername.setText(groupName);
            usernameHolder.setVisibility(View.GONE);
            groupDetails.setVisibility(View.VISIBLE);
            setUpRecyclerView();
            setPhoto(chatroomId);
            otherUserProfilePic.setOnClickListener((v) ->{
                updatePhotoBtn.setVisibility(View.VISIBLE);
                ImagePicker.with(UserDetails.this).cropSquare().compress(512).maxResultSize(512,512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePicker.launch(intent);
                                return null;
                            }
                        });
                adapter.notifyDataSetChanged();
            });
            updatePhotoBtn.setOnClickListener(v-> updatePhoto(chatroomId));
        } else{
            UserModel otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
            updateDetails(otherUser);
        }

    }
    void updatePhoto(String groupId){
        FirebaseUtils.getGroupDPStorageRef().putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    AndroidUtils.showToastShort(UserDetails.this,"Photo Updated Successfully");
                    updatePhotoBtn.setVisibility(View.GONE);
                } else {
                    AndroidUtils.showToastShort(UserDetails.this,"Something went Wrong");
                }

            }
        });
    }
    void setPhoto(String groupId){
        FirebaseUtils.getGroupDPStorageRef().getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful()){
                Uri imageUri = t.getResult();
                AndroidUtils.setProfilePic(this,imageUri,otherUserProfilePic);
            }

        });
    }
    void setUpRecyclerView(){
        Query query = FirebaseUtils.allUsersCollectionReference()
                .whereArrayContains("allGroupsList",chatroomId);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new GroupMembersDetailsAdapter(options,UserDetails.this);
        groupMembersRecyclerV.setLayoutManager(new LinearLayoutManager(this));
        groupMembersRecyclerV.setAdapter(adapter);
        adapter.startListening();
    }


    void updateDetails(UserModel otherUser){
        usernameToolbar.setText(otherUser.getUserName());
        mainUsername.setText(otherUser.getUserName());
        mobile.setText(otherUser.getPhone());

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

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!= null){
            adapter.startListening();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!= null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!= null){
            adapter.notifyDataSetChanged();
        }
    }

    void setOnlineStatus(boolean isOnline){
        AndroidUtils.setOnlineStatus(isOnline);
    }

}