package com.t2.vas.view.chart;

import com.t2.vas.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class ChartBitmapFactory {
	public static Bitmap getBitmap(Context context, Chart c, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		c.onDraw(new Canvas(bitmap));
		return bitmap;
	}
}
