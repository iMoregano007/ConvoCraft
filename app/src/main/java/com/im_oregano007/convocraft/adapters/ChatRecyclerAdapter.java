package com.im_oregano007.convocraft.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.im_oregano007.convocraft.ChatActivity;
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.model.ChatMessageModel;
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
        String seenS = model.getSeenStatus();
        if(model.getSenderId().equals(FirebaseUtils.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextV.setText(model.getMessage());
            holder.rightChatTimestamp.setText(FirebaseUtils.timestampToString(model.getTimestamp()));
            if(seenS==null){
                holder.seenStatus.setText("sent");
            } else {
                holder.seenStatus.setText(seenS);
            }
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextV.setText(model.getMessage());
            holder.leftChatTimestamp.setText(FirebaseUtils.timestampToString(model.getTimestamp()));
            if(seenS == null || seenS.equals("sent")){
                holder.msgStatus.setVisibility(View.VISIBLE);
                holder.msgStatus.setText("new");
                if(!model.getMsgId().equals("")){
                    FirebaseUtils.getChatroomMessageReference(FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),model.getSenderId())).document(model.getMsgId()).update("seenStatus","seen");
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

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_view,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextV, rightChatTextV, leftChatTimestamp, rightChatTimestamp, seenStatus, msgStatus;
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


        }
    }
}
