package com.im_oregano007.convocraft.chatgpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.im_oregano007.convocraft.R;

import java.util.List;

public class ChatGptAdapter extends RecyclerView.Adapter<ChatGptAdapter.MyViewHolder> {


    List<ChatGptMessage> messageList;
    public ChatGptAdapter(List<ChatGptMessage> messageList){
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatgpt_msg_row,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatGptMessage message = messageList.get(position);
        if(message.getSentBy().equals(ChatGptMessage.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatText.setText(message.getMessage());
        } else{
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftChatText.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatView, rightChatView;
        TextView leftChatText, rightChatText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_message_layout);
            leftChatText = itemView.findViewById(R.id.left_chat_textv);
            rightChatView = itemView.findViewById(R.id.right_message_layout);
            rightChatText = itemView.findViewById(R.id.right_chat_textv);

        }
    }
}
