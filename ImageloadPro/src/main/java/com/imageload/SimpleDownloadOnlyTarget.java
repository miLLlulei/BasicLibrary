package com.imageload;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by lulei-ms on 2018/6/1.
 */
public class SimpleDownloadOnlyTarget implements Target<File> {
    @Override
    public void onLoadStarted(Drawable drawable) {
    }

    @Override
    public void onLoadFailed(Exception e, Drawable drawable) {
    }

    @Override
    public void onResourceReady(File file, GlideAnimation<? super File> glideAnimation) {
    }

    @Override
    public void onLoadCleared(Drawable drawable) {

    }

    @Override
    public void getSize(SizeReadyCallback sizeReadyCallback) {
    }

    @Override
    public void setRequest(Request request) {

    }

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
