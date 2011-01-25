package com.t2.vas.view;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class OffsetGraphicalChartView extends View {
	private static final String TAG = OffsetGraphicalChartView.class.getSimpleName();
	  /** The chart to be drawn. */
	  private AbstractChart mChart;
	  /** The chart renderer. */
	  private XYMultipleSeriesRenderer mRenderer;
	  /** The view bounds. */
	  private Rect mRect = new Rect();
	  /** The user interface thread handler. */
	  private Handler mHandler;
	  /** The old x coordinate. */
	  private float oldX;
	  /** The old y coordinate. */
	  private float oldY;
	
	public OffsetGraphicalChartView(Context context, AbstractChart chart) {
	    super(context);
	    mChart = chart;
    }

	@Override
	  protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.getClipBounds(mRect);
	    int top = mRect.top;
	    int left = mRect.left-10;
	    int width = mRect.width();
	    int height = mRect.height();
	    mChart.draw(canvas, left, top, width, height);
	  }
}
