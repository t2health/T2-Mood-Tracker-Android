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

import zencharts.data.DatePoint;
import zencharts.data.DateSeries;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.t2.vas.ArraysExtra;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.GroupResultsActivity.KeyItem;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.importexport.ImportExport;
import com.t2.vas.view.SeparatedListAdapter;

public class ExportActivity extends ABSActivity  implements OnClickListener, OnItemClickListener {
	private Button finishButton;
	private DatePickerDialog fromDatePicker;
	private DatePickerDialog toDatePicker;
	private static ArrayList<DateSeries> dateSeriesCollection = null;
	private long fromTime = 0;
	private long toTime = 0;

	private List<Long> groupIds;
	private List<String> otherIds;

	private SimpleDateFormat dateFormatter = new SimpleDateFormat(Global.SHARE_TIME_FORMAT);
	private Button fromTimeButton;
	private Button toTimeButton;
	private SimpleAdapter groupsAdapter;
	private SimpleAdapter otherItemsAdapter;
	private SeparatedListAdapter listAdapter;
	private ListView list;
	private ProgressDialog progressDialog;
	private ArrayList<KeyItem> keyItems;
	private static final String TAG = ABSExportActivity.class.getSimpleName();
	private static final int EXPORT_SUCCESS = 1;
	private static final int EXPORT_FAILED = 0;
	private static final String KEY_NAME = "results_visible_ids_";
	//private KeyItemAdapter keysAdapter;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.export_sdcard_not_mounted, Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}

		File exportDir = Global.EXPORT_DIR;
		if(!exportDir.exists() && !exportDir.mkdirs()) {
			this.finish();
			return;
		}

		// Build the progress dialog.
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(this.getProgressMessage());
		progressDialog.setCancelable(false);

		// Set the content view
		this.setContentView(R.layout.abs_import_export_activity);

		// Setup the buttons.
		fromTimeButton = (Button)this.findViewById(R.id.fromDate);
		fromTimeButton.setOnClickListener(this);

		toTimeButton = (Button)this.findViewById(R.id.toDate);
		toTimeButton.setOnClickListener(this);

		finishButton = (Button)this.findViewById(R.id.finishButton);
		finishButton.setOnClickListener(this);
		finishButton.setText(R.string.export_button);

		// Setup group items.
		groupsAdapter = this.getGroupsAdapter();

		// Setup the other clear items.
		otherItemsAdapter = this.getOtherItemsAdapter();

		// init the list adapter
		/*listAdapter = new SeparatedListAdapter(this);
		if(groupsAdapter != null) {
			listAdapter.addSection(getString(R.string.export_groups_header), groupsAdapter);
		}
		if(otherItemsAdapter != null) {
			listAdapter.addSection(getString(R.string.export_other_header), otherItemsAdapter);
		}*/

		list = (ListView)this.findViewById(R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setOnItemClickListener(this);
		//list.setAdapter(listAdapter);
		initListAdapter();

		// Prepare the keys adapter.
		keyItems = getKeyItems(); 

		// set the visibility for each key item.
		ArrayList<Long> visibleIds = getVisibleIds(getSettingSuffix());
		int keyCount = keyItems.size();
		for(int i = 0; i < keyCount; ++i) {
			KeyItem item = keyItems.get(i);
			item.visible = visibleIds.contains(item.id);
		}
		//keysAdapter = new KeyItemAdapter(this, getKeyItemViewType(), keyItems);

		// Build the from date dialog.
		Calendar cal = Calendar.getInstance();
		fromDatePicker = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						onFromDateSet(year, monthOfYear, dayOfMonth);
					}
				},
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
				);
		this.updateFromDate(cal.getTimeInMillis());

		// Build the to date dialog.
		toDatePicker = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						onToDateSet(year, monthOfYear, dayOfMonth);
					}
				},
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
				);
		this.updateToDate(cal.getTimeInMillis());
	}

	protected SimpleAdapter getGroupsAdapter() {
		
		ArrayList<HashMap<String,Object>> groupsItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		
		Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
		while(cursor.moveToNext()) {
			Group g = new Group(dbAdapter);
			g.load(cursor);
		
			if(g.getScalesCount() > 0)
			{
				item = new HashMap<String,Object>();
				item.put(Group.FIELD_TITLE, cursor.getString(cursor.getColumnIndex(Group.FIELD_TITLE)));
				item.put(Group.FIELD_ID, cursor.getLong(cursor.getColumnIndex(Group.FIELD_ID)));
				groupsItems.add(item);
			}
		}
		cursor.close();
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				groupsItems,
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
						Group.FIELD_TITLE, 
				},
				new int[] {
						android.R.id.text1,
				}
				);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == android.R.id.text1) {
					((TextView)view).setText(data.toString());
					return true;
				}

				return false;
			}
		});
		return adapter;
	}

	protected void onDataExportFailed() {
		Toast.makeText(this, R.string.fail_to_export_files, Toast.LENGTH_LONG).show();
	}

	protected void onFinishButtonPressed() {
		groupIds = new ArrayList<Long>();
		otherIds = new ArrayList<String>();

		ArrayList<HashMap<String,Object>> groupItems = this.getSelectedGroupsItems();
		for(int i = 0; i < groupItems.size(); ++i) {
			groupIds.add((Long)groupItems.get(i).get(Group.FIELD_ID));
		}

		ArrayList<HashMap<String,Object>> otherItems = this.getSelectedOtherItems();
		for(int i = 0; i < otherItems.size(); ++i) {
			otherIds.add(otherItems.get(i).get("id").toString());
		}


		// Start the progress dialog.
		//showProgressDialog();
		exportFileUris.clear();

		RadioButton rbPDF = (RadioButton) this.findViewById(R.id.rb_pdf);
		if(rbPDF.isChecked())
			exportPDF();
		else
			exportCSV();
	}

	private void exportCSV() {


		// Run the export on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				long fromTime = getFromTime();
				long toTime = getToTime();

				File exportDir = Global.EXPORT_DIR;
				if(!exportDir.exists() && !exportDir.mkdirs()) {
					fileExportCompleteHandler.sendEmptyMessage(EXPORT_FAILED);
					return;
				}

				File outputFile = new File(exportDir, getExportFilename(fromTime, toTime));

				if(outputFile.exists()) {
					if(!outputFile.delete()) {
						fileExportCompleteHandler.sendEmptyMessage(EXPORT_FAILED);
						return;
					}
				}

				// Export the groups.
				boolean exportGroupIds = groupIds.size() > 0;
				boolean exportNotes = otherIds.contains("notes");
				if(exportGroupIds) {
					ImportExport.exportGroupData(
							dbAdapter, 
							outputFile, 
							true,
							groupIds, 
							fromTime, 
							toTime
							);
				}

				// Export the notes.
				if(exportNotes) {
					ImportExport.exportNotesData(
							dbAdapter, 
							outputFile,
							true,
							fromTime, 
							toTime
							);
				}

				if(exportGroupIds || exportNotes) {
					exportFileUris.add(Uri.fromFile(outputFile));
				}

				fileExportCompleteHandler.sendEmptyMessage(EXPORT_SUCCESS);

			}
		}).start();
	}

	protected SimpleAdapter getOtherItemsAdapter() {
		SimpleAdapter otherItemsAdapter = new SimpleAdapter(
				this, 
				getOtherItems(), 
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
					"title", 
				},
				new int[] {
					android.R.id.text1,
				}
				);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		otherItemsAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == android.R.id.text1) {
					((TextView)view).setText(data.toString());
					return true;
				}

				return false;
			}
		});

		return otherItemsAdapter;
	}

	private ArrayList<Uri> exportFileUris = new ArrayList<Uri>();
	private Handler fileExportCompleteHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Hide the progress dialog.
			hideProgressDialog();

			if(msg.what == EXPORT_SUCCESS) {
				onDataExported(exportFileUris);
			} else if(msg.what == EXPORT_FAILED) {
				onDataExportFailed();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.fromDate:
			fromDatePicker.show();
			return;

		case R.id.toDate:
			toDatePicker.show();
			return;

		case R.id.finishButton:
			onFinishButtonPressed();
			return;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		setFinishButtonEnabled();
	}

	protected void onDataExported(ArrayList<Uri> uris) {
		if(uris.size() > 0) {
			Toast.makeText(this, R.string.files_exported, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.fail_to_export_files, Toast.LENGTH_LONG).show();
		}
	}

	protected String getExportFilename(long fromTime, long toTime) {
		String fileName = "MoodTracker_";
		fileName += dateFormat.format(new Date(System.currentTimeMillis()));

		RadioButton rbCSV = (RadioButton) this.findViewById(R.id.rb_csv);
		if(rbCSV.isChecked())
			fileName += ".csv";
		else
			fileName += ".pdf";

		return fileName;
	}

	protected String getProgressMessage() {
		return getString(R.string.export_progress_message);
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<HashMap<String,Object>> getSelectedGroupsItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(!isChecked) {
				continue;
			}

			Adapter adapter = listAdapter.getAdapterForItem(position);
			if(adapter == groupsAdapter) {
				items.add((HashMap<String, Object>) listAdapter.getItem(position));
			}
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<HashMap<String,Object>> getSelectedOtherItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(!isChecked) {
				continue;
			}

			Adapter adapter = listAdapter.getAdapterForItem(position);
			if(adapter == otherItemsAdapter) {
				items.add((HashMap<String, Object>) listAdapter.getItem(position));
			}
		}
		return items;
	}

	protected void updateFromDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		updateFromDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
				);
	}

	protected void updateToDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		updateToDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
				);
	}

	protected void updateFromDate(int year, int monthOfYear, int dayOfMonth) {
		this.fromDatePicker.updateDate(year, monthOfYear, dayOfMonth);
		onFromDateSet(year, monthOfYear, dayOfMonth);
	}

	protected void updateToDate(int year, int monthOfYear, int dayOfMonth) {
		this.toDatePicker.updateDate(year, monthOfYear, dayOfMonth);
		onToDateSet(year, monthOfYear, dayOfMonth);
	}

	private void onFromDateSet(int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		setFromTime(cal.getTimeInMillis());

		if(toTime < fromTime) {
			cal.setTimeInMillis(fromTime);
			cal.add(Calendar.DAY_OF_MONTH, 1);

			onToDateSet(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)
					);
		}

		setFinishButtonEnabled();
	}

	private void onToDateSet(int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		setToTime(cal.getTimeInMillis());

		if(fromTime > toTime) {
			cal.setTimeInMillis(toTime);
			cal.add(Calendar.DAY_OF_MONTH, -1);

			onFromDateSet(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)
					);
		}

		setFinishButtonEnabled();
	}

	private void setFromTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		fromTime = cal.getTimeInMillis();

		fromTimeButton.setText(
				dateFormatter.format(cal.getTime())
				);
	}

	private void setToTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		this.toTime = cal.getTimeInMillis();

		toTimeButton.setText(
				dateFormatter.format(cal.getTime())
				);
	}

	private void setFinishButtonEnabled() {
		finishButton.setEnabled(isFormDataValid());
	}

	private boolean isFormDataValid() {
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		boolean isListItemChecked = false;
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(isChecked) {
				isListItemChecked = true;
				break;
			}
		}

		boolean isTimeCorrect = false;
		if(fromTime < toTime) {
			isTimeCorrect = true;
		}

		return isListItemChecked && isTimeCorrect;
	}

	protected ArrayList<HashMap<String,Object>> getOtherItems() {
		ArrayList<HashMap<String,Object>> otherDataItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item = new HashMap<String,Object>();
		item.put("id", "notes");
		item.put("title", getString(R.string.clear_data_notes));
		otherDataItems.add(item);
		return otherDataItems;
	}

	protected void notifyDataSetChanged() {
		initListAdapter();
		listAdapter.notifyDataSetChanged();
	}

	private void initListAdapter() {
		listAdapter = new SeparatedListAdapter(this);
		if(groupsAdapter != null && groupsAdapter.getCount() > 0) {
			listAdapter.addSection(getString(R.string.export_groups_header), groupsAdapter);
		}
		if(otherItemsAdapter != null && otherItemsAdapter.getCount() > 0) {
			listAdapter.addSection(getString(R.string.export_other_header), otherItemsAdapter);
		}
		this.list.setAdapter(listAdapter);
	}

	protected long getFromTime() {
		return this.fromTime;
	}

	protected long getToTime() {
		return this.toTime;
	}

	protected void showProgressDialog() {
		this.progressDialog.show();
	}

	protected void hideProgressDialog() {
		this.progressDialog.hide();
	}

	protected ArrayList<KeyItem> getKeyItems() {
		ArrayList<KeyItem> items = new ArrayList<KeyItem>();

		Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
		while(cursor.moveToNext()) {
			Group group = new Group(dbAdapter);
			group.load(cursor);

			KeyItem item = new KeyItem(
					group._id,
					group.title,
					null
					);
			item.reverseData = group.inverseResults;

			if(SharedPref.getKeyColor(sharedPref, "grp"+item.id) != 0)
				item.color = SharedPref.getKeyColor(sharedPref, "grp"+item.id);
			else
				item.color = getKeyColor(cursor.getPosition(), cursor.getCount());

			if(SharedPref.getKeyResource(sharedPref, "grp"+item.id) != 0)
				item.resID = SharedPref.getKeyResource(sharedPref, "grp"+item.id);
			else
				item.resID = R.drawable.fivestar;

			items.add(item);

		}
		cursor.close();

		return items;
	}

	protected int getKeyColor(int currentIndex, int totalCount) {
		float hue = currentIndex / (1.00f * totalCount) * 360.00f;

		return Color.HSVToColor(
				255,
				new float[]{
						hue,
						1.0f,
						1.0f
				}
				);
	}

	protected String getSettingSuffix() {
		return "group";
	}

	private ArrayList<Long> getVisibleIds(String keySuffix) {
		String[] idsStrArr = SharedPref.getValues(
				sharedPref, 
				KEY_NAME+keySuffix, 
				",",
				new String[0]
				);

		return new ArrayList<Long>(
				Arrays.asList(
						ArraysExtra.toLongArray(idsStrArr)
						)
				);
	}

	protected double getValue(KeyItem item, double value) {
		if(item.reverseData) {
			return value;
		}
		return 100 - value;
	}
	
	private void exportPDF()
	{

		// Run the export on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {

				if(dateSeriesCollection == null)
					dateSeriesCollection = new ArrayList<DateSeries>();
				else
					dateSeriesCollection.clear();

				long startTime = getFromTime();
				long endTime = getToTime();

				// Build the data series for each enabled key.
				ArrayList<DataPoint> dataPoints = null;
				int kisize = keyItems.size();

				for(int i = 0; i < kisize; ++i) 
				{
					KeyItem item = keyItems.get(i);

					DateSeries series = new DateSeries(null, item.resID);
					series.lineColor = item.color;
					series.title = item.title1;
					series.id = item.id;
					series.visible = item.visible;
					series.symbolResID = item.resID;
					//			if(SharedPref.getKeyResource(sharedPref, ""+item.id) > 0) 
					//			{
					//				series.markerBitmap = BitmapFactory.decodeResource(getResources(), SharedPref.getKeyResource(sharedPref, ""+item.id));
					//				//series.markerSize = sharedPref.getInt(item.title1 + item.id+"size", 0);
					//			}


					// Get the data points
					DataProvider dataProvider = new GroupResultsDataProvider(dbAdapter);
					dataPoints = dataProvider.getGroupData(item.id, startTime, endTime); 
					//ArrayList<String> addedDays = new ArrayList<String>();

					//Cursor notes = null;
					int dpsize = dataPoints.size();
					for(int j = 0; j < dpsize; ++j) {
						DataPoint dp = dataPoints.get(j);

						if((dp.time >= startTime) && (dp.time <= endTime))
							series.add(new DatePoint(dp.time, (int) getValue(item, dp.getAverageValue()), ""));

					}
					dateSeriesCollection.add(series);

					float chartWidth = 332;
					float chartHeight = 45;


					Date cal = Calendar.getInstance().getTime();

					int pdftop = 900;
					Document document = new Document(); //PageSize.A4.rotate() in constr for landscape

					try
					{
						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(android.os.Environment.getExternalStorageDirectory() + java.io.File.separator + "T2MoodTracker" + java.io.File.separator + getExportFilename(fromTime, toTime)));
						document.open();
						PdfContentByte cb = writer.getDirectContent();
						BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

						//int pageCount = 0;
						int lineOffset = 160;
						int spaceLoop = 0;
						float spaceHeight = chartHeight + 30;
						int horizontalPos = 180;
						float verticalPos = ((pdftop - lineOffset) - (spaceLoop * spaceHeight));

						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
						String hDate = dateFormat.format(new Date(startTime));
						String lDate = dateFormat.format(new Date(endTime));

						cb.beginText();
						cb.setFontAndSize(bf, 20);
						cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "T2 MoodTracker Report", 300, verticalPos+60, 0);
						cb.setFontAndSize(bf, 14);
						cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "From " + hDate + " to " + lDate, 300, verticalPos+30, 0);
						cb.setFontAndSize(bf, 14);
						cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Generated on: " + cal.toLocaleString(), 300, verticalPos, 0);
						cb.endText();

						cb.setLineWidth(1f);
						verticalPos -= spaceHeight;

						//Loop over dateseriescollection and draw group charts
						for(int a=0; a<dateSeriesCollection.size();a++)
						{
							DateSeries cSeries = dateSeriesCollection.get(a);
							//if(cSeries.visible)
							if(groupIds.contains(cSeries.id))
							{
								//Draw a border rect
								cb.rectangle(horizontalPos, verticalPos, chartWidth, chartHeight);
								cb.stroke();

								cb.beginText();
								cb.setFontAndSize(bf, 12);
								cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, cSeries.title, 170, (verticalPos+(chartHeight/2))-5, 0);
								cb.endText();

								float lastY = -1;
								float xIncrement = chartWidth / cSeries.size();
								float yIncrement = chartHeight/100;
								int lCount = 0;

								float highValue= 0;
								long highTime = 0;
								float highY = 0;
								float highX = 0;
								long lowTime = 0;
								float lowY = 100;
								float lowX = chartWidth;
								float lowValue = 100;

								ArrayList<RegressionItem> ritems = new ArrayList<RegressionItem>();

								for(int c=0;c<cSeries.size();c++)
								{
									DatePoint dp = cSeries.get(c);
									
									if(lastY<0)lastY=dp.value;
									cb.setLineWidth(1f);
									cb.setRGBColorStrokeF(0,0,0);
									cb.moveTo(horizontalPos+(lCount * xIncrement), verticalPos+(lastY*yIncrement));
									cb.lineTo(horizontalPos+((lCount+1) * xIncrement), verticalPos+(dp.value*yIncrement));
									cb.stroke();
									cb.setRGBColorStrokeF(Color.red(cSeries.lineColor), Color.green(cSeries.lineColor), Color.blue(cSeries.lineColor));
									cb.moveTo(horizontalPos+(lCount * xIncrement), verticalPos+(lastY*yIncrement));
									cb.lineTo(horizontalPos+((lCount+1) * xIncrement), verticalPos+(dp.value*yIncrement));
									cb.stroke();
									cb.setRGBColorStrokeF(0,0,0);
									cb.setLineWidth(1f);


									/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
							Bitmap bitmap = BitmapFactory.decodeResource(getResources(), cSeries.symbolResID);
							Bitmap outBMP = Bitmap.createScaledBitmap(bitmap, 10, 10, true);

							outBMP.compress(Bitmap.CompressFormat.PNG, 100, stream);
							Image png = Image.getInstance(stream.toByteArray());
							png.setAbsolutePosition(horizontalPos+(lCount * xIncrement), verticalPos+(lastY*yIncrement));
							document.add(png);*/

									//Add regression Item
									ritems.add(new RegressionItem(lCount, (dp.value*yIncrement)));

									if(dp.value > highValue)
									{
										highValue = dp.value;
										highY = verticalPos+(dp.value*yIncrement);
										highX = horizontalPos+((lCount+1) * xIncrement);
										highTime = dp.timeStamp;
									}

									if(dp.value < lowValue)
									{
										lowValue = dp.value;
										lowY = verticalPos+(dp.value*yIncrement);
										lowX = horizontalPos+((lCount+1) * xIncrement);
										lowTime = dp.timeStamp;
									}


									lCount++;
									lastY = dp.value;
								}

								//Draw high low dates
								dateFormat = new SimpleDateFormat("MM/dd/yy");
								hDate = dateFormat.format(new Date(highTime));
								lDate = dateFormat.format(new Date(lowTime));
								cb.beginText();
								cb.setFontAndSize(bf, 8);
								cb.showTextAligned(PdfContentByte.ALIGN_CENTER, hDate, highX, highY, 0);
								cb.showTextAligned(PdfContentByte.ALIGN_CENTER, lDate, lowX, lowY, 0);
								cb.endText();

								//Draw Regression Line
								RegressionResult regression = calculateRegression(ritems);
								cb.saveState();
								cb.setRGBColorStrokeF(0,0,250);
								cb.setLineDash(3, 3, 0);
								cb.moveTo(horizontalPos,verticalPos+(float)regression.intercept);
								
								//cb.rectangle(horizontalPos, verticalPos, chartWidth, chartHeight);
								float rX = (float) ((verticalPos+regression.intercept)+(float) (regression.slope * (chartWidth/xIncrement)));
								if(rX > chartHeight) rX = chartHeight;
								if(rX < verticalPos) rX = verticalPos;
								
								cb.lineTo(horizontalPos+chartWidth, rX);
								cb.stroke();
								cb.restoreState();
								cb.setRGBColorStrokeF(0,0,0);

								verticalPos -= spaceHeight;
							}
						}


						//Loop over groups and draw scale charts
						for(int l=0; l<groupIds.size(); l++)
						{
							//Get the current group entry
							Long entry = groupIds.get(l);

							//New page for a new group
							document.newPage();
							verticalPos = (pdftop - (spaceHeight * 2));

							//Get the series data
							int rColor = 0;
							String groupName = "";
							for(int s = 0; s < dateSeriesCollection.size(); s++)
							{
								if(dateSeriesCollection.get(s).id == entry)
								{
									rColor = dateSeriesCollection.get(s).lineColor;
									groupName = dateSeriesCollection.get(s).title;
									break;
								}
							}

							//Draw Group Info
							verticalPos+=50;
							cb.beginText();
							cb.setFontAndSize(bf, 20);
							cb.showTextAligned(PdfContentByte.ALIGN_CENTER, groupName, 300, verticalPos, 0);
							cb.endText();

							verticalPos-=100;

							//Get scaleID's in group
							//Pull data
							Cursor scaleCursor = dbAdapter.GetRawCursor("select s._id, s.min_label, s.max_label from [scale] s where s.group_id = " + entry);

							while(scaleCursor.moveToNext())
							{

								//Draw a border rect
								cb.rectangle(horizontalPos, verticalPos, chartWidth, chartHeight);
								cb.stroke();

								cb.beginText();
								cb.setFontAndSize(bf, 12);
								cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, scaleCursor.getString(2), 170, (verticalPos+chartHeight)-10, 0);
								cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, scaleCursor.getString(1), 170, verticalPos, 0);
								cb.endText();

								//Get all the results in this scale
								String scaleID = scaleCursor.getString(0);
								Cursor valuesCursor = dbAdapter.GetRawCursor("select g.title, s.min_label, s.max_label, r.timestamp, r.value, g._id from result r join [group] g on g._id = r.group_id join scale s on s._id = r.scale_id where s._id = " + scaleID + " and r.timestamp > " + startTime + " and r.timestamp < " + endTime + " order by g.title, s.min_label, r.timestamp");// LIMIT " + chunkSize + " OFFSET " + rc);

								//float lastX = 0;
								float lastY = -1;
								float xIncrement = chartWidth / valuesCursor.getCount();
								float yIncrement = chartHeight/100;
								int lCount = 0;

								float highValue= 0;
								long highTime = 0;
								float highY = 0;
								float highX = 0;
								long lowTime = 0;
								float lowY = 100;
								float lowX = chartWidth;
								float lowValue = 100;

								ArrayList<RegressionItem> ritems = new ArrayList<RegressionItem>();

								while(valuesCursor.moveToNext())
								{
									
									/*if(highTime == 0)
									{
										highTime = valuesCursor.getLong(3);
										highValue = valuesCursor.getFloat(4);
									}*/
									
									
									if(lastY<0)lastY=valuesCursor.getFloat(4);
									cb.setLineWidth(1f);
									cb.setRGBColorStrokeF(0,0,0);
									cb.moveTo(horizontalPos+(lCount * xIncrement), verticalPos+(lastY*yIncrement));
									cb.lineTo(horizontalPos+((lCount+1) * xIncrement), verticalPos+(valuesCursor.getFloat(4)*yIncrement));
									cb.stroke();
									cb.setRGBColorStrokeF(Color.red(rColor), Color.green(rColor), Color.blue(rColor));
									cb.moveTo(horizontalPos+(lCount * xIncrement), verticalPos+(lastY*yIncrement));
									cb.lineTo(horizontalPos+((lCount+1) * xIncrement), verticalPos+(valuesCursor.getFloat(4)*yIncrement));
									cb.stroke();
									cb.setRGBColorStrokeF(0,0,0);
									cb.setLineWidth(1f);

									//Add regression Item
									ritems.add(new RegressionItem(lCount, (valuesCursor.getFloat(4)*yIncrement)));

									if(valuesCursor.getFloat(4) > highValue)
									{
										highValue = valuesCursor.getFloat(4);
										highY = verticalPos+(valuesCursor.getFloat(4)*yIncrement);
										highX = horizontalPos+((lCount+1) * xIncrement);
										highTime = valuesCursor.getLong(3);
									}

									if(valuesCursor.getFloat(4) < lowValue)
									{
										lowValue = valuesCursor.getFloat(4);
										lowY = verticalPos+(valuesCursor.getFloat(4)*yIncrement);
										lowX = horizontalPos+((lCount+1) * xIncrement);
										lowTime = valuesCursor.getLong(3);
									}


									lCount++;
									lastY = valuesCursor.getFloat(4);
								}

								//Draw high low dates
								dateFormat = new SimpleDateFormat("MM/dd/yy");
								hDate = dateFormat.format(new Date(highTime));
								lDate = dateFormat.format(new Date(lowTime));
								cb.beginText();
								cb.setFontAndSize(bf, 8);
								cb.showTextAligned(PdfContentByte.ALIGN_CENTER, hDate, highX, highY, 0);
								cb.showTextAligned(PdfContentByte.ALIGN_CENTER, lDate, lowX, lowY, 0);
								cb.endText();

								//Draw Regression Line
								RegressionResult regression = calculateRegression(ritems);
								cb.saveState();
								cb.setRGBColorStrokeF(0,0,250);
								cb.setLineDash(3, 3, 0);
								cb.moveTo(horizontalPos,verticalPos+(float)regression.intercept);
								
								float rX = (float) ((verticalPos+regression.intercept)+(float) (regression.slope * (chartWidth/xIncrement)));
								if(rX > chartHeight) rX = chartHeight;
								if(rX < verticalPos) rX = verticalPos;
								
								cb.lineTo(horizontalPos+chartWidth,rX);
								cb.stroke();
								cb.restoreState();
								cb.setRGBColorStrokeF(0,0,0);

								valuesCursor.close();
								verticalPos -= spaceHeight;
							}

							scaleCursor.close();

						}

						boolean exportNotes = otherIds.contains("notes");
						if(exportNotes) 
						{
							// Draw the Notes
							document.newPage();
							verticalPos = (pdftop - (spaceHeight * 2));
							Cursor notesCursor = new Note(dbAdapter).queryForNotes(startTime, endTime, "timestamp DESC");

							cb.beginText();
							cb.setFontAndSize(bf, 20);
							cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "T2 MoodTracker Notes", 300, verticalPos+60, 0);
							cb.endText();

							while(notesCursor.moveToNext()) {

								dateFormat = new SimpleDateFormat("MM/dd/yy hh.mm aa");
								String date = dateFormat.format(new Date(Long.parseLong(notesCursor.getString(1))));
								Paragraph p = new Paragraph(date + " - " + notesCursor.getString(2));
								document.add(p);
								document.add(new Paragraph("                                "));

								verticalPos -= spaceHeight;

							}
						}


					}
					catch(Exception ex){
						Log.v("ERROR", ex.toString());
					}
					document.close();

				}
				File outputFile = new File(android.os.Environment.getExternalStorageDirectory() + java.io.File.separator + "T2MoodTracker" + java.io.File.separator + getExportFilename(fromTime, toTime));
				exportFileUris.add(Uri.fromFile(outputFile));
				fileExportCompleteHandler.sendEmptyMessage(EXPORT_SUCCESS);
			}
		}).start();

	}

	public RegressionResult calculateRegression(ArrayList<RegressionItem> inArray)
	{
		RegressionResult result = new RegressionResult();

		int count = inArray.size();
		double sumY = 0.0;
		double sumX = 0.0;
		double sumXY = 0.0;
		double sumX2 = 0.0;
		double sumY2 = 0.0;

		for(int l=0;l<count;l++)
		{
			RegressionItem item = inArray.get(l);

			sumX += item.xValue;
			sumY += item.yValue;
			sumXY += (item.xValue * item.yValue);
			sumX2 += (item.xValue * item.xValue);
			sumY2 += (item.yValue * item.yValue);
		}

		result.slope = ((count * sumXY) - sumX * sumY) / ((count * sumX2) - (sumX * sumX));
		result.intercept = ((sumY - (result.slope * sumX))/count);
		result.correlation = Math.abs((count * sumXY) - (sumX * sumY)) / (Math.sqrt((count * sumX2 - sumX * sumX) * (count * sumY2 - (sumY * sumY))));

		return result;
	}

	public class RegressionItem
	{
		public double xValue =0;
		public double yValue =0;

		RegressionItem(double xv, double yv)
		{
			xValue = xv;
			yValue = yv;
		}

	}

	public class RegressionResult
	{
		public double slope =0;
		public double intercept =0;
		public double correlation = 0;
	}
}
