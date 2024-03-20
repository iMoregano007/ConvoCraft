package com.im_oregano007.convocraft.adapters;

import android.content.Context;
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
import com.im_oregano007.convocraft.R;
import com.im_oregano007.convocraft.model.UserModel;
import com.im_oregano007.convocraft.utils.AndroidUtils;
import com.im_oregano007.convocraft.utils.FirebaseUtils;

public class GroupMembersDetailsAdapter extends FirestoreRecyclerAdapter<UserModel , GroupMembersDetailsAdapter.GroupMembersDetails> {


   Context context;
    public GroupMembersDetailsAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupMembersDetails holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUserName());
        holder.phoneText.setText(model.getPhone());

        FirebaseUtils.getOtherUserProfilePicStorageRef(model.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidUtils.setProfilePic(context,uri,holder.profilePic);
                }
            }
        });
    }

    @NonNull
    @Override
    public GroupMembersDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_members_details,parent,false);
        return new GroupMembersDetails(view);
    }

    public class GroupMembersDetails extends RecyclerView.ViewHolder{

        TextView usernameText, phoneText;
        ImageView profilePic;

        public GroupMembersDetails(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            profilePic = itemView.findViewById(R.id.user_profile_picture);
            phoneText = itemView.findViewById(R.id.phone_text);
        }
    }
}
