package com.t2.vas;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class NoteCursorAdapter extends SimpleCursorAdapter {
	private static final String TAG = NoteCursorAdapter.class.getName();
	private DBAdapter dbAdapter;
	
	public NoteCursorAdapter(Context context, DBAdapter dbAdapter, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.dbAdapter = dbAdapter;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.newView(context, cursor, parent);
		Note n = ((Note)dbAdapter.getTable("note")).newInstance();
		n.load(cursor);
		v.setTag(n);
		
		return v;
	}
	
	

}
