package com.t2.vas.view;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;

public class GroupGallery extends Gallery {
	private static final String TAG = GroupGallery.class.getName();
	
	private SelectedViewSelector selector = new SelectedViewSelector();
	private SelectGroupIndexHandler selectGroupIndexHandler = new SelectGroupIndexHandler();
	
	public GroupGallery(Context context) {
		super(context);
	}

	public GroupGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GroupGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setSelection(int position, boolean animate) {
		this.privateSetSelection(position);
		super.setSelection(position, animate);
	}

	@Override
	public void setSelection(int position) {
		this.privateSetSelection(position);
		super.setSelection(position);
	}
	
	private void privateSetSelection(int newPosition) {
		if(newPosition == this.getSelectedItemPosition() && newPosition != 0) {
			this.hilightSelectedView();
		} else {
			if(!this.selector.isRunning()) {
				this.selector = new SelectedViewSelector();
				this.selector.execute(this.getSelectedView());
			}
		}
	}
	
	private void hilightSelectedView() {
		if(getSelectedView() != null) {
			getSelectedView().performClick();
			getSelectedView().setSelected(true);
		}
	}
	
	
	
	private class SelectedViewSelector extends AsyncTask<View,Boolean,Boolean> {
		private boolean isRunning = false;
		
		public boolean isRunning() {
			return this.isRunning;
		}

		@Override
		protected Boolean doInBackground(View... params) {
			this.isRunning  = true;
			int loopCount = 0;
			View currSelView = params[0];
			View selView;
			while(true) {
//				Log.v(TAG, "Checking for selected view change.");
				selView = getSelectedView();
				if(selView != currSelView) {
					break;
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Quit the loop if it appears to be endless.
				if(loopCount > 10) {
					break;
				}
				
				loopCount++;
			}
			
//			Log.v(TAG, "Selecting the newly selected view.");
			selectGroupIndexHandler.sendEmptyMessage(1);
			
			this.isRunning = false;
			
			return true;
		}
		
	}
	
	private class SelectGroupIndexHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			hilightSelectedView();
		}
	}

}
