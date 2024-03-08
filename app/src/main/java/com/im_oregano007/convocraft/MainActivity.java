package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout searchBtn, createGroup, recentChats, chatWithAiBtn, profileBtn;
//    ChatFragment chatFragment;
//    AiCornerFragment aiCornerFragment;
//    ProfileFragment profileFragment;


private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        chatFragment = new ChatFragment();
//        aiCornerFragment = new AiCornerFragment();
//        profileFragment = new ProfileFragment();
//        bottomNavigationView = findViewById(R.id.main_navigation_view);
        searchBtn = findViewById(R.id.search_button);
        createGroup = findViewById(R.id.create_group);
        profileBtn = findViewById(R.id.profileBtn);
        chatWithAiBtn = findViewById(R.id.chatWithAiBtn);
        recentChats = findViewById(R.id.recentChats);

        recentChats.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, RecentChats.class));
        });
        profileBtn.setOnClickListener(v ->{
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

//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                if(item.getItemId() == R.id.menu_chat){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,chatFragment).commit();
//                }
//                if(item.getItemId() == R.id.menu_ai){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,aiCornerFragment).commit();
//                }
//                if(item.getItemId() == R.id.menu_profile){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,profileFragment).commit();
//                }
//                return true;
//            }
//        });

//        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();

    }


    @Override
    protected void onResume() {
        super.onResume();
        setOnlineStatus(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setOnlineStatus(true);
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
        AndroidUtils.setOnlineStatus(isOnline);
    }

}