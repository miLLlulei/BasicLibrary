package com.mill.thread;

import android.content.ClipData;
import android.content.Context;
import android.text.TextUtils;

import com.mill.utils.ContextUtils;

public class Utils {
    public static boolean copyToClipboard(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ContextUtils.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", content);
        clipboard.setPrimaryClip(clip);
        return true;
    }
}
