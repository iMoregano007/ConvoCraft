package com.im_oregano007.convocraft.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.im_oregano007.convocraft.MainActivity;
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.RecentChats;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class MainScreenRecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, MainScreenRecentChatRecyclerAdapter.ChatroomViewHolder> {

    Context context;

    public MainScreenRecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position, @NonNull ChatroomModel model) {
        if(model.isGroup()){
            holder.usernameText.setText(model.getGroupName());
        } else{
            FirebaseUtils.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    UserModel user = task.getResult().toObject(UserModel.class);
                    if(user.getUserId().equals(FirebaseUtils.currentUserId())){
                        holder.usernameText.setText(user.getUserName()+"(Me)");
                    } else {
                        holder.usernameText.setText(user.getUserName());
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, RecentChats.class));
        });
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_screen_recent_chat_recycler_row,parent,false);
        return new ChatroomViewHolder(view);
    }

    public class ChatroomViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
        }
    }
}
