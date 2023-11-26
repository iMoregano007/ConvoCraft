package com.im_oregano007.convocraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.im_oregano007.convocraft.model.ChatMessageModel;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomID;

    ChatroomModel chatroomModel;

    TextView otherUsername;
    ImageButton backBtn, messageSendBtn;
    EditText inputMessage;
    RecyclerView chatRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
        chatroomID = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),otherUser.getUserId());

        otherUsername = findViewById(R.id.other_username);
        backBtn = findViewById(R.id.back_button);
        messageSendBtn = findViewById(R.id.message_send_btn);
        inputMessage = findViewById(R.id.input_message);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener( v -> {
            onBackPressed();
        });



        otherUsername.setText(otherUser.getUserName());
        if(FirebaseUtils.currentUserId().equals(otherUser.getUserId())){
            otherUsername.setText(otherUser.getUserName()+" (Me)");
        } else{
            otherUsername.setText(otherUser.getUserName());
        }

        getOrCreateChatroomModel();

        messageSendBtn.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });
    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimeStamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserId(),Timestamp.now());
        FirebaseUtils.getChatroomMessageReference(chatroomID).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    inputMessage.setText("");
                }
            }
        });
    }
    void getOrCreateChatroomModel(){
        FirebaseUtils.getChatroomReference(chatroomID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    chatroomModel = new ChatroomModel(
                            chatroomID,
                            Timestamp.now(),
                            Arrays.asList(FirebaseUtils.currentUserId(),otherUser.getUserId()),
                            ""

                    );

                    FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);
                }
            }
        });
    }
}