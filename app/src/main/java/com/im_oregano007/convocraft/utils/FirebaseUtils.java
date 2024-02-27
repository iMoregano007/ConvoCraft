package com.im_oregano007.convocraft.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.im_oregano007.convocraft.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseUtils {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId() != null){
            return true;
        } else
            return false;

    }
    public static DocumentReference getCurrentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUsersCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"-"+userId2;
        } else{
            return userId2+"-"+userId1;
        }
    }

    public  static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtils.currentUserId())){
            return allUsersCollectionReference().document(userIds.get(1));
        } else {
            return allUsersCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp, boolean isRecentChat){
        Date date = timestamp.toDate();

        // Format the date in HH:mm format
        SimpleDateFormat sdf;
        if(isRecentChat){
            sdf = new SimpleDateFormat("HH:mm");

        } else{
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        }
        return sdf.format(date);

    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic").child(FirebaseUtils.currentUserId());
    }
    public static StorageReference getImageStorageReference(String chatroomId, String uri){
        return FirebaseStorage.getInstance().getReference().child("chat_images").child(chatroomId).child(uri);
    }

    public static StorageReference getOtherUserProfilePicStorageRef(String otherUserID){
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic").child(otherUserID);
    }




}
