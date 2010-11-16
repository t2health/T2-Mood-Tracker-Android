package com.t2.vas.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

public class StaticListView extends ListView {

	private ListAdapter adapter;

	public StaticListView(Context context) {
		super(context);
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setCacheColorHint(Color.argb(0, 255, 255, 255));
//		this.setDividerHeight(0);
	}
	
	public StaticListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setCacheColorHint(Color.argb(0, 255, 255, 255));
//		this.setDividerHeight(0);
	}
	
	public StaticListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setVerticalScrollBarEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setCacheColorHint(Color.argb(0, 255, 255, 255));
//		this.setDividerHeight(0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// here I assume that height's being calculated for one-child only, seen it in ListView's source which is actually a bad idea
        int childHeight = getMeasuredHeight() - (getListPaddingTop() + getListPaddingBottom() +  getVerticalFadingEdgeLength() * 2);

        int fullHeight = getListPaddingTop() + getListPaddingBottom() + childHeight*(getCount());

        setMeasuredDimension(getMeasuredWidth(), fullHeight);
	}

	
	/*@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.adapter = adapter;
		this.removeAllViewsInLayout();
		this.requestLayout();
	}

	@Override
	public ListAdapter getAdapter() {
		return this.adapter;
	}
	
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int bottomEdge = 0;
		for(int i = 0; i < this.adapter.getCount(); ++i) {
			View v = this.adapter.getView(i, null, this);
			this.addAndMeasureChild(v);
			bottomEdge += v.getMeasuredHeight();		
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec | bottomEdge);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		this.layoutChildren();
		
		Log.v("TEST", "DIM:"+this.getWidth()+"x"+this.getHeight());
	}

	private void layoutChildren() {
		int bottomEdge = 0;
		
		for(int i = 0; i < this.adapter.getCount(); ++i) {
			View v = this.adapter.getView(i, null, this);
			this.addAndMeasureChild(v);
			bottomEdge += v.getMeasuredHeight();		
		}
		
		Log.v("TEST", "BE:"+bottomEdge);
	}

	private void addAndMeasureChild(View v) {
		Log.v("ADD", "ADD");
		LayoutParams params = v.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		this.addViewInLayout(v, -1, params, true);
		
		int itemWidth = this.getWidth();
		v.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}
	
	private void positionItems() {
		int top = 0;
		for(int i = 0; i < this.getChildCount(); ++i) {
			View v = this.getChildAt(i);
			
			int width = v.getMeasuredWidth();
			int height = v.getMeasuredHeight();
			int left = (this.getWidth() - width) / 2;
			
			v.layout(left, top, left+width, top+height);
			top += height;
		}
	}*/
}
