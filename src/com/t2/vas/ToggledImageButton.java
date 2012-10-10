/*
 * 
 */
package com.t2.vas;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ToggledImageButton extends ImageButton 
{

	//private AccessibilityManager aManager;
	private boolean isChecked = false;
	public int onResource = 0;
	public int offResource = 0;
	
	//
	public ToggledImageButton(Context context) 
	{
		super(context);
		this.init();
	}

	public ToggledImageButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.init();
	}

	public ToggledImageButton(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() 
	{
		//aManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
		this.setScaleType(ScaleType.FIT_XY);
		this.setPadding(0, 0, 0, 0);
		this.setBackgroundColor(Color.TRANSPARENT);
	}

	public void setChecked(boolean isChecked) 
	{
		this.isChecked = isChecked;
		if(this.isChecked)
		{
			//this.setBackgroundResource(R.drawable.shine);
			this.setImageResource(onResource);
		}
		else
		{
			//this.setBackgroundResource(R.drawable.shine);
			this.setImageResource(offResource);
		}

		this.refreshDrawableState();
	}

	public boolean isChecked()
	{
		return isChecked;
	}

//	@Override
//	public int[] onCreateDrawableState(int extraSpace) 
//	{
//		int[] states;
//
//		if(this.isChecked()) 
//		{
//			states = ImageButton.PRESSED_WINDOW_FOCUSED_STATE_SET;
//		} 
//		else 
//		{
//			if(super.hasFocus()) 
//			{
//				states = super.onCreateDrawableState(extraSpace);
//			} 
//			else 
//			{
//				states = initialState;
//			}
//		}
////
//		return states;
//	}

//	@SuppressWarnings("unused")
//	private void speakText(String text, int queueMode) 
//	{
//		if(aManager.isEnabled()) 
//		{
//			aManager.interrupt();
//
//			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
//			event.setPackageName(this.getClass().getPackage().toString());
//			event.setClassName(this.getClass().getSimpleName());
//			event.setContentDescription(text);
//			event.setEventTime(System.currentTimeMillis());
//			aManager.sendAccessibilityEvent(event);
//		}
//	}

}
