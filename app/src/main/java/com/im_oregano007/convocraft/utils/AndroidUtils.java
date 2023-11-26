package com.im_oregano007.convocraft.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.im_oregano007.convocraft.model.UserModel;

public class AndroidUtils {

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUserName());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel otherModel = new UserModel();
        otherModel.setUserName(intent.getStringExtra("username"));
        otherModel.setPhone(intent.getStringExtra("phone"));
        otherModel.setUserId(intent.getStringExtra("userId"));
        return otherModel;
    }
}
