package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.im_oregano007.convocraft.adapters.RecentChatRecyclerAdapter;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {


    RecyclerView mainScreenRecyclerV;
    RecentChatRecyclerAdapter adapter;
    ImageView profilePic ,searchBtn, createGroup;
    FloatingActionButton chatWithAiBtn;

    ShimmerFrameLayout shimmerFrameLayout;
    TextView emptyRecyclerViewText;
    String currentUserId;


private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBtn = findViewById(R.id.searchUserBtn);
        createGroup = findViewById(R.id.createGroupBtn);
        chatWithAiBtn = findViewById(R.id.chatWithAi);
        mainScreenRecyclerV = findViewById(R.id.recyclerView);
        mainScreenRecyclerV.setVisibility(View.INVISIBLE);
        profilePic = findViewById(R.id.mainScreenProfilePic);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_recent_chats);
        emptyRecyclerViewText = findViewById(R.id.emptyRecyclerViewText);


        currentUserId = FirebaseUtils.currentUserId();
        profilePic.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, ProfileViewActivity.class));

        });
        chatWithAiBtn.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, ChatgptScreen.class));

        });

        setOnlineStatus(true);

        if(!checkNotificationPermission()){
            requestNotificationPermission();
        }

        searchBtn.setOnClickListener((v) ->{
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });
        createGroup.setOnClickListener( (v) -> {
            startActivity(new Intent(MainActivity.this,CreateGroup.class));
        });

        setUpRecyclerView();
        setUserDetails();

        getFCMToken();


    }


    void setUserDetails(){

            FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri imageUri = task.getResult();
                        AndroidUtils.setProfilePic(MainActivity.this,imageUri,profilePic);
                    }
                }
            });

    }

    void setUpRecyclerView(){
        Query query = FirebaseUtils.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtils.currentUserId())
                .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                assert value != null;
                if(value != null){
                    if(value.isEmpty()){
                        emptyRecyclerViewText.setVisibility(View.VISIBLE);
                    } else{
                        emptyRecyclerViewText.setVisibility(View.GONE);
                    }
                }

            }
        });

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();
        hideShimmerEffect();
        adapter = new RecentChatRecyclerAdapter(options,this);
        mainScreenRecyclerV.setLayoutManager(new LinearLayoutManager(this));
        mainScreenRecyclerV.setAdapter(adapter);
        adapter.startListening();
    }

    void hideShimmerEffect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScreenRecyclerV.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        },1500);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnlineStatus(true);
        if(adapter!= null){
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setOnlineStatus(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!= null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!= null){
            adapter.startListening();
        }
    }

    @Override
    protected void onDestroy() {
        setOnlineStatus(false);
        super.onDestroy();
    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtils.getCurrentUserDetails().update("fcmToken",token);
            }
        });
    }


    private boolean checkNotificationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show a dialog to explain why the permission is needed
                showPermissionExplanationDialog();
            }
        }
    }

    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Permission Required");
        builder.setMessage("This app requires notification permission to show notifications. Please grant the permission in settings.");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Open the app settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

        // Permission is not granted, request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
        // Permission already granted
        Toast.makeText(this, "Notification permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    void setOnlineStatus(boolean isOnline){
        AndroidUtils.setOnlineStatus(isOnline, currentUserId);
    }

}