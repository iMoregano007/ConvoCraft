package com.im_oregano007.convocraft.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameTextV.setText(model.getUserName());
        holder.phoneTextV.setText(model.getPhone());

        if(model.getUserId().equals(FirebaseUtils.currentUserId())){
            holder.usernameTextV.setText(model.getUserName()+" (Me)");
        }

        holder.itemView.setOnClickListener(v -> {
//            move to chat activity
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_view_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView usernameTextV, phoneTextV;
        ImageView profilePic;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextV = itemView.findViewById(R.id.username_text);
            phoneTextV = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.user_profile_picture);
        }
    }
}
