package com.imageload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.imageload.transform.GlideRoundTransform;


/**
 * Created by lulei-ms on 2018/3/2.
 */

public interface IImageLoader {

    public Drawable getPlaceholder(ImageView imageView);

    public void setImageByGif(ImageView imageView, String url, int count);

    public void setImageByUrl(ImageView imageView, String url);

    public void setImageByUrl(ImageView imageView, String url, int width, int height);

    public void setImageByFilePath(ImageView imageView, String filePath, int width, int height);

    /**
     * 不建议使用该方法了，
     * placeholder可以通过 ImageView直接设置 android:src 属性 搞定
     */
    @Deprecated
    public void setImageByFilePath(ImageView imageView, String filePath, int placeholder, int width, int height);

    public void setImageByResId(ImageView imageView, int resId);

    public void setViewBackGround(View view, String url);

    public void getBitmapFromUrl(Context context, String url, ImageLoadListener listener);

    public void getPathFromUrl(final Context context, final String url, final ImageLoadListener listener);

    public void setRoundImageByUrl(ImageView imageView, String url, int dp);

    public void setRoundImageByUrl(ImageView imageView, String url, int dp, GlideRoundTransform.CornerType cornerType);

    /**
     * @param cornerType 圆角类型
     * */
    public void setRoundImageByUrl(ImageView imageView, String url, int dp, int width, int height, GlideRoundTransform.CornerType cornerType);

    public void setCircleImageByUrl(ImageView imageView, String url);

    /**
     * 带描边的圆形
     * @param strokeWidth dp
     * */
    public void setStrokeCircleImgByUrl(ImageView imgByUrl, String url, float strokeWidth, int strokeColor);

    public void setRoundImageByFilePath(ImageView imageView, String filePath,  int width, int height, int dp);

    public void resumeRequests(Context context);

    public void pauseRequests(Context context);

    public void clearCache(Context context);

    public String getDiskCacheDir(Context context);

    public void clearMemoryCacheByUrl(Context context, String url);
}
