package com.im_oregano007.convocraft;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.im_oregano007.convocraft.adapters.ChatRecyclerAdapter;
import com.im_oregano007.convocraft.adapters.SearchUserRecyclerAdapter;
import com.im_oregano007.convocraft.model.ChatMessageModel;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomID;

    ChatroomModel chatroomModel;

    ChatRecyclerAdapter adapter;

    TextView otherUsername;
    ImageButton backBtn, messageSendBtn;
    EditText inputMessage;
    RecyclerView chatRecyclerView;

    ImageView profilePic;
//    seenStatus applying code
    String currentMsgId;
//    trying to add active status
    TextView activeStatus;

    LinearLayout otherUserDetails;

    boolean cameFromSearchUser;
//    group chat feature
    ChatroomModel groupChatroom;
    boolean groupChat;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUsername = findViewById(R.id.other_username);
        backBtn = findViewById(R.id.back_button);
        messageSendBtn = findViewById(R.id.message_send_btn);
        inputMessage = findViewById(R.id.input_message);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        activeStatus = findViewById(R.id.otherUserStatus);
        otherUserDetails = findViewById(R.id.otherUserDetails);

        profilePic = findViewById(R.id.user_profile_picture);

        setOnlineStatus(true);
        chatroomID = getIntent().getStringExtra("chatroomID");
        groupChat = getIntent().getBooleanExtra("isGroupChat",false);
        if(chatroomID != null){
            FirebaseUtils.getChatroomReference(chatroomID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    groupChatroom = task.getResult().toObject(ChatroomModel.class);
                    groupName = groupChatroom.getGroupName();
                    if(groupChat){
                        if(groupName != null){
                            otherUsername.setText(groupChatroom.getGroupName());
                        } else {
                            otherUsername.setText("GroupChat");
                        }

                        activeStatus.setVisibility(View.INVISIBLE);
                        otherUserDetails.setEnabled(false);
                    }

                }
            });
        }




         if(!groupChat){
             otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
//             set profile pic of other user
             FirebaseUtils.getOtherUserProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                 if(t.isSuccessful()){
                     Uri imageUri = t.getResult();
                     AndroidUtils.setProfilePic(this,imageUri,profilePic);
                 }

             });

             otherUsername.setText(otherUser.getUserName());
//        online status
             if(otherUser.getOnlineStatus()!= null){
                 activeStatus.setText(otherUser.getOnlineStatus());
             } else {
                 activeStatus.setText("Offline");
             }

             if(FirebaseUtils.currentUserId().equals(otherUser.getUserId())){
                 otherUsername.setText(otherUser.getUserName()+" (Me)");
             } else{
                 otherUsername.setText(otherUser.getUserName());
             }


             otherUserDetails.setOnClickListener(v -> {
                 Intent intent = new Intent(this, UserDetails.class);
                 AndroidUtils.passUserModelAsIntent(intent, otherUser);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
             });
         }

//        chatroomID = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),otherUser.getUserId());
//        online status


//        trying to solve bug
        cameFromSearchUser = getIntent().getBooleanExtra("cameFromSearchSection",false);


//        onBackPressed alternative code working partially part 1


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackBtnClick();
            }
        });








        getOrCreateChatroomModel();

        messageSendBtn.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });

        setUpRecyclerView();

//        trying to show OtherUser profile

    }

    @Override
    protected void onDestroy() {
        setOnlineStatus(false);
        super.onDestroy();
    }

    void setUpRecyclerView(){
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(manager);
        chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){



        chatroomModel.setLastMessageTimeStamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserId(),Timestamp.now(),"sent","");
        FirebaseUtils.getChatroomMessageReference(chatroomID).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    currentMsgId = task.getResult().getId();
                    FirebaseUtils.getChatroomMessageReference(chatroomID).document(currentMsgId).update("msgId",currentMsgId);
                    inputMessage.setText("");
                    sendNotification(message);
                }
            }
        });

//      here is a problem with the seen status yet to be rectified later

//        FirebaseUtils.getChatroomMessageReference(chatroomID).document(currentMsgId).update("msgId",currentMsgId).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    inputMessage.setText("");
//                    sendNotification(message);
//                }
//            }
//        });

    }
//    trying to solve bug
    void onBackBtnClick(){
        if(cameFromSearchUser){
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);

        } else {
            getOnBackPressedDispatcher().onBackPressed();
        }
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
                            "",
                            "",
                            false

                    );

                    FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);
                }
            }
        });
    }

    void sendNotification(String message){
        FirebaseUtils.getCurrentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUserName());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApis(jsonObject);

                } catch (Exception e){

                }
            }
        });
    }


    void callApis(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAA3NEjjQM:APA91bHPRXKUWuGtKSL49YSMKGjGr-DIrH5Tba9oA96GYOpm1dzIbwJJObH6wszPAwpNX8MwAixhsevknPGvskJEPVtvo2tjtkArQ_hdiCbMZkjm6eR5CDLpj0UeG06qUhE7uKUjfjl5")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    void setOnlineStatus(boolean isOnline){
        AndroidUtils.setOnlineStatus(isOnline);
    }

}