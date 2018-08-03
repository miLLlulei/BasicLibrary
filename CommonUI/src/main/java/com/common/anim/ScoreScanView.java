package com.common.anim;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


import com.mill.utils.BitmapUtils;
import com.mill.utils.DensityUtils;
import com.mill.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 注意放一个中心位置的风车图片资源 ：icon_scan_fs.png
 *
 */
public class ScoreScanView extends View {
    public static final int RED = 0xffff5252;
    public static final int ORANGE = 0xfffcd038;
    public static final int GREEN = 0xff54d36f;
    public static final int BLUE = 0xff32a7de;

    public static final int CIRCLE_BG = 0x66ffffff;

    /**
     * 总时间
     * 一般可能是超时时间
     */
    private final static int ANIMATION_TIME_SCAN = 1000 * 30;
    /**
     * 圈圈冲击的时间
     * 数值越小，冲击速度越快
     */
    private final static int ANIMATION_TIME_SCAN_LOOP = 80;
    /**
     * 圈圈冲击的时间分片
     */
    private float LOOP_TIME = 1f / ANIMATION_TIME_SCAN_LOOP;
    /**
     * 产生新的一组圈圈的时间间隔
     * 数值越小，连续性圈圈越多
     */
    private float NEXT_NEW_TIME = LOOP_TIME * (int) (ANIMATION_TIME_SCAN_LOOP * 0.4f);
    /**
     * 风车旋转，每次自增的多少度
     * 数值越大，旋转越快
     */
    private float LOOP_ANGLE = 15;


    private ValueAnimator mAnim;

    private Paint mPaint;
    private LinearGradient mLGradient;

    private int mBgColor = ORANGE;
    private ArgbEvaluator mArgbEvaluator;

    private Bitmap mScanBitmap;
    private Matrix mBmMatrix;
    private float mScanAngle;

    private String mDesc = "%d%%   优化中...";
    private float mTextWidth;
    private float mTextSize;
    private float mTextMarginBottom;

    private int mTargetScore = 100;
    private int mCurrentScore;
    private float mCurrentProgress;

    private List<Circle> mCircles = new ArrayList<>();
    private float mCurrentNextTime = NEXT_NEW_TIME;


    public ScoreScanView(Context context) {
        this(context, null);
    }

    public ScoreScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setFilterBitmap(true); //动画时，去图片锯齿
        mBmMatrix = new Matrix();

        mArgbEvaluator = new ArgbEvaluator();

        mTextSize = DensityUtils.dip2px(13f);
        mTextMarginBottom = DensityUtils.dip2px(12f);
        mPaint.setTextSize(mTextSize);
        mTextWidth = mPaint.measureText(String.format(mDesc, 90));

        addNewCircles();

        // 获取风车图片资源
        int fs_ResId = getResources().getIdentifier("icon_scan_fs", "drawable", getContext().getPackageName());
        mScanBitmap = BitmapUtils.getBitmapFromResourceWithHighQuality(getContext().getResources(), fs_ResId, 268, 268);

        mAnim = ValueAnimator.ofInt(0, 100);
        mAnim.setDuration(ANIMATION_TIME_SCAN);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = animation.getAnimatedFraction();
                mCurrentProgress = animation.getAnimatedFraction();

                mCurrentScore = (int) ((mTargetScore - mCurrentScore) * progress + mCurrentScore);
                mBgColor = (int) mArgbEvaluator.evaluate((float) mCurrentScore / 100, RED, GREEN);

                invalidate();
            }
        });
    }

    public void startAnim(int startScore) {
        setScore(startScore);
        if (!mAnim.isStarted()) {
            mAnim.start();
        }
    }

    public void stopAnim() {
        if (mAnim.isStarted()) {
            mAnim.cancel();
        }
    }

    public void setScore(int score) {
        this.mTargetScore = score;
    }

    /**
     * 随机生成某个方向的 圈圈
     */
    private Circle obtainNewCircle(int where) {
        Circle circle = new Circle();
        circle.where = where;
        int random = DensityUtils.dip2px(-30f) + RandomUtils.getRandomInt(DensityUtils.dip2px(60f));
        if (where == 0) {
            circle.x = DensityUtils.dip2px(-100f) + random * 2;
            circle.y = DensityUtils.dip2px(-150f) + random;
            circle.radius = DensityUtils.dip2px(30f) + random / 6;
            circle.color = CIRCLE_BG;
        } else if (where == 1) {
            circle.x = DensityUtils.dip2px(120f) + random;
            circle.y = DensityUtils.dip2px(-150f) + random / 2;
            circle.radius = DensityUtils.dip2px(25f) + random / 6;
            circle.color = CIRCLE_BG;
        } else if (where == 2) {
            circle.x = DensityUtils.dip2px(-110f) + random;
            circle.y = DensityUtils.dip2px(150f) + random * 2;
            circle.radius = DensityUtils.dip2px(26f) + random / 6;
            circle.color = CIRCLE_BG;
        } else if (where == 3) {
            circle.x = DensityUtils.dip2px(100f) + random / 2;
            circle.y = DensityUtils.dip2px(150f) + random;
            circle.radius = DensityUtils.dip2px(20f) + random / 6;
            circle.color = CIRCLE_BG;
        } else if (where == 4) {
            circle.x = DensityUtils.dip2px(-200f) + random / 2;
            circle.y = DensityUtils.dip2px(10f) + random / 2;
            circle.radius = DensityUtils.dip2px(15f) + random / 6;
            circle.color = CIRCLE_BG;
        } else if (where == 5) {
            circle.x = DensityUtils.dip2px(200f) + random / 2;
            circle.y = DensityUtils.dip2px(-10f) + random / 2;
            circle.radius = DensityUtils.dip2px(18f) + random / 6;
            circle.color = CIRCLE_BG;
        }
        return circle;
    }

    /**
     * 添加新的一组圈圈，到屏幕里；
     * mCurrentNextTime 会逐渐变大的，每间隔 NEXT_NEW_TIME 的时间，会增加一组圈圈，显得看着连贯冲击感；
     */
    private void addNewCircles() {
        if (mCurrentNextTime >= NEXT_NEW_TIME
                && mCurrentNextTime < NEXT_NEW_TIME + LOOP_TIME) {
            mCurrentNextTime = 0;
            mCircles.add(obtainNewCircle(0));
            mCircles.add(obtainNewCircle(1));
            mCircles.add(obtainNewCircle(2));
            mCircles.add(obtainNewCircle(3));
            mCircles.add(obtainNewCircle(4));
            mCircles.add(obtainNewCircle(5));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // 背景渐变
        canvas.drawColor(mBgColor);
        if (mLGradient == null) {
            mLGradient = new LinearGradient(0, 0, getWidth(), getHeight(), 0x88ffffff, 0x00ffffff, Shader.TileMode.REPEAT);
        }
        mPaint.setShader(mLGradient);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        // 风扇
        if (mScanBitmap != null) {
            mPaint.setShader(null);
            mPaint.setAlpha((int) (255));
            mPaint.setStyle(Paint.Style.FILL);
            float scanAngle = mScanAngle % 360;
            int centerBmX = mScanBitmap.getWidth() / 2;
            int centerBmY = mScanBitmap.getHeight() / 2;
            mBmMatrix.setTranslate(centerX - centerBmX, centerY - centerBmY);
            mBmMatrix.preRotate(scanAngle, centerBmX, centerBmY);
            canvas.drawBitmap(mScanBitmap, mBmMatrix, mPaint);
            mScanAngle += LOOP_ANGLE;
        }

        // 冲击的 圈圈
        Iterator<Circle> it = mCircles.iterator();
        while (it.hasNext()) {
            Circle circle = it.next();
            // 每个圈圈都有自己的进度，大于1了，就可以回收删除了；
            if (circle.progress > 1) {
                it.remove();
            } else {
                mPaint.setShader(null);
                mPaint.setColor(circle.color);
                mPaint.setAlpha((int) (125 * (1f - circle.progress)));
                float x = centerX + circle.x * (1f - circle.progress);
                float y = centerY + circle.y * (1f - circle.progress);
                float radius = circle.radius * (1.2f - circle.progress);
                canvas.drawCircle(x, y, radius, mPaint);
                circle.progress += LOOP_TIME;
            }
        }
        addNewCircles();
        mCurrentNextTime += LOOP_TIME;

        // 底部文案
        mPaint.setShader(null);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(mTextSize);
        String disText = String.format(mDesc, (int) (mCurrentProgress * 100));
        canvas.drawText(disText, centerX - mTextWidth / 2, getHeight() - mTextMarginBottom, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnim();
        super.onDetachedFromWindow();
    }

    public static class Circle {
        public float x;
        public float y;
        public float radius;
        public int color;
        public float progress;
        public int where;

        public Circle() {
        }

        public Circle(float x, float y, float radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }
}
