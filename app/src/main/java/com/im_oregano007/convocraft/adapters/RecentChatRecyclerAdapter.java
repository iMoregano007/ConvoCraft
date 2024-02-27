package com.im_oregano007.convocraft.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.im_oregano007.convocraft.ChatActivity;
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.model.ChatMessageModel;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {

        if(model.isGroup()){
            if(AndroidUtils.isUserFromGroup(FirebaseUtils.currentUserId(),model.getUserIds())){
                holder.usernameTextV.setText(model.getGroupName());
            }

        } else {
            FirebaseUtils.getOtherUserFromChatroom(model.getUserIds())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                            UserModel otherUser = task.getResult().toObject(UserModel.class);
                            if(otherUser.getUserId().equals(FirebaseUtils.currentUserId())){
                                holder.usernameTextV.setText(otherUser.getUserName()+" (Me)");
                            } else {
                                holder.usernameTextV.setText(otherUser.getUserName());
                            }

                            FirebaseUtils.getOtherUserProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                                if(t.isSuccessful()){
                                    Uri imageUri = t.getResult();
                                    AndroidUtils.setProfilePic(context,imageUri,holder.profilePic);
                                }

                            });

                            holder.itemView.setOnClickListener(v -> {
//            move to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtils.passUserModelAsIntent(intent, otherUser);
                                intent.putExtra("chatroomID",model.getChatroomId());
                                intent.putExtra("isGroupChat",model.isGroup());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });

                        }
                    });
        }
//change last message setting implementation
        boolean lastMsgSentByMe = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserId());
        FirebaseUtils.getChatroomMessageReference(model.getChatroomId()).document(model.getLastMessage()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    ChatMessageModel messageModel = task.getResult().toObject(ChatMessageModel.class);
                    if(messageModel != null){
                        holder.lastMessageSeenStatus.setVisibility(View.VISIBLE);
                        if(lastMsgSentByMe){
                            holder.lastMessageTextV.setText("You : "+messageModel.getMessage());
                            holder.lastMessageSeenStatus.setText(messageModel.getSeenStatus());
                        } else {
                            holder.lastMessageTextV.setText(messageModel.getMessage());
                            if(messageModel.getSeenStatus().equals("sent")){
                                holder.lastMessageSeenStatus.setText("new");
                            } else{
                                holder.lastMessageSeenStatus.setText(messageModel.getSeenStatus());
                            }
                        }

                    }else {
                            if(lastMsgSentByMe){
                                holder.lastMessageTextV.setText("You : "+model.getLastMessage());
                            } else
                                holder.lastMessageTextV.setText(model.getLastMessage());
                    }

                }
            }
        });
        holder.lastMessageTimeTextV.setText(FirebaseUtils.timestampToString(model.getLastMessageTimeStamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatroomID",model.getChatroomId());
            intent.putExtra("isGroupChat",model.isGroup());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });



    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{

        TextView usernameTextV, lastMessageTextV, lastMessageTimeTextV, lastMessageSeenStatus;
        ImageView profilePic;
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextV = itemView.findViewById(R.id.username_text);
            lastMessageTextV = itemView.findViewById(R.id.last_message_text);
            lastMessageTimeTextV = itemView.findViewById(R.id.last_msg_time_text);
            lastMessageSeenStatus = itemView.findViewById(R.id.last_msg_seenStatus);
            profilePic = itemView.findViewById(R.id.user_profile_picture);
        }
    }
}
