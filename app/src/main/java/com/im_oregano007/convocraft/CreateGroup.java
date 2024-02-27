package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.im_oregano007.convocraft.adapters.CreateGroupRecyclerAdapter;
import com.im_oregano007.convocraft.adapters.SearchUserRecyclerAdapter;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateGroup extends AppCompatActivity {

    ImageButton backBtn, doneBtn;
    EditText inputGroupName;
    String groupName = "";
    CreateGroupRecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        backBtn = findViewById(R.id.back_button);
        doneBtn = findViewById(R.id.done_btn);
        inputGroupName = findViewById(R.id.input_group_name);
        recyclerView = findViewById(R.id.create_group_recycler_view);

        setupCreateGrpRecyclerView();

        backBtn.setOnClickListener(v -> {
            AndroidUtils.clearUserList();
            getOnBackPressedDispatcher().onBackPressed();
        });

        doneBtn.setOnClickListener( v ->{
            groupName = inputGroupName.getText().toString();
            if(groupName.length() < 3){
                inputGroupName.setError("Group Name should be more than 3 letters");
                return;
            } else {
                if(AndroidUtils.getUserListSize()>0){
                    AndroidUtils.addToGroup(FirebaseUtils.currentUserId());
                    createGroupChatroom();
                } else {
                    inputGroupName.setError("Group should contain more than 1 participant");
                    return;
                }
            }
            AndroidUtils.clearUserList();
            Intent intent = new Intent(CreateGroup.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

    }

    void setupCreateGrpRecyclerView(){
        Query query = FirebaseUtils.allUsersCollectionReference()
                .whereGreaterThanOrEqualTo("userName"," ");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new CreateGroupRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
            adapter.startListening();
        }
    }



    void createGroupChatroom(){
        ChatroomModel chatroomModel = new ChatroomModel(
                "",
                Timestamp.now(),
                AndroidUtils.getUserList(),
                "",
                groupName,
                true
        );

        FirebaseUtils.allChatroomCollectionReference().add(chatroomModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    String groupChatroomId = task.getResult().getId();
                    FirebaseUtils.getChatroomReference(groupChatroomId).update("chatroomId",groupChatroomId);
                }
            }
        });
    }


}