package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class ViewImageFullScreen extends AppCompatActivity {

    ImageButton backBtn;
    ImageView imageFullView;

    String chatroomId, msgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_full_screen);

        backBtn = findViewById(R.id.backBtn);
        imageFullView = findViewById(R.id.imageFullView);

        chatroomId = getIntent().getStringExtra("chatroomID");
        msgId = getIntent().getStringExtra("msgId");


        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        if(chatroomId != null && msgId != null){
            FirebaseUtils.getImageStorageReference(chatroomId,msgId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri imagUri = task.getResult();
                        AndroidUtils.setImage(ViewImageFullScreen.this,imagUri,imageFullView);
                    }
                }
            });
        }



    }
}