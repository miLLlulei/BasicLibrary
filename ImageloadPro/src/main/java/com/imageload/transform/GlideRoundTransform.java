package com.imageload.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by lulei-ms on 2018/3/2.
 */

public class GlideRoundTransform extends BitmapTransformation {

    private float radius = 0f;
    private CornerType mCornerType;

    public enum CornerType {
        ALL,//四个圆角
        TOP,//左上角、右上角圆角
        BOTTOM,//左下角、右下角圆角
    }

    public GlideRoundTransform(Context context) {
        this(context, 4);
    }

    public GlideRoundTransform(Context context, int dp) {
        this(context, dp, CornerType.ALL);
    }

    public GlideRoundTransform(Context context, int dp, CornerType cornerType ) {
        super(context);
        this.radius = context.getResources().getDisplayMetrics().density * dp;
        this.mCornerType = cornerType;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
//        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());

//        canvas.drawRoundRect(rectF, radius, radius, paint);

        Path path = new Path();
        drawRoundRect(canvas, paint, path, source.getWidth(), source.getHeight());

        return result;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, Path path, int width, int height) {
        float[] rids;
        switch (mCornerType) {
            case ALL:
                rids = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                drawPath(rids, canvas, paint, path, width, height);
                break;

            case TOP:
                rids = new float[]{radius, radius, radius, radius, 0.0f, 0.0f, 0.0f, 0.0f};
                drawPath(rids, canvas, paint, path, width, height);
                break;
            case BOTTOM:
                rids = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radius, radius, radius, radius};
                drawPath(rids, canvas, paint, path, width, height);
                break;

            default:
                throw new RuntimeException("RoundedCorners type not belong to CornerType");


        }
    }

    /**
     * @param rids 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角
     */
    private void drawPath(float[] rids, Canvas canvas, Paint paint, Path path, int width, int height) {
        path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
//        canvas.clipPath(path);
        canvas.drawPath(path, paint);
    }


    @Override
    public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}
