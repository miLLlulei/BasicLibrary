package com.mill.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * UI单位互转
 */
public class DensityUtils {
	
	private DensityUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * dp转px
	 */
	public static int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * sp转px
	 */
	public static int sp2px(Context context, float spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * px转dp
	 */
	public static float px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * px转sp
	 */
	public static float px2sp(Context context, float pxVal) {
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

	public static int dip2px(float dip) {
		final float scale = getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	public static DisplayMetrics dm = null;

	public static DisplayMetrics getDisplayMetrics() {
		if (dm == null) {
			dm = ContextUtils.getApplicationContext().getResources().getDisplayMetrics();
		}
		return dm;
	}

}
