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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.t2.vas.Analytics;
import com.t2.vas.ArraysExtra;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.data.NotesDataProvider;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.view.ColorPickerDialog;
import com.t2.vas.view.SimpleCursorDateSectionAdapter;
import com.t2.vas.view.ToggledButton;

public class GroupResultsActivity extends ABSActivity implements
        AdapterView.OnItemSelectedListener, OnClickListener, OnItemClickListener,
        GestureDetector.OnGestureListener {

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

    private static final int Menu1 = Menu.FIRST + 1;
    // private static final int Menu2 = Menu.FIRST + 2;

    private DateChart dateChart;

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

    String[] durationItemsText = {
            "30 Days", "90 Days", "180 Days", "1 Year"
    };
    int[] durationItemsNum = {
            30, 90, 180, 365
    };
    int selectedDuration = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        resourceIDS = new int[] {
                R.drawable.circle, R.drawable.clover, R.drawable.club, R.drawable.clover,
                R.drawable.cross, R.drawable.davidstar, R.drawable.diamondring, R.drawable.doublehook, R.drawable.fivestar,
                R.drawable.heart, R.drawable.hexagon, R.drawable.hourglass, R.drawable.octogon,
                R.drawable.pentagon, R.drawable.quadstar, R.drawable.spade, R.drawable.square,
                R.drawable.triangle
        };

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

        Intent intent = this.getIntent();
        this.calendarField = intent.getIntExtra(EXTRA_CALENDAR_FIELD, Calendar.DAY_OF_MONTH);
        this.reverseLabels = intent.getBooleanExtra(EXTRA_REVERSE_DATA, false);

        // Set the time ranges.
        /*
         * startCal = Calendar.getInstance();
         * startCal.setTimeInMillis(MathExtra.roundTime(startTime,
         * calendarField)); startCal.set(calendarField,
         * startCal.getMinimum(calendarField)); endCal = Calendar.getInstance();
         * endCal.setTimeInMillis(startCal.getTimeInMillis());
         * endCal.add(Calendar.MONTH, 1);
         */

        // Set the content view.
        this.setContentView(R.layout.abs_results_activity);
        collapseList = (FrameLayout) this.findViewById(R.id.collapseList);
        dateChart = (DateChart) this.findViewById(R.id.datechart);
        dateChart.loadFont("Elronmonospace.ttf", 16, 2, 2);
        dateChart.maxValueManual = 100;
        
        
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

        // set the visibility for each key item.
        ArrayList<Long> visibleIds = getVisibleIds(getSettingSuffix());
        int keyCount = keyItems.size();
        for (int i = 0; i < keyCount; ++i) {
            KeyItem item = keyItems.get(i);
            if (visibleIds.contains(item.id)) // Changed to save only toggled
                                              // off items (they default on)
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

        try
        {
            optionsTabButton = (ToggledButton) this.findViewById(R.id.optionsTabButton);
            optionsTabButton.setOnClickListener(this);
            optionsList = (ListView) this.findViewById(R.id.optionsList);
            // ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
            // R.layout.simple_list_item, new String[] {"Date Range",
            // "Toggle Symbols", "Toggle Lines", "Toggle Shading"});
            optionsList.setAdapter(new OptionItemAdapter(this, 1, getOptionItems()));
            optionsList.setOnItemClickListener(this);
        } catch (Exception ex) {
        }

        // Load data and populate chart
        generateChart();
        showKeysTab();
        
        //Show a hint
        Toast.makeText(this, R.string.chart_hint, Toast.LENGTH_LONG).show();
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
        } catch (Exception ex) {
        }

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
        } else if (arg0 == optionsList) {

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
        // dateChart.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // dateChart.onPause();
    }

    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_1: {

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

                Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, durationItemsText);
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
            DateTime startTime = new DateTime().minusDays(durationItemsNum[selectedDuration])
                    .withTime(0, 0, 0, 0);
            DateTime endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);
            dateChart.setPeriod(new Duration(startTime, endTime));
            dateChart.setPeriodStartTime(startTime);
            generateChart();
        } catch (Exception ex)
        {
        }
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

            // Lock to timeframe
            DateTime startTime = new DateTime().minusDays(durationItemsNum[selectedDuration])
                    .withTime(0, 0, 0, 0);
            DateTime endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);

            for (int i = 0; i < kisize; ++i) {
                KeyItem item = keyItems.get(i);

                // update progressbar
                m_ProgressDialog.setProgress(i);

                DateSeries series = new DateSeries(this, item.resID);
                series.lineColor = item.color;
                series.title = item.title1;
                series.id = item.id;
                series.visible = item.visible;
                series.symbolResID = item.resID;
                // if(SharedPref.getKeyResource(sharedPref, ""+item.id) > 0)
                // {
                // series.markerBitmap =
                // BitmapFactory.decodeResource(getResources(),
                // SharedPref.getKeyResource(sharedPref, ""+item.id));
                // //series.markerSize = sharedPref.getInt(item.title1 +
                // item.id+"size", 0);
                // }

                series.lineWidth = (2 * displayMetrics.density);

                // Get the data points
                dataPoints = dataProvider.getGroupData(item.id, startTime.getMillis(),
                        endTime.getMillis());
                // dataPoints = dataProvider.getAllGroupData(item.id); //
                // loadAllData(keyItems.get(i));

                // Cursor notes = null;
                int dpsize = dataPoints.size();
                for (int j = 0; j < dpsize; ++j) {
                    DataPoint dp = dataPoints.get(j);

                    // if((dp.time >= startTime.getMillis()) && (dp.time <=
                    // endTime.getMillis()))

                    series.add(new DatePoint(dp.time, (int) getValue(item, dp.getAverageValue()),
                            ""));

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
            } catch (Exception ex)
            {
                // Likely no data (first run?)
                // Set timeperiod for today
                startTime = new DateTime().withTime(0, 0, 0, 0);
                endTime = new DateTime().plusDays(1).withTime(0, 0, 0, 0);
                dateChart.setPeriod(new Duration(startTime, endTime));
                dateChart.setPeriodStartTime(startTime);
            }

        } catch (Exception ex)
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
        try
        {
            dateChart.clearChart();

            /*
             * DateTime startTime = null; DateTime endTime = null;
             */

            for (int i = 0; i < dateSeriesCollection.size(); i++)
            {
                dateChart.addSeries(dateSeriesCollection.get(i));
            }

            /*
             * startTime = new
             * DateTime(dateSeriesCollection.get(0).get(0).timeStamp); endTime =
             * new
             * DateTime(dateSeriesCollection.get(0).get(dateSeriesCollection.
             * get(0).size() - 1).timeStamp); dateChart.setPeriod(new
             * Duration(startTime, endTime));
             * dateChart.setPeriodStartTime(startTime);
             */
            
            dateChart.showSymbols(showSymbols);
            dateChart.showLines(showLines);
            dateChart.showShading(showShading);

            if (m_ProgressDialog != null)
                m_ProgressDialog.dismiss();
        } catch (Exception ex) {
        }
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
        ColorPickerDialog cp = new ColorPickerDialog(this, SharedPref.getKeyColor(sharedPref, "grp"
                + item.id), resourceIDS, new ColorPickerDialog.OnColorPickerListener() {

            @Override
            public void onCancel(ColorPickerDialog dialog) {

            }

            @Override
            public void onOk(ColorPickerDialog dialog, int color, int selectedResID) {
                item.color = color;
                item.resID = selectedResID;

                SharedPref.setKeyColor(sharedPref, "grp" + item.id, color);
                SharedPref.setKeyResource(sharedPref, "grp" + item.id, selectedResID);
                updateIconsColors();
            }

        }, SharedPref.getKeyResource(sharedPref, "grp" + item.id));
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
                        if (buttonView.getTag() instanceof KeyItem) {
                            KeyItem item = (KeyItem) buttonView.getTag();
                            Analytics.onEvent(GroupResultsActivity.this, "Graph Changed,"
                                    + item.title1
                                    + (item.title2 == null ? "" : ("-" + item.title2))
                                    + "," + (isChecked ? "Enabled" : "Disabled"));
                        }

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

    class OptionItemAdapter extends ArrayAdapter<String> {

        private LayoutInflater layoutInflater;
        private int layoutId;

        public OptionItemAdapter(Context context, int viewType,
                List<String> objects) {
            super(context, viewType, objects);

            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            layoutId = R.layout.list_item_result_key_1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(layoutId, null);
            }

            final String item = this.getItem(position);
            TextView tv1 = (TextView) convertView.findViewById(R.id.text1);
            ToggleButton tb = (ToggleButton) convertView.findViewById(R.id.showKeyToggleButton);
            Button btn = (Button) convertView.findViewById(R.id.showKeyButton);
            ImageView keyBox = (ImageView) convertView.findViewById(R.id.keyBox);

            boolean tv1Null = tv1 == null;
            if (!tv1Null) {
                tv1.setText(item);
            }

            if (tb != null) {
                if (item == "Date Range") {

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
                else if (item == "Toggle Symbols")
                {
                    keyBox.setImageResource(android.R.drawable.ic_menu_gallery);
                    tb.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    tb.setOnCheckedChangeListener(null);
                    tb.setChecked(showSymbols);
                    tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(
                                CompoundButton buttonView, boolean isChecked) {
                            toggleSymbols();
                            onKeyToggleButtonCheckedChanged();
                        }
                    });
                }
                else if (item == "Toggle Lines")
                {
                    keyBox.setImageResource(android.R.drawable.ic_menu_crop);
                    tb.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    tb.setOnCheckedChangeListener(null);
                    tb.setChecked(showLines);
                    tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(
                                CompoundButton buttonView, boolean isChecked) {
                            toggleLines();
                            onKeyToggleButtonCheckedChanged();
                        }
                    });
                }
                else if (item == "Toggle Shading")
                {
                    keyBox.setImageResource(android.R.drawable.ic_menu_crop);
                    tb.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                    tb.setOnCheckedChangeListener(null);
                    tb.setChecked(showShading);
                    tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(
                                CompoundButton buttonView, boolean isChecked) {
                            toggleShading();
                            onKeyToggleButtonCheckedChanged();
                        }
                    });
                }
            }

            /*
             * if(keyBox != null) { keyBox.setImageResource(item.resID);
             * keyBox.setColorFilter(item.color); }
             */

            return convertView;
        }
    }

    public void populateMenu(Menu menu) {

        menu.setQwertyMode(true);

        MenuItem item1 = menu.add(0, Menu1, 0, "Save Screenshot");
        {
            // item1.setAlphabeticShortcut('a');
            item1.setIcon(android.R.drawable.ic_menu_camera);
        }
        /*
         * MenuItem item2 = menu.add(0, Menu2, 0, "Save As PDF"); {
         * //item1.setAlphabeticShortcut('a');
         * item2.setIcon(android.R.drawable.ic_menu_save); }
         */
    }

    public boolean applyMenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case Menu1:
                SaveScreenshot();
                break;
        /*
         * case Menu2: //setRequestedOrientation(5); m_ProgressDialog = new
         * ProgressDialog(this); m_ProgressDialog.setTitle("Please wait...");
         * m_ProgressDialog.setMessage("Generating PDF file.");
         * m_ProgressDialog.setIndeterminate(false);
         * m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         * m_ProgressDialog.show(); Runnable myRunnable = new Runnable() {
         * public void run() { GeneratePDF();
         * runOnUiThread(saveSharePDFRunnable); } }; Thread thread = new
         * Thread(null, myRunnable, "ChartThread"); thread.start(); break;
         */

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return applyMenuChoice(item) || super.onOptionsItemSelected(item);
    }

    public static boolean SdIsPresent()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // private String pdfdata = "";
    // private Runnable saveSharePDFRunnable = new Runnable()
    // {
    // public void run()
    // {
    // saveSharePDF();
    // }
    // };

    /*
     * private void GeneratePDF() { if(SdIsPresent()) { float chartWidth = 332;
     * float chartHeight = 45; Date cal = Calendar.getInstance().getTime(); int
     * pdftop = 900; Document document = new Document(); //PageSize.A4.rotate()
     * in constr for landscape try { PdfWriter writer =
     * PdfWriter.getInstance(document, new
     * FileOutputStream(android.os.Environment.getExternalStorageDirectory() +
     * java.io.File.separator + "T2MoodTracker" + java.io.File.separator +
     * "finalPDF.pdf")); document.open(); PdfContentByte cb =
     * writer.getDirectContent(); BaseFont bf =
     * BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
     * BaseFont.NOT_EMBEDDED); //int pageCount = 0; int lineOffset = 160; int
     * spaceLoop = 0; float spaceHeight = chartHeight + 30; int horizontalPos =
     * 180; float verticalPos = ((pdftop - lineOffset) - (spaceLoop *
     * spaceHeight)); //Lock to timeframe DateTime startTime = new
     * DateTime().minusDays(durationItemsNum[selectedDuration]).withTime(0, 0,
     * 0, 0); DateTime endTime = new DateTime().plusDays(1).withTime(0, 0, 0,
     * 0); SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
     * String hDate = dateFormat.format(new Date(startTime.getMillis())); String
     * lDate = dateFormat.format(new Date(endTime.getMillis())); cb.beginText();
     * cb.setFontAndSize(bf, 20);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "T2 MoodTracker Report",
     * 300, verticalPos+60, 0); cb.setFontAndSize(bf, 14);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "From " + hDate + " to "
     * + lDate, 300, verticalPos+30, 0); cb.setFontAndSize(bf, 14);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Generated on: " +
     * cal.toLocaleString(), 300, verticalPos, 0); cb.endText(); //Pull visible
     * groups ArrayList<Long> ids = new ArrayList<Long>(); for(int s = 0; s <
     * dateSeriesCollection.size(); s++) {
     * if(dateSeriesCollection.get(s).visible)
     * ids.add(dateSeriesCollection.get(s).id); }
     * m_ProgressDialog.setMax(ids.size()); cb.setLineWidth(1f); verticalPos -=
     * spaceHeight; //Loop over dateseriescollection and draw group charts
     * for(int a=0; a<dateSeriesCollection.size();a++) { DateSeries cSeries =
     * dateSeriesCollection.get(a); if(cSeries.visible) { //Draw a border rect
     * cb.rectangle(horizontalPos, verticalPos, chartWidth, chartHeight);
     * cb.stroke(); cb.beginText(); cb.setFontAndSize(bf, 12);
     * cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, cSeries.title, 170,
     * (verticalPos+(chartHeight/2))-5, 0); cb.endText(); float lastY = -1;
     * float xIncrement = chartWidth / cSeries.size(); float yIncrement =
     * chartHeight/100; int lCount = 0; float highValue= 0; int highTime = 0;
     * float highY = 0; float highX = 0; int lowTime = 0; float lowY = 100;
     * float lowX = chartWidth; float lowValue = 100; ArrayList<RegressionItem>
     * ritems = new ArrayList<RegressionItem>(); for(int
     * c=0;c<cSeries.size();c++) { DatePoint dp = cSeries.get(c);
     * if(lastY<0)lastY=dp.value; cb.setLineWidth(1f);
     * cb.setRGBColorStrokeF(0,0,0); cb.moveTo(horizontalPos+(lCount *
     * xIncrement), verticalPos+(lastY*yIncrement));
     * cb.lineTo(horizontalPos+((lCount+1) * xIncrement),
     * verticalPos+(dp.value*yIncrement)); cb.stroke();
     * cb.setRGBColorStrokeF(Color.red(cSeries.lineColor),
     * Color.green(cSeries.lineColor), Color.blue(cSeries.lineColor));
     * cb.moveTo(horizontalPos+(lCount * xIncrement),
     * verticalPos+(lastY*yIncrement)); cb.lineTo(horizontalPos+((lCount+1) *
     * xIncrement), verticalPos+(dp.value*yIncrement)); cb.stroke();
     * cb.setRGBColorStrokeF(0,0,0); cb.setLineWidth(1f); ByteArrayOutputStream
     * stream = new ByteArrayOutputStream(); Bitmap bitmap =
     * BitmapFactory.decodeResource(getResources(), cSeries.symbolResID); Bitmap
     * outBMP = Bitmap.createScaledBitmap(bitmap, 10, 10, true);
     * outBMP.compress(Bitmap.CompressFormat.PNG, 100, stream); Image png =
     * Image.getInstance(stream.toByteArray());
     * png.setAbsolutePosition(horizontalPos+(lCount * xIncrement),
     * verticalPos+(lastY*yIncrement)); document.add(png); //Add regression Item
     * ritems.add(new RegressionItem(lCount, (dp.value*yIncrement)));
     * if(dp.value > highValue) { highValue = dp.value; highY =
     * verticalPos+(dp.value*yIncrement); highX = horizontalPos+((lCount+1) *
     * xIncrement); highTime = (int) dp.timeStamp; } if(dp.value < lowValue) {
     * lowValue = dp.value; lowY = verticalPos+(dp.value*yIncrement); lowX =
     * horizontalPos+((lCount+1) * xIncrement); lowTime = (int) dp.timeStamp; }
     * lCount++; lastY = dp.value; } //Draw high low dates dateFormat = new
     * SimpleDateFormat("MM/dd/yy"); hDate = dateFormat.format(new
     * Date(highTime)); lDate = dateFormat.format(new Date(lowTime));
     * cb.beginText(); cb.setFontAndSize(bf, 8);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, hDate, highX, highY, 0);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, lDate, lowX, lowY, 0);
     * cb.endText(); //Draw Regression Line RegressionResult regression =
     * calculateRegression(ritems); cb.saveState();
     * cb.setRGBColorStrokeF(0,0,250); cb.setLineDash(3, 3, 0);
     * cb.moveTo(horizontalPos,verticalPos+(float)regression.intercept);
     * cb.lineTo(horizontalPos+chartWidth,(float)
     * ((verticalPos+regression.intercept)+(float) (regression.slope *
     * (chartWidth/xIncrement)))); cb.stroke(); cb.restoreState();
     * cb.setRGBColorStrokeF(0,0,0); verticalPos -= spaceHeight; } } //Loop over
     * visible groups and draw scale charts for(int l=0; l<ids.size(); l++) {
     * //Get the current group entry Long entry = ids.get(l); //New page for a
     * new group document.newPage(); verticalPos = (pdftop - (spaceHeight * 2));
     * //Get the series data int rColor = 0; String groupName = ""; for(int s =
     * 0; s < dateSeriesCollection.size(); s++) {
     * if(dateSeriesCollection.get(s).id == entry) { rColor =
     * dateSeriesCollection.get(s).lineColor; groupName =
     * dateSeriesCollection.get(s).title; break; } } //Draw Group Info
     * verticalPos+=50; cb.beginText(); cb.setFontAndSize(bf, 20);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, groupName, 300,
     * verticalPos, 0); cb.endText(); verticalPos-=100; //Get scaleID's in group
     * //Pull data Cursor scaleCursor = dbAdapter.GetRawCursor(
     * "select s._id, s.min_label, s.max_label from [scale] s where s.group_id = "
     * + entry); while(scaleCursor.moveToNext()) { //Draw a border rect
     * cb.rectangle(horizontalPos, verticalPos, chartWidth, chartHeight);
     * cb.stroke(); cb.beginText(); cb.setFontAndSize(bf, 12);
     * cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, scaleCursor.getString(1),
     * 170, (verticalPos+chartHeight)-10, 0);
     * cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, scaleCursor.getString(2),
     * 170, verticalPos, 0); cb.endText(); //Get all the results in this scale
     * String scaleID = scaleCursor.getString(0); Cursor valuesCursor =
     * dbAdapter.GetRawCursor(
     * "select g.title, s.min_label, s.max_label, r.timestamp, r.value, g._id from result r join [group] g on g._id = r.group_id join scale s on s._id = r.scale_id where s._id = "
     * + scaleID + " and r.timestamp > " + startTime.getMillis() +
     * " and r.timestamp < " + endTime.getMillis() +
     * " order by g.title, s.min_label, r.timestamp");// LIMIT " + chunkSize + "
     * OFFSET " + rc); //float lastX = 0; float lastY = -1; float xIncrement =
     * chartWidth / valuesCursor.getCount(); float yIncrement = chartHeight/100;
     * int lCount = 0; float highValue= 0; int highTime = 0; float highY = 0;
     * float highX = 0; int lowTime = 0; float lowY = 100; float lowX =
     * chartWidth; float lowValue = 100; ArrayList<RegressionItem> ritems = new
     * ArrayList<RegressionItem>(); while(valuesCursor.moveToNext()) {
     * if(lastY<0)lastY=valuesCursor.getFloat(4); cb.setLineWidth(1f);
     * cb.setRGBColorStrokeF(0,0,0); cb.moveTo(horizontalPos+(lCount *
     * xIncrement), verticalPos+(lastY*yIncrement));
     * cb.lineTo(horizontalPos+((lCount+1) * xIncrement),
     * verticalPos+(valuesCursor.getFloat(4)*yIncrement)); cb.stroke();
     * cb.setRGBColorStrokeF(Color.red(rColor), Color.green(rColor),
     * Color.blue(rColor)); cb.moveTo(horizontalPos+(lCount * xIncrement),
     * verticalPos+(lastY*yIncrement)); cb.lineTo(horizontalPos+((lCount+1) *
     * xIncrement), verticalPos+(valuesCursor.getFloat(4)*yIncrement));
     * cb.stroke(); cb.setRGBColorStrokeF(0,0,0); cb.setLineWidth(1f); //Add
     * regression Item ritems.add(new RegressionItem(lCount,
     * (valuesCursor.getFloat(4)*yIncrement))); if(valuesCursor.getFloat(4) >
     * highValue) { highValue = valuesCursor.getFloat(4); highY =
     * verticalPos+(valuesCursor.getFloat(4)*yIncrement); highX =
     * horizontalPos+((lCount+1) * xIncrement); highTime =
     * valuesCursor.getInt(3); } if(valuesCursor.getFloat(4) < lowValue) {
     * lowValue = valuesCursor.getFloat(4); lowY =
     * verticalPos+(valuesCursor.getFloat(4)*yIncrement); lowX =
     * horizontalPos+((lCount+1) * xIncrement); lowTime =
     * valuesCursor.getInt(3); } lCount++; lastY = valuesCursor.getFloat(4); }
     * //Draw high low dates dateFormat = new SimpleDateFormat("MM/dd/yy");
     * hDate = dateFormat.format(new Date(highTime)); lDate =
     * dateFormat.format(new Date(lowTime)); cb.beginText();
     * cb.setFontAndSize(bf, 8); cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
     * hDate, highX, highY, 0); cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
     * lDate, lowX, lowY, 0); cb.endText(); //Draw Regression Line
     * RegressionResult regression = calculateRegression(ritems);
     * cb.saveState(); cb.setRGBColorStrokeF(0,0,250); cb.setLineDash(3, 3, 0);
     * cb.moveTo(horizontalPos,verticalPos+(float)regression.intercept);
     * cb.lineTo(horizontalPos+chartWidth,(float)
     * ((verticalPos+regression.intercept)+(float) (regression.slope *
     * (chartWidth/xIncrement)))); cb.stroke(); cb.restoreState();
     * cb.setRGBColorStrokeF(0,0,0); valuesCursor.close(); verticalPos -=
     * spaceHeight; } scaleCursor.close(); } { // Draw the Notes
     * document.newPage(); verticalPos = (pdftop - (spaceHeight * 2)); Cursor
     * notesCursor = new Note(dbAdapter).queryForNotes(startTime.getMillis(),
     * endTime.getMillis(), "timestamp DESC"); cb.beginText();
     * cb.setFontAndSize(bf, 20);
     * cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "T2 MoodTracker Notes",
     * 300, verticalPos+60, 0); cb.endText(); while(notesCursor.moveToNext()) {
     * dateFormat = new SimpleDateFormat("MM/dd/yy hh.mm aa"); String date =
     * dateFormat.format(new Date(Long.parseLong(notesCursor.getString(1))));
     * Paragraph p = new Paragraph(date + " - " + notesCursor.getString(2));
     * document.add(p); document.add(new
     * Paragraph("                                ")); verticalPos -=
     * spaceHeight; } } } catch(Exception ex){ Log.v("ERROR", ex.toString()); }
     * document.close(); } }
     */

    public RegressionResult calculateRegression(ArrayList<RegressionItem> inArray)
    {
        RegressionResult result = new RegressionResult();

        int count = inArray.size();
        double sumY = 0.0;
        double sumX = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;

        for (int l = 0; l < count; l++)
        {
            RegressionItem item = inArray.get(l);

            sumX += item.xValue;
            sumY += item.yValue;
            sumXY += (item.xValue * item.yValue);
            sumX2 += (item.xValue * item.xValue);
            sumY2 += (item.yValue * item.yValue);
        }

        result.slope = ((count * sumXY) - sumX * sumY) / ((count * sumX2) - (sumX * sumX));
        result.intercept = ((sumY - (result.slope * sumX)) / count);
        result.correlation = Math.abs((count * sumXY) - (sumX * sumY))
                / (Math.sqrt((count * sumX2 - sumX * sumX) * (count * sumY2 - (sumY * sumY))));

        return result;
    }

    /*
     * private void saveSharePDF() { if(m_ProgressDialog != null) try {
     * m_ProgressDialog.dismiss(); } catch(Exception ex){} SimpleDateFormat
     * dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); String fileName =
     * "MoodTracker_"; fileName += dateFormat.format(new
     * Date(System.currentTimeMillis())); fileName += ".pdf"; File root = new
     * File(Environment.getExternalStorageDirectory(), "T2MoodTracker"); File
     * from = new File(root,"finalPDF.pdf"); File to = new File(root,fileName);
     * from.renameTo(to); Toast.makeText(this, "PDF saved to SD Card",
     * Toast.LENGTH_SHORT).show(); Uri uri = Uri.fromFile(to); Intent i = new
     * Intent(Intent.ACTION_SEND);
     * //i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {
     * "stephen.ody@tee2.org" }); i.putExtra(Intent.EXTRA_SUBJECT,
     * "PDF From MoodTracker3"); i.putExtra(Intent.EXTRA_TEXT,
     * "Report included as PDF attachment."); i.putExtra(Intent.EXTRA_STREAM,
     * uri); i.setType("text/plain"); startActivity(Intent.createChooser(i,
     * "Send PDF via email")); i = new Intent(Intent.ACTION_VIEW);
     * i.setDataAndType(uri, "application/pdf"); startActivity(i);
     * //setRequestedOrientation(4); }
     */

    /*
     * public ArrayList<String[]> pullPDFGroupData(List<Long> groupIds, long
     * startTime, long endTime) { ArrayList<String[]> outArray = new
     * ArrayList<String[]>(); Group group; for(int i = 0; i < groupIds.size();
     * ++i) { group = new Group(dbAdapter); group._id = groupIds.get(i);
     * group.load(); int lineColor = 0; for(int s = 0; s <
     * dateSeriesCollection.size(); s++) { if(dateSeriesCollection.get(s).id ==
     * group._id) { lineColor = dateSeriesCollection.get(s).lineColor; } }
     * Cursor cursor; Scale scale; ArrayList<Scale> scales = group.getScales();
     * for(int j = 0; j < scales.size(); ++j) { scale = scales.get(j); cursor =
     * scale.getResults(startTime, endTime); int timeIndex =
     * cursor.getColumnIndex("timestamp"); int valueIndex =
     * cursor.getColumnIndex("value"); while(cursor.moveToNext()) { String[]
     * entry = new String[6]; entry[0] = group.title; entry[1] = scale.min_label
     * + "/" + scale.max_label; entry[2] = ""+cursor.getLong(timeIndex);
     * entry[3] = ""+cursor.getInt(valueIndex); entry[5] = ""+lineColor;
     * outArray.add(entry); } cursor.close(); } } //Sort this mess out
     * Collections.sort(outArray, new Comparator<String[]>() {
     * @Override public int compare(String[] entry1, String[] entry2) { // Sort
     * by date int result = 0; if(Long.parseLong(entry1[2]) >
     * Long.parseLong(entry2[2])) result = 1; else result = -1; return result; }
     * }); return outArray; }
     */

    private void SaveScreenshot()
    {
        if (SdIsPresent())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "MoodTracker_";
            fileName += dateFormat.format(new Date(System.currentTimeMillis()));
            fileName += ".png";

            File dest = new File(Global.EXPORT_DIR, fileName);

            Bitmap bitmap = (Bitmap) dateChart.getScreenShot();
            // Log.v("screenshot", "" + bitmap);
            try {
                FileOutputStream out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Chart saved to SD Card", Toast.LENGTH_SHORT).show();

                Uri uri = Uri.fromFile(dest);
                Intent i = new Intent(Intent.ACTION_SEND);
                // i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {
                // "stephen.ody@tee2.org" });
                i.putExtra(Intent.EXTRA_SUBJECT, "My T2 Mood Tracker Screenshot");
                i.putExtra(Intent.EXTRA_TEXT, "Screenshot included as attachment.");
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, "Send Screenshot via email"));
            } catch (Exception e) {
                // Log.v("save", e.toString());
                Toast.makeText(this, "Failed to save Chart", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Unable to save, please check your SD Card", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected String getSettingSuffix() {
        return "group";
    }

    protected double getValue(KeyItem item, double value) {
        if (item.reverseData) {
            return value;
        }
        return 100 - value;
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
        ArrayList<KeyItem> items = new ArrayList<KeyItem>();
        List<Long> hiddenGids = SharedPref.getHiddenGroups(sharedPref);

        Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
        while (cursor.moveToNext()) {
            Group group = new Group(dbAdapter);
            group.load(cursor);

            if (!hiddenGids.contains(group._id)) {
                KeyItem item = new KeyItem(
                        group._id,
                        group.title,
                        null
                        );
                item.reverseData = group.inverseResults;

                if (SharedPref.getKeyColor(sharedPref, "grp" + item.id) != 0)
                    item.color = SharedPref.getKeyColor(sharedPref, "grp" + item.id);
                else
                {
                    item.color = getKeyColor(cursor.getPosition(), cursor.getCount());
                    SharedPref.setKeyColor(sharedPref, "grp" + item.id, item.color);
                }

                if (SharedPref.getKeyResource(sharedPref, "grp" + item.id) != 0)
                    item.resID = SharedPref.getKeyResource(sharedPref, "grp" + item.id);
                else
                    item.resID = R.drawable.fivestar;

                items.add(item);
            }
        }
        cursor.close();

        return items;
    }

    protected int getKeyItemViewType() {
        return KeyItemAdapter.VIEW_TYPE_ONE_LINE;
    }

    protected void onKeysItemClicked(KeyItem keyItem, View view, int pos,
            long id) {
        Intent i = new Intent(this, ScaleResultsActivity.class);
        i.putExtra(ScaleResultsActivity.EXTRA_GROUP_ID, keyItem.id);
        // i.putExtra(ScaleResultsActivity.EXTRA_BACK_BUTTON_TEXT,
        // getString(R.string.back_button));
        // i.putExtra(ScaleResultsActivity.EXTRA_TIME_START,
        // this.startCal.getTimeInMillis());
        // i.putExtra(ScaleResultsActivity.EXTRA_CALENDAR_FIELD,
        // this.calendarField);
        Analytics.onEvent(this, "Sub Graph Viewed, " + keyItem.title1);
        this.startActivity(i);
    }

    protected DataProvider getDataProvider() {
        return new GroupResultsDataProvider(this.dbAdapter);
    }

    protected boolean isKeyItemsClickable() {
        return true;
    }

    protected String getKeyTabText() {
        return getString(R.string.groups_tab);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        selectedDuration = arg2;

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public class RegressionItem
    {
        public double xValue = 0;
        public double yValue = 0;

        RegressionItem(double xv, double yv)
        {
            xValue = xv;
            yValue = yv;
        }

    }

    public class RegressionResult
    {
        public double slope = 0;
        public double intercept = 0;
        public double correlation = 0;
    }
}
