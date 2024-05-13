package com.im_oregano007.convocraft.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.im_oregano007.convocraft.model.ChatroomModel;
import com.im_oregano007.convocraft.model.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidUtils {
    static List<String> userIds = new ArrayList<>();
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static void showToastShort(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUserName());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());
        intent.putExtra("onlineStatus",model.getOnlineStatus());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel otherModel = new UserModel();
        otherModel.setUserName(intent.getStringExtra("username"));
        otherModel.setPhone(intent.getStringExtra("phone"));
        otherModel.setUserId(intent.getStringExtra("userId"));
        otherModel.setFcmToken(intent.getStringExtra("fcmToken"));
        otherModel.setOnlineStatus(intent.getStringExtra("onlineStatus"));
        return otherModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void setImage(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.centerCropTransform()).into(imageView);
    }

    public static void setOnlineStatus(boolean isOnline, String uId){
        if(isOnline){
            FirebaseUtils.getUserDetails(uId).update("onlineStatus","online");
        } else {
            FirebaseUtils.getUserDetails(uId).update("onlineStatus","offline");

        }

    }
    public static void addToGroup(String userId){
        if(userId != null && !userId.equals("")){
            userIds.add(userId);
        }
    }

    public static List<String> getUserList(){
        return userIds;
    }
    public static int getUserListSize(){
        return userIds.size();
    }
    public static void clearUserList(){
        userIds.clear();
    }

    public static boolean isUserFromGroup(String userId, List<String> userIds){
        if(userIds.contains(userId)){
            return true;
        }

        return false;
    }

//    get or create chatroom reference
    public static String getOrCreateChatroomModel(String otherUserId){
        String chatroomID = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),otherUserId);

        FirebaseUtils.getChatroomReference(chatroomID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ChatroomModel chatroomModel;
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    chatroomModel = new ChatroomModel(
                            chatroomID,
                            Timestamp.now(),
                            Arrays.asList(FirebaseUtils.currentUserId(),otherUserId),
                            "",
                            "",
                            false

                    );

                    FirebaseUtils.getChatroomReference(chatroomID).set(chatroomModel);
                }
            }
        });

        return chatroomID;
    }

}
