package com.im_oregano007.convocraft.utils;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtils {

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
