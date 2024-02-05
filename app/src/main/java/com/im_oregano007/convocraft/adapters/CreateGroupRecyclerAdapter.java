package com.im_oregano007.convocraft.adapters;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.im_oregano007.convocraft.CreateGroup;
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class CreateGroupRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, CreateGroupRecyclerAdapter.UserModelViewHolder> {

    Context context;
    public CreateGroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {


        if(!model.getUserId().equals(FirebaseUtils.currentUserId())){
            holder.userRow.setVisibility(View.VISIBLE);
            holder.usernameTextV.setText(model.getUserName());
            holder.phoneTextV.setText(model.getPhone());
            FirebaseUtils.getOtherUserProfilePicStorageRef(model.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                if(t.isSuccessful()){
                    Uri imageUri = t.getResult();
                    AndroidUtils.setProfilePic(context,imageUri,holder.profilePic);
                }

            });
        } else {
            holder.userRow.setVisibility(View.GONE);
        }



        holder.itemView.setOnClickListener(v -> {
            holder.selectSign.setVisibility(View.VISIBLE);
            AndroidUtils.addToGroup(model.getUserId());

//            move to chat activity

        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView usernameTextV, phoneTextV;
        LinearLayout userRow;
        ImageView profilePic, selectSign;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextV = itemView.findViewById(R.id.username_text);
            phoneTextV = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.user_profile_picture);
            selectSign = itemView.findViewById(R.id.select_sign);
            userRow = itemView.findViewById(R.id.user_row);
        }
    }
}
