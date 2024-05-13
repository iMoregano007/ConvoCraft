package com.im_oregano007.convocraft;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
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

    TextView otherUsername, emptyRecyclerViewText;
    ImageButton backBtn, messageSendBtn, imgSelectBtn;
    EditText inputMessage;
    RecyclerView chatRecyclerView;

    ImageView profilePic;
    String currentMsgId;
    TextView activeStatus;

    LinearLayout otherUserDetails;

    boolean cameFromSearchUser;
    ChatroomModel groupChatroom;
    boolean groupChat;
    String groupName = "GroupChat";
    ActivityResultLauncher<Intent> imagePicLauncher;
    Uri selectedImageUri;
    ImageView selectedImage;
    boolean isImage;
    RelativeLayout selectImageFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isImage = false;

        imagePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data!=null && data.getData()!=null){
                    selectedImageUri = data.getData();
                    selectImageFrame.setVisibility(View.VISIBLE);
                    inputMessage.setVisibility(View.INVISIBLE);
                    imgSelectBtn.setVisibility(View.INVISIBLE);
                    isImage = true;
                    AndroidUtils.setImage(this,selectedImageUri,selectedImage);
                }
            }
        });
        selectedImage = findViewById(R.id.selectedImage);
        selectImageFrame = findViewById(R.id.selectImageFrame);
        otherUsername = findViewById(R.id.other_username);
        backBtn = findViewById(R.id.back_button);
        messageSendBtn = findViewById(R.id.message_send_btn);
        inputMessage = findViewById(R.id.input_message);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        activeStatus = findViewById(R.id.otherUserStatus);
        otherUserDetails = findViewById(R.id.otherUserDetails);
        imgSelectBtn = findViewById(R.id.imgSelectBtn);
        emptyRecyclerViewText = findViewById(R.id.emptyRecyclerViewText);

        profilePic = findViewById(R.id.user_profile_picture);

        setOnlineStatus(true);
        chatroomID = getIntent().getStringExtra("chatroomID");
        groupChat = getIntent().getBooleanExtra("isGroupChat",false);
        if(chatroomID != null){
            FirebaseUtils.getChatroomReference(chatroomID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    groupChatroom = task.getResult().toObject(ChatroomModel.class);
                    if(groupChat){
                        groupName = groupChatroom.getGroupName();
                        otherUsername.setText(groupChatroom.getGroupName());
                        activeStatus.setVisibility(View.INVISIBLE);
                        FirebaseUtils.getGroupDPStorageRef().getDownloadUrl().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                Uri imgUri = task1.getResult();
                                AndroidUtils.setProfilePic(ChatActivity.this,imgUri,profilePic);
                            }
                        });
                    }

                }
            });
        }
        imgSelectBtn.setOnClickListener((v) ->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,384)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePicLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() ==0){
                    imgSelectBtn.setVisibility(View.VISIBLE);
                } else
                    imgSelectBtn.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });




         if(!groupChat){
             otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
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


         }

        otherUserDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDetails.class);
            intent.putExtra("isGroupChat",groupChat);
            intent.putExtra("chatroomID",chatroomID);
            if(groupChat)
                intent.putExtra("groupName",groupName);
            else
                AndroidUtils.passUserModelAsIntent(intent, otherUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        cameFromSearchUser = getIntent().getBooleanExtra("cameFromSearchSection",false);





        backBtn.setOnClickListener(v -> onBackPressed());








        getOrCreateChatroomModel();

        messageSendBtn.setOnClickListener(v -> {
            if(isImage){
                sendMessageToUser("sent an image",isImage);
                selectImageFrame.setVisibility(View.GONE);
                inputMessage.setVisibility(View.VISIBLE);
                imgSelectBtn.setVisibility(View.VISIBLE);
                return;
            }
            String message = inputMessage.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message, isImage);
        });

        setUpRecyclerView();



    }


    void setUpRecyclerView(){
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomID)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    if(value.isEmpty()){
                        emptyRecyclerViewText.setVisibility(View.VISIBLE);
                    } else{
                        emptyRecyclerViewText.setVisibility(View.GONE);
                    }
                }

            }
        });

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, ChatActivity.this);
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

    void sendMessageToUser(String message, boolean isImage){

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserId(),Timestamp.now(),"sent","",chatroomID,groupChat,isImage);


        FirebaseUtils.getChatroomMessageReference(chatroomID).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    currentMsgId = task.getResult().getId();
                    FirebaseUtils.getChatroomMessageReference(chatroomID).document(currentMsgId).update("msgId",currentMsgId);
                    inputMessage.setText("");
                    if(isImage && selectedImageUri!=null){
                        FirebaseUtils.getImageStorageReference(chatroomID,currentMsgId).putFile(selectedImageUri);
                    } else{
                        imgSelectBtn.setVisibility(View.VISIBLE);
                    }
                    sendNotificationToUsers(message);
                    chatroomModel.setLastMessageTimeStamp(Timestamp.now());
                    chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
                    chatroomModel.setLastMessage(currentMsgId);
                    FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);
                }
            }
        });


    }
//   send notification to all members(group members)
    void sendNotificationToUsers(String nMessage){

        int numberOfUsers = chatroomModel.getUserIds().size();
        for(int i = 0; i < numberOfUsers; i++){
            FirebaseUtils.allUsersCollectionReference().document(chatroomModel.getUserIds().get(i))
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel user = task.getResult().toObject(UserModel.class);
                            if(user.getUserId()!=FirebaseUtils.currentUserId()){
                                sendNotification(nMessage,user.getFcmToken());
                            }
                        }
            });

        }
    }

    @Override
    public void onBackPressed() {

        Intent mainIntent;
        if (cameFromSearchUser) {
            mainIntent = new Intent(this, SearchUserActivity.class);
        } else {
            mainIntent = new Intent(this, MainActivity.class);
        }

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        super.onBackPressed();

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

    void sendNotification(String message, String fcmToken){
        FirebaseUtils.getCurrentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                String title;
                if(groupChat){
                    title = currentUser.getUserName() +"("+groupName+")";
                } else {
                    title = currentUser.getUserName();
                }
                try{
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",title);
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",fcmToken);

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
        AndroidUtils.setOnlineStatus(isOnline,FirebaseUtils.currentUserId());
    }

}