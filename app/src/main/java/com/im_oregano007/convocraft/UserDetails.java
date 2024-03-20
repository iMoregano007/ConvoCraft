package com.im_oregano007.convocraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.im_oregano007.convocraft.adapters.GroupMembersDetailsAdapter;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class UserDetails extends AppCompatActivity {

    TextView usernameToolbar, mainUsername, mobile, activeStatus, usernameHolder;
    ImageButton backBtn;
    ImageView otherUserProfilePic;
    boolean isGroupChat;
    String chatroomId;
    RecyclerView groupMembersRecyclerV;
    GroupMembersDetailsAdapter adapter;
    LinearLayout groupDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        isGroupChat = getIntent().getBooleanExtra("isGroupChat",false);
        chatroomId = getIntent().getStringExtra("chatroomID");



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

        } else{
            UserModel otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
            updateDetails(otherUser);
        }

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