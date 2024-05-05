package com.im_oregano007.convocraft.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.im_oregano007.convocraft.ViewImageFullScreen;
import com.im_oregano007.convocraft.model.ChatMessageModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
//        can set timestamp as well from here
        String chatroomID = model.getChatroomID();
        boolean isGroup = model.isGroup();
        boolean isImage = model.isImage();
        if( chatroomID == null ||chatroomID.isEmpty()){
            chatroomID = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),model.getSenderId());
        }
        String seenS = model.getSeenStatus();
        if(model.getSenderId().equals(FirebaseUtils.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTimestamp.setText(FirebaseUtils.timestampToString(model.getTimestamp(),false));
            if(isImage && !model.getMsgId().equals("")){
                holder.rightImageV.setVisibility(View.VISIBLE);
                holder.rightChatTextV.setVisibility(View.GONE);
                FirebaseUtils.getImageStorageReference(chatroomID,model.getMsgId()).getDownloadUrl().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri imageUri = task.getResult();
                        AndroidUtils.setImage(context,imageUri,holder.rightImageV);
                    }

                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImageFullView(model.getChatroomID(), model.getMsgId());
                    }
                });
            }else{
//                holder.rightChatTextV.setVisibility(View.VISIBLE);
                holder.rightChatTextV.setText(model.getMessage());

            }

            if(seenS==null){
                holder.seenStatus.setText("sent");
            } else {
                holder.seenStatus.setText(seenS);
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                showDialogBox(model.getMsgId(), model.getChatroomID());
                return true;
                }
            });
        } else {
            if(isGroup){
                holder.otherUserName.setVisibility(View.VISIBLE);
                FirebaseUtils.allUsersCollectionReference().document(model.getSenderId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            UserModel otherUser = task.getResult().toObject(UserModel.class);
                            holder.otherUserName.setText(otherUser.getUserName());
                        }
                    }
                });
            } else {
                holder.otherUserName.setVisibility(View.GONE);
            }

            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);

            if(isImage && !model.getMsgId().equals("")){
                holder.leftImageV.setVisibility(View.VISIBLE);
                holder.leftChatTextV.setVisibility(View.GONE);
                FirebaseUtils.getImageStorageReference(chatroomID,model.getMsgId()).getDownloadUrl().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri imageUri = task.getResult();
                        AndroidUtils.setImage(context,imageUri,holder.leftImageV);
                    }

                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImageFullView(model.getChatroomID(), model.getMsgId());
                    }
                });
            }else{
//                holder.rightChatTextV.setVisibility(View.VISIBLE);
                holder.leftChatTextV.setText(model.getMessage());

            }


            holder.leftChatTimestamp.setText(FirebaseUtils.timestampToString(model.getTimestamp(),false));
            if(seenS == null || seenS.equals("sent")){
                holder.msgStatus.setVisibility(View.VISIBLE);
                holder.msgStatus.setText("new");
                if(!model.getMsgId().equals("")){
                    FirebaseUtils.getChatroomMessageReference(chatroomID).document(model.getMsgId()).update("seenStatus","seen");
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.msgStatus.setVisibility(View.GONE);
                    }
                },1000);
            }
        }
    }
    void showDialogBox(String msgID, String chatroomID){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Delete Message");
        alertDialog.setMessage("Do you want to Delete this message?");
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUtils.DeleteMessage(msgID, chatroomID);
                AndroidUtils.showToastShort(context,"Message Deleted Successfully");
            }
        });

        alertDialog.show();
    }
    void openImageFullView(String chatroomId, String msgId){
        Intent intent = new Intent(context, ViewImageFullScreen.class);
        intent.putExtra("chatroomID",chatroomId);
        intent.putExtra("msgId",msgId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_view,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout, rightChatLayout;
        ImageView leftImageV, rightImageV;
        TextView leftChatTextV, rightChatTextV, leftChatTimestamp, rightChatTimestamp, seenStatus, msgStatus, otherUserName;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_message_layout);
            rightChatLayout = itemView.findViewById(R.id.right_message_layout);
            leftChatTextV = itemView.findViewById(R.id.left_chat_textv);
            rightChatTextV = itemView.findViewById(R.id.right_chat_textv);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            seenStatus = itemView.findViewById(R.id.seen_status);
            msgStatus = itemView.findViewById(R.id.msg_status);
            otherUserName = itemView.findViewById(R.id.other_username);
            leftImageV = itemView.findViewById(R.id.leftImageV);
            rightImageV = itemView.findViewById(R.id.rightImageV);


        }
    }
}
