/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;


import zencharts.charts.DateChart;
import zencharts.data.DatePoint;
import zencharts.data.DateSeries;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.t2.vas.ArraysExtra;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.ScaleResultsDataProvider;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.ColorPickerDialog;
import com.t2.vas.view.SimpleCursorDateSectionAdapter;
import com.t2.vas.view.ToggledButton;

public class ScaleResultsActivity extends ABSActivity implements AdapterView.OnItemSelectedListener, OnClickListener, OnItemClickListener, GestureDetector.OnGestureListener {

	protected static final int DIALOG_1 = 0; // Dialog 1 ID
	
	public static final String EXTRA_TIME_START = "timeStart";
	public static final String EXTRA_CALENDAR_FIELD = "calendarField";
	public static final String EXTRA_REVERSE_DATA = "reverseData";

	private static final int ADD_EDIT_NOTE_ACTIVITY = 30958;
	private static final String NOTES_CACHE = "notes";

	private static final String KEY_NAME = "results_visible_ids_";

	private static final int KEYS_TAB = 1;
	private static final int NOTES_TAB = 2;
	private static final int OPTIONS_TAB = 3;
	private int selectedTab = 0;
	private int[] resourceIDS;
	private static ArrayList<DateSeries> dateSeriesCollection = null;
	ArrayList<Long> toggledIds;
	
	private ProgressDialog m_ProgressDialog = null;

	protected static final File EXTERNAL_DIRECTORY =
			new File(Environment.getExternalStorageDirectory(), "T2MoodTracker");

	private DateChart dateChart;

	private static final int Menu1 = Menu.FIRST + 1;
	private static final int Menu2 = Menu.FIRST + 2;
	private static final int Menu3 = Menu.FIRST + 3;
	private static final int Menu4 = Menu.FIRST + 4;
	private static final int Menu5 = Menu.FIRST + 5;
	
	private boolean showSymbols = true;
    private boolean showShading = true;
    private boolean showLines = true;
	
	private ListView keysList;
	private ListView notesList;
	private ListView optionsList;

	private ArrayList<KeyItem> keyItems;

	private KeyItemAdapter keysAdapter;
	private SimpleCursorDateSectionAdapter notesAdapter;

	private ToggledButton keysTabButton;
	private ToggledButton notesTabButton;
	private ToggledButton optionsTabButton;
	
	protected Calendar startCal;
	protected Calendar endCal;
	protected int calendarField;

	SimpleDateFormat monthNameFormatter = new SimpleDateFormat("MMMM, yyyy");
	private GestureDetector gestureDetector;
	private DataProvider dataProvider;
	private Cursor notesCursor;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private DataPointCache dataPointCache;
	protected boolean reverseLabels;
	private FrameLayout collapseList;

	long minDate = Long.MAX_VALUE;
	long maxDate = Long.MIN_VALUE;
	
	String[] durationItemsText={"30 Days", "90 Days", "120 Days", "1 Year"};
	int[] durationItemsNum={30, 90, 120, 365}; 
	int selectedDuration = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		resourceIDS = new int[]{R.drawable.circle, R.drawable.clover, R.drawable.club, R.drawable.clover, R.drawable.cross, R.drawable.davidstar, R.drawable.diamondring, R.drawable.doublehook, R.drawable.fivestar, R.drawable.heart, R.drawable.hexagon, R.drawable.hourglass, R.drawable.octogon, R.drawable.pentagon, R.drawable.quadstar, R.drawable.spade, R.drawable.square, R.drawable.triangle};

		Intent intent = this.getIntent();
		groupId = intent.getLongExtra(EXTRA_GROUP_ID, 0);
		if (groupId <= 0) {
			finish();
			return;
		}

		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		Display display = this.getWindowManager().getDefaultDisplay();

		int width = display.getWidth();
		int height = display.getHeight();
		if (width > height)
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		super.onCreate(savedInstanceState);

		showSymbols = SharedPref.getSymbols(sharedPref);
        showShading = SharedPref.getShading(sharedPref);
        showLines = SharedPref.getLines(sharedPref);
        
		dataPointCache = new DataPointCache();
		dataProvider = this.getDataProvider();
		gestureDetector = new GestureDetector(this, this);

		/*
		 * long startTime = this.getIntent().getLongExtra(EXTRA_TIME_START, 0);
		 * if(startTime == 0) { startTime =
		 * Calendar.getInstance().getTimeInMillis(); }
		 */

		// Intent intent = this.getIntent();
		// this.calendarField = intent.getIntExtra(EXTRA_CALENDAR_FIELD,
		// Calendar.DAY_OF_MONTH);
		// this.reverseLabels = intent.getBooleanExtra(EXTRA_REVERSE_DATA,
		// false);

		// Set the time ranges.
		/*
		 * startCal = Calendar.getInstance();
		 * startCal.setTimeInMillis(MathExtra.roundTime(startTime,
		 * calendarField)); startCal.set(calendarField,
		 * startCal.getMinimum(calendarField));
		 * 
		 * endCal = Calendar.getInstance();
		 * endCal.setTimeInMillis(startCal.getTimeInMillis());
		 * endCal.add(Calendar.MONTH, 1);
		 */

		// Set the content view.
		this.setContentView(R.layout.abs_results_activity);
		collapseList = (FrameLayout) this.findViewById(R.id.collapseList);
		dateChart = (DateChart) this.findViewById(R.id.datechart);
		dateChart.loadFont("Elronmonospace.ttf", 16, 2, 2);
		dateChart.showSymbols(showSymbols);
        dateChart.showLines(showLines);
        dateChart.showShading(showShading);
        
		// Prepare the notes adapter.
		notesCursor = new Note(dbAdapter).queryForNotes(-1, -1, "timestamp DESC");
		this.startManagingCursor(notesCursor);
		notesAdapter = SimpleCursorDateSectionAdapter.buildNotesAdapter(
				this,
				notesCursor,
				new SimpleDateFormat(Global.NOTES_LONG_DATE_FORMAT),
				new SimpleDateFormat(Global.NOTES_SECTION_DATE_FORMAT),
				R.layout.list_item_2_black
				);

		// Prepare the keys adapter.
		keyItems = getKeyItems();

		// set the color and visibility for each key item.
		ArrayList<Long> visibleIds = getVisibleIds(getSettingSuffix());
		int keyCount = keyItems.size();
		for (int i = 0; i < keyCount; ++i) {
			KeyItem item = keyItems.get(i);
			if(visibleIds.contains(item.id))
				item.visible = false;
			else
				item.visible = true;
		}

		keysAdapter = new KeyItemAdapter(this, getKeyItemViewType(), keyItems);

		keysList = (ListView) this.findViewById(R.id.keysList);
		keysList.setAdapter(keysAdapter);
		if (isKeyItemsClickable()) {
			keysList.setOnItemClickListener(this);
		}

		notesList = (ListView) this.findViewById(R.id.notesList);
		notesList.setAdapter(notesAdapter);
		notesList.setOnItemClickListener(this);
		notesList.setFastScrollEnabled(true);

		keysTabButton = (ToggledButton) this.findViewById(R.id.keysTabButton);
		keysTabButton.setOnClickListener(this);
		keysTabButton.setText(getKeyTabText());

		notesTabButton = (ToggledButton) this.findViewById(R.id.notesTabButton);
		notesTabButton.setOnClickListener(this);

//		if (savedInstanceState != null) {
//
//			// Populate chart only
//			dateSeriesCollection = (ArrayList<DateSeries>) savedInstanceState.getSerializable("data");
//			PopulateChart();
//
//			int selTab = savedInstanceState.getInt("selectedTab");
//			if (selTab == NOTES_TAB) {
//				showNotesTab();
//			} else {
//				showKeysTab();
//			}
//		} else 
		
		try
		{
			optionsTabButton = (ToggledButton)this.findViewById(R.id.optionsTabButton);
			optionsTabButton.setOnClickListener(this);
			optionsList = (ListView) this.findViewById(R.id.optionsList);
			//ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.simple_list_item, new String[] {"Date Range", "Toggle Symbols", "Toggle Lines", "Toggle Shading"});
			optionsList.setAdapter(new OptionItemAdapter(this, 1, getOptionItems()));
			optionsList.setOnItemClickListener(this);
		}
		catch(Exception ex){}
		
		{

			// Load data and populate chart
			generateChart();
			showKeysTab();
		}

		group = new Group(this.dbAdapter);
		group._id = groupId;
		if (!group.load()) {
			finish();
			return;
		}

		this.reverseLabels = !group.inverseResults;

		this.setTitle(group.title);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedTab", selectedTab);
		outState.putSerializable("data", dateSeriesCollection);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_EDIT_NOTE_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				dataPointCache.clearCache(NOTES_CACHE);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	protected int getKeyColor(int currentIndex, int totalCount) {
		float hue = currentIndex / (1.00f * totalCount) * 360.00f;

		return Color.HSVToColor(
				255,
				new float[] {
						hue,
						1.0f,
						1.0f
				}
				);
	}

	protected final void showKeysTab() {
		selectedTab = KEYS_TAB;
		keysTabButton.setChecked(true);
		notesTabButton.setChecked(false);

		try
		{
			optionsTabButton.setChecked(false);
			optionsList.setVisibility(View.INVISIBLE);
		}
		catch(Exception ex){}
		
		keysList.setVisibility(View.VISIBLE);
		notesList.setVisibility(View.INVISIBLE);
	}

	protected final void showNotesTab() {
		selectedTab = NOTES_TAB;
		keysTabButton.setChecked(false);
		notesTabButton.setChecked(true);
		optionsTabButton.setChecked(false);
		
		keysList.setVisibility(View.INVISIBLE);
		notesList.setVisibility(View.VISIBLE);
		optionsList.setVisibility(View.INVISIBLE);
	}

	protected final void showOptionsTab() {
		selectedTab = OPTIONS_TAB;
		keysTabButton.setChecked(false);
		notesTabButton.setChecked(false);
		optionsTabButton.setChecked(true);

		keysList.setVisibility(View.INVISIBLE);
		notesList.setVisibility(View.INVISIBLE);
		optionsList.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0 == keysList) {
			onKeysItemClicked(keyItems.get(arg2), arg1, arg2, arg3);

		} else if (arg0 == notesList) {
			onNotesItemClicked(arg1, arg2, arg3);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.keysTabButton:
			if (selectedTab != KEYS_TAB)
			{
				collapseList.setVisibility(View.VISIBLE);

				showKeysTab();
			}
			else
			{
				if (collapseList.getVisibility() == View.GONE)
					collapseList.setVisibility(View.VISIBLE);
				else
					collapseList.setVisibility(View.GONE);
			}
			break;

		case R.id.notesTabButton:
			showNotesTab();
			break;
			
		case R.id.optionsTabButton:
			showOptionsTab();
			break;
		}
	}

	private void onNotesItemClicked(View view, int pos, long id) {
		Note note = new Note(dbAdapter);
		note._id = id;
		note.load();

		// Show a dialog with the full note.
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.note_details_title))
				.setMessage(note.note)
				.setCancelable(true)
				.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.create()
				.show();
	}

	private void onKeyToggleButtonCheckedChanged() {
		saveVisibleKeyIds();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		dateChart.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		dateChart.onPause();
	}

	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case DIALOG_1:
		{

			final Dialog dialog;
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.spinnerdialog);
			dialog.setTitle("Select chart duration");
			Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
			btnDone.setOnClickListener(new View.OnClickListener() {  
				public void onClick(View v)
				{
					changeChartDateRange();
					dialog.cancel();
				}
			});

			Spinner spinner = (Spinner)dialog.findViewById(R.id.spinner);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, durationItemsText);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
			return dialog;
		}
		}
		return null;
	}
	
	public void changeChartDateRange()
	{
		try
		{
			DateTime startTime = new DateTime().minusDays(durationItemsNum[selectedDuration]).withTime(0, 0, 0, 0);
			DateTime endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);
			dateChart.setPeriod(new Duration(startTime, endTime));
			dateChart.setPeriodStartTime(startTime);
			generateChart();
		}
		catch(Exception ex)
		{}
	}

	
	public void generateChart()
	{
		if (dateSeriesCollection == null)
			dateSeriesCollection = new ArrayList<DateSeries>();
		else
			dateSeriesCollection.clear();

		m_ProgressDialog = new ProgressDialog(this);
		m_ProgressDialog.setTitle("Please wait...");
		m_ProgressDialog.setIndeterminate(false);
		m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_ProgressDialog.show();

		Runnable myRunnable = new Runnable()
		{
			public void run()
			{
				generateChartThreaded();
				runOnUiThread(PopulateChartRunnable);
			}
		};

		Thread thread = new Thread(null, myRunnable, "ChartThread");
		thread.start();
	}

	private void generateChartThreaded()
	{
		
		try
		{
			
		// Build the data series for each enabled key.
		ArrayList<DataPoint> dataPoints = null;
		int kisize = keyItems.size();

		m_ProgressDialog.setMax(kisize);
		m_ProgressDialog.setProgress(0);

		//Lock to timeframe
		DateTime startTime = new DateTime().minusDays(durationItemsNum[selectedDuration]).withTime(0, 0, 0, 0);
		DateTime endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);
		
		long minDate = Long.MAX_VALUE;
		long maxDate = Long.MIN_VALUE;
		for (int i = 0; i < kisize; ++i) {
			KeyItem item = keyItems.get(i);

			// update progressbar
			m_ProgressDialog.setProgress(i);

			DateSeries series = new DateSeries(this, item.resID);
			series.lineColor = item.color;

			series.id = item.id;
			series.visible = item.visible;
			
			series.lineWidth = (2 * displayMetrics.density);

			// Get the data points
			dataPoints = dataProvider.getScaleData(item.id, startTime.getMillis(), endTime.getMillis()); // loadAllData(keyItems.get(i));
			// ArrayList<String> addedDays = new ArrayList<String>();

			// Cursor notes = null;
			int dpsize = dataPoints.size();
			for (int j = 0; j < dpsize; ++j) {
				DataPoint dp = dataPoints.get(j);
				
				//if((dp.time >= startTime.getMillis()) && (dp.time <= endTime.getMillis()))
				series.add(new DatePoint(dp.time, (int) dp.getAverageValue(), ""));
				minDate = Math.min(minDate, dp.time);
				maxDate = Math.max(maxDate, dp.time);
			}

			dateSeriesCollection.add(series);

		}
		
		//Draw the "Notes" series
        DateSeries series = new DateSeries(this, R.drawable.diamondclassic);
        series.lineColor = Color.RED;
        series.title = "Note";
        series.id = 111L;
        series.visible = true;
        series.symbolResID = R.drawable.diamondclassic;
        Note note = new Note(dbAdapter);
		Cursor cursor = note.getNotesCursor(startTime.getMillis(), endTime.getMillis());
		while(cursor.moveToNext())
		{
			series.add(new DatePoint(cursor.getLong(cursor.getColumnIndex("timestamp")), 1,""));
		}
		dateSeriesCollection.add(series);
		
		try
		{
			dateChart.setPeriod(new Duration(startTime, endTime));
			dateChart.setPeriodStartTime(startTime);
		}
		catch(Exception ex)
		{
			//Likely no data (first run?)
			//Set timeperiod for today
			startTime = new DateTime().withTime(0, 0, 0, 0);
			endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);
			dateChart.setPeriod(new Duration(startTime, endTime));
			dateChart.setPeriodStartTime(startTime);
		}
		
		}
		catch(Exception ex)
		{

		}
	}

	private Runnable PopulateChartRunnable = new Runnable() {
		public void run() {
			PopulateChart();
		}
	};

	private void PopulateChart()
	{
		dateChart.clearChart();
		
		// dateChart.clearChart();
		for (int i = 0; i < dateSeriesCollection.size(); i++)
		{
			dateChart.addSeries(dateSeriesCollection.get(i));
		}

		/*
		 * DateTime startTime = new
		 * DateTime(dateSeriesCollection.get(0).get(0).timeStamp); DateTime
		 * endTime = new
		 * DateTime(dateSeriesCollection.get(0).get(dateSeriesCollection
		 * .get(0).size() - 1).timeStamp);
		 * 
		 * dateChart.setPeriod(new Duration(startTime, endTime));
		 * dateChart.setPeriodStartTime(startTime);
		 */

		// dateChart.refreshView();

		dateChart.showSymbols(showSymbols);
        dateChart.showLines(showLines);
        dateChart.showShading(showShading);
        
		if (m_ProgressDialog != null)
			m_ProgressDialog.dismiss();
	}

	private void saveVisibleKeyIds() {
		String keySuffix = getSettingSuffix();
		ArrayList<Long> toggledIds = new ArrayList<Long>();
		for (int i = 0; i < keyItems.size(); i++) {
			KeyItem item = keyItems.get(i);
			if (!item.visible) {
				toggledIds.add(item.id);
			}
			for (int a = 0; a < dateSeriesCollection.size(); a++)
			{
				if (dateSeriesCollection.get(a).id == item.id)
				{
					dateSeriesCollection.get(a).visible = item.visible;
				}
				dateChart.setSeriesVisibility(a, dateSeriesCollection.get(a).visible);
			}
		}
		setVisibleIds(keySuffix, toggledIds);
	}

	private ArrayList<Long> getVisibleIds(String keySuffix) {
		String[] idsStrArr = SharedPref.getValues(
				sharedPref,
				KEY_NAME + keySuffix,
				",",
				new String[0]
				);

		return new ArrayList<Long>(
				Arrays.asList(
						ArraysExtra.toLongArray(idsStrArr)
						));
	}

	private void setVisibleIds(String keySuffix, ArrayList<Long> ids) {
		SharedPref.setValues(
				sharedPref,
				KEY_NAME + keySuffix,
				",",
				ArraysExtra.toStringArray(ids.toArray(new Long[ids.size()]))
				);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Log.v(TAG, "v:"+ velocityX +","+ velocityY);
		if (Math.abs(velocityY) < 50 || Math.abs(velocityX) < 200) {
			return false;
		}

		if (velocityX > 200) {
			// monthMinusButtonPressed();
			return true;

		} else if (velocityX < -200) {
			// monthPlusButtonPressed();
			return true;
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public void ChooseKeyColor(final KeyItem item)
	{
		ColorPickerDialog cp = new ColorPickerDialog(this, SharedPref.getKeyColor(sharedPref, "scl" + item.id), resourceIDS, new ColorPickerDialog.OnColorPickerListener() {

			@Override
			public void onCancel(ColorPickerDialog dialog) {
				

			}

			@Override
			public void onOk(ColorPickerDialog dialog, int color, int selectedResID) {
				item.color = color;
				item.resID = selectedResID;

				SharedPref.setKeyColor(sharedPref, "scl" + item.id, color);
				SharedPref.setKeyResource(sharedPref, "scl" + item.id, selectedResID);
				updateIconsColors();
			}

		}, SharedPref.getKeyResource(sharedPref, "scl" + item.id));
		cp.show();

	}

	public void updateIconsColors()
	{
		keysAdapter.notifyDataSetChanged();
		generateChart();
	}

	static class KeyItem {
		public long id;
		public String title1;
		public String title2;
		public int color;
		public int resID;
		public boolean visible;
		public boolean reverseData = false;

		public KeyItem(long id, String title1, String title2) {
			this.id = id;
			this.title1 = title1;
			this.title2 = title2;
		}

		public HashMap<String, Object> toHashMap() {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("id", id);
			data.put("title1", title1);
			data.put("title2", title2);
			data.put("color", color);
			data.put("visible", visible);
			return data;
		}
	}

	class KeyItemAdapter extends ArrayAdapter<KeyItem> {
		public static final int VIEW_TYPE_ONE_LINE = 1;
		public static final int VIEW_TYPE_TWO_LINE = 2;

		private LayoutInflater layoutInflater;
		private int layoutId;

		public KeyItemAdapter(Context context, int viewType,
				List<KeyItem> objects) {
			super(context, viewType, objects);

			layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			if (viewType == VIEW_TYPE_TWO_LINE) {
				layoutId = R.layout.list_item_result_key_2;
			} else {
				layoutId = R.layout.list_item_result_key_1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(layoutId, null);
			}

			final KeyItem item = this.getItem(position);
			TextView tv1 = (TextView) convertView.findViewById(R.id.text1);
			TextView tv2 = (TextView) convertView.findViewById(R.id.text2);
			ToggleButton tb = (ToggleButton) convertView.findViewById(R.id.showKeyToggleButton);
			ImageView keyBox = (ImageView) convertView.findViewById(R.id.keyBox);
			keyBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{
					ChooseKeyColor(item);
				}
			});

			boolean tv1Null = tv1 == null;
			boolean tv2Null = tv2 == null;
			if (reverseLabels && !tv1Null && !tv2Null) {
				if (!tv1Null) {
					tv1.setText(item.title2);
				}
				if (!tv2Null) {
					tv2.setText(item.title1);
				}
			} else {
				if (!tv1Null) {
					tv1.setText(item.title1);
				}
				if (!tv2Null) {
					tv2.setText(item.title2);
				}
			}

			if (tb != null) {
				if (isKeyItemsClickable()) {
					tb.setFocusable(false);
				}
				tb.setOnCheckedChangeListener(null);
				tb.setChecked(item.visible);
				tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(
							CompoundButton buttonView, boolean isChecked) {
						item.visible = isChecked;
						onKeyToggleButtonCheckedChanged();
					}
				});
			}

			if (keyBox != null) {
				keyBox.setImageResource(item.resID);
				keyBox.setColorFilter(item.color);
			}

			return convertView;
		}
	}

	public void populateMenu(Menu menu) {

		menu.setQwertyMode(true);

		MenuItem item1 = menu.add(0, Menu1, 0, "Save Screenshot");
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_camera);
		}
	}

	public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			SaveScreenshot();
			break;
		case Menu2:
			//SavePDF();
			break;
		case Menu3:
			toggleSymbols();
			break;
		case Menu4:
			toggleShading();
			break;
		case Menu5:
			toggleLines();
			break;
		}
		return false;
	}

	private void toggleSymbols()
    {
        if (showSymbols)
            showSymbols = false;
        else
            showSymbols = true;
        dateChart.showSymbols(showSymbols);
        SharedPref.setSymbols(sharedPref, showSymbols);
    }

    private void toggleShading()
    {
        if (showShading)
            showShading = false;
        else
            showShading = true;
        dateChart.showShading(showShading);
        SharedPref.setShading(sharedPref, showShading);
    }

    private void toggleLines()
    {
        if (showLines)
            showLines = false;
        else
            showLines = true;
        dateChart.showLines(showLines);
        SharedPref.setLines(sharedPref, showLines);
    }
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** when menu button option selected */
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onOptionsItemSelected(item);
	}

	public static boolean SdIsPresent()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private void SaveScreenshot()
	{
		if(SdIsPresent())
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName = "MoodTracker_";
			fileName += dateFormat.format(new Date(System.currentTimeMillis()));
			fileName += ".png";

			File dest = new File(Environment.getExternalStorageDirectory(), fileName);

			Bitmap bitmap = (Bitmap)dateChart.getScreenShot();
			Log.v("screenshot", "" + bitmap);
			try {
				FileOutputStream out = new FileOutputStream(dest);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
				Toast.makeText(this, "Chart saved to SDCard", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.v("save", e.toString());
				Toast.makeText(this, "Failed to save Chart", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "Unable to save, please check your SDCard", Toast.LENGTH_SHORT).show();
		}
	}

	public static final String EXTRA_GROUP_ID = "groupId";
	private long groupId;
	private Group group;

	protected double getValue(KeyItem item, double value) {
		if (!this.reverseLabels) {
			return value;
		}
		return 100 - value;
	}

	protected String getSettingSuffix() {
		return "scale";
	}

	protected ArrayList<String> getOptionItems() {
		ArrayList<String> items = new ArrayList<String>();
		items.add("Date Range");
		items.add("Toggle Symbols");
		items.add("Toggle Lines");
		items.add("Toggle Shading");
		return items;
	}
	
	protected ArrayList<KeyItem> getKeyItems() {
		group = new Group(this.dbAdapter);
		group._id = groupId;
		group.load();

		ArrayList<Scale> scales = group.getScales();
		ArrayList<KeyItem> items = new ArrayList<KeyItem>();

		for (int i = 0; i < scales.size(); ++i) {
			Scale scale = scales.get(i);
			KeyItem item = new KeyItem(
					scale._id,
					scale.max_label,
					scale.min_label
					);

			if (SharedPref.getKeyColor(sharedPref, "scl" + item.id) != 0)
				item.color = SharedPref.getKeyColor(sharedPref, "scl" + item.id);
			else
			{
				item.color = getKeyColor(i, scales.size());
				SharedPref.setKeyColor(sharedPref, "grp"+item.id, item.color);
			}

			if (SharedPref.getKeyResource(sharedPref, "scl" + item.id) != 0)
				item.resID = SharedPref.getKeyResource(sharedPref, "scl" + item.id);
			else
				item.resID = R.drawable.fivestar;

			items.add(item);
		}
		return items;
	}

	protected boolean isKeyItemsClickable() {
		return false;
	}

	protected int getKeyItemViewType() {
		return KeyItemAdapter.VIEW_TYPE_TWO_LINE;
	}

	protected void onKeysItemClicked(KeyItem keyItem, View view, int pos,
			long id) {
		return;
	}

	protected DataProvider getDataProvider() {
		return new ScaleResultsDataProvider(this.dbAdapter);
	}

	protected String getKeyTabText() {
		return getString(R.string.scales_tab);
	}
	
	class OptionItemAdapter extends ArrayAdapter<String> {

		private LayoutInflater layoutInflater;
		private int layoutId;

		public OptionItemAdapter(Context context, int viewType,
				List<String> objects) {
			super(context, viewType, objects);

			layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
			layoutId = R.layout.list_item_result_key_1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = layoutInflater.inflate(layoutId, null);
			}

			final String item = this.getItem(position);
			TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
			ToggleButton tb = (ToggleButton)convertView.findViewById(R.id.showKeyToggleButton);
			Button btn = (Button)convertView.findViewById(R.id.showKeyButton);
			ImageView keyBox = (ImageView) convertView.findViewById(R.id.keyBox);


			boolean tv1Null = tv1 == null;
			if(!tv1Null) {
				tv1.setText(item);
			}

			if(tb != null) {
				if(item == "Date Range") {

					keyBox.setImageResource(android.R.drawable.ic_menu_day);
					tb.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(new View.OnClickListener() {  
						public void onClick(View v)
						{
							showDialog(DIALOG_1);
						}
					});

				}
				else if(item == "Toggle Symbols")
				{
					keyBox.setImageResource(android.R.drawable.ic_menu_gallery);
					tb.setVisibility(View.VISIBLE);
					btn.setVisibility(View.GONE);
					tb.setOnCheckedChangeListener(null);
					tb.setChecked(showSymbols);
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(
								CompoundButton buttonView, boolean isChecked) {
							toggleSymbols();
							onKeyToggleButtonCheckedChanged();
						}
					});
				}
				else if(item == "Toggle Lines")
				{
					keyBox.setImageResource(android.R.drawable.ic_menu_crop);
					tb.setVisibility(View.VISIBLE);
					btn.setVisibility(View.GONE);
					tb.setOnCheckedChangeListener(null);
					tb.setChecked(showLines);
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(
								CompoundButton buttonView, boolean isChecked) {
							toggleLines();
							onKeyToggleButtonCheckedChanged();
						}
					});
				}
				else if(item == "Toggle Shading")
				{
					keyBox.setImageResource(android.R.drawable.ic_menu_crop);
					tb.setVisibility(View.VISIBLE);
					btn.setVisibility(View.GONE);
					tb.setOnCheckedChangeListener(null);
					tb.setChecked(showShading);
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(
								CompoundButton buttonView, boolean isChecked) {
							toggleShading();
							onKeyToggleButtonCheckedChanged();
						}
					});
				}
			}

			/*if(keyBox != null) {
				keyBox.setImageResource(item.resID);
				keyBox.setColorFilter(item.color);
			}*/

			return convertView;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		selectedDuration = arg2;

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
