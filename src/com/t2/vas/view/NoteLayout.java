package com.t2.vas.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.t2.vas.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteLayout extends LinearLayout {
	private static final String TAG = NoteLayout.class.getName();
	private String dateFormat = "EEEE MMMM, d yyyy";
	private long timestamp = 0;
	
	public NoteLayout(Context context) {
		super(context);
	}
	
	public NoteLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public String getNote() {
		return ((TextView)this.findViewById(R.id.note)).getText().toString();
	}
	
	public void setNote(String text) {
		((TextView)this.findViewById(R.id.note)).setText(text);
	}
	
	public void setDateFormat(String format) {
		this.dateFormat = format;
		setDateValue();
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		setDateValue();
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public void setDateValue() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		
		SimpleDateFormat f = new SimpleDateFormat(this.dateFormat);
		String dateValue = f.format(c.getTime());
		
		((TextView)this.findViewById(R.id.date)).setText(dateValue);
	}
}
