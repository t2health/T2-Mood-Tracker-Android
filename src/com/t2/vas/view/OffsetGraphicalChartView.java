package com.t2.vas.view;

import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class OffsetGraphicalChartView extends View {
	  private AbstractChart mChart;
	  private Rect mRect = new Rect();
	
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
