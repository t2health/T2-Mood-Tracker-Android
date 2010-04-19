package com.t2.vas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends SimpleCursorAdapter {
	private static final String TAG = NotesCursorAdapter.class.getName();
	private SimpleDateFormat dateFormat;
	private Calendar cal = Calendar.getInstance();

	public NotesCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, SimpleDateFormat sdf) {
		super(context, layout, c, from, to);
		this.dateFormat = sdf;
	}

	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = super.newView(context, cursor, parent);
		v.setTag(new Long(cursor.getLong(cursor.getColumnIndex("_id"))));
		return v;
	}
	
	@Override
	public void setViewText(TextView v, String text) {
		super.setViewText(v, text);
		
		if(v.getId() == R.id.date) {
			long timestamp = Long.parseLong(text);
			cal.setTimeInMillis(timestamp);
			
			v.setText(this.dateFormat.format(cal.getTime()));
		}
	}

	
}
