package com.vijay.locationviewer;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by vijay-3593 on 19/11/17.
 */

public class ToastUtils {
    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context, @StringRes int resourceID){
        Toast.makeText(context, resourceID, Toast.LENGTH_SHORT).show();
    }
}
