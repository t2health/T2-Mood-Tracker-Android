package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.t2.vas.Global;
import com.t2.vas.NotesAdapter;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.db.tables.Scale.ResultValues;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.NoteLayout;
import com.t2.vas.view.chart.LineSeries;
import com.t2.vas.view.chart.Series;
import com.t2.vas.view.chart.SeriesDrawableListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class NotesActivity extends BaseActivity implements OnClickListener, android.content.DialogInterface.OnClickListener, OnScrollListener, SeriesDrawableListener, OnItemClickListener {
	private static final String TAG = NotesActivity.class.getName();
	private long activityScaleId;
	
	private LayoutInflater layoutInflater;
	private ArrayList<Note> notesList = new ArrayList<Note>();
	private NotesAdapter notesAdapter;
	
	private DBAdapter dbAdapter;
	private ListView notesListView;
	//private GroupsResultValue scaleValues;
	private ResultValues scaleValues;
	
	private LineSeries chartLineSeries;
	
	private int CURRENT_DIALOG;
	private static final int ADD_NOTE_DIALOG = 10;
	private static final int VIEW_NOTE_DIALOG = 11;
	private static final int DELETE_NOTE_DIALOG = 12;
	
	private static final String NOTE_DATE_FORMAT = "EEEE MMMM, d yyyy";
	
	private Scale activityScaleObject;
	private Note currentlySelectedNote;
	
	private LinearLayout contentView;
	private NoteLayout dialogViewNoteLayout;
	private NoteLayout dialogAddNoteLayout;
	
	private ChartLayout chartLayout;
	private int resultsGroupBy;
	private int chartLabelsColor;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// init the local variables;
		Intent intent;
		
		
		// Init global main variables.
		layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogAddNoteLayout = (NoteLayout)layoutInflater.inflate(R.layout.add_note_layout, null);
		dialogViewNoteLayout = (NoteLayout)layoutInflater.inflate(R.layout.view_note_layout, null);
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		
		dialogAddNoteLayout.setDateFormat(NOTE_DATE_FORMAT);
		dialogViewNoteLayout.setDateFormat(NOTE_DATE_FORMAT);
		
		// This activity requires a valid scale id be set to run.
		intent = this.getIntent();
		this.activityScaleId = intent.getLongExtra("scale_id", 0);
		if(this.activityScaleId <= 0) {
			this.finish();
		}
		
		// Get the method in which results should be grouped.
		if(intent.getIntExtra("resultsGroupBy", 0) != 0) {
			this.resultsGroupBy = intent.getIntExtra("resultsGroupBy", 0);
		} else {
			this.finish();
		}
		
		// Get the chart label colors
		if(intent.getIntExtra("chartLabelsColor", 0) != 0) {
			this.chartLabelsColor = intent.getIntExtra("chartLabelsColor", 0);
		}
		
		// Init the line series.
		chartLineSeries = new LineSeries("Line Series");
		if(intent.getIntExtra("seriesFillColor", 0) != 0) {
			chartLineSeries.setFillColor(intent.getIntExtra("seriesFillColor", 0));
		}
		if(intent.getIntExtra("seriesStrokeColor", 0) != 0) {
			chartLineSeries.setStrokeColor(intent.getIntExtra("seriesStrokeColor", 0));
		}
		if(intent.getIntExtra("seriesLineFillColor", 0) != 0) {
			chartLineSeries.setLineFillColor(intent.getIntExtra("seriesLineFillColor", 0));
		}
		if(intent.getIntExtra("seriesLineStrokeColor", 0) != 0) {
			chartLineSeries.setLineStrokeColor(intent.getIntExtra("seriesLineStrokeColor", 0));
		}
		
		
		// init the content view
		contentView = (LinearLayout)layoutInflater.inflate(R.layout.notes_activity, null);
		
		// scale the object
		activityScaleObject = (Scale)dbAdapter.getTable("scale").newInstance();
        activityScaleObject._id = this.activityScaleId;
        activityScaleObject.load();
        scaleValues = activityScaleObject.getResultValues(this.resultsGroupBy, Global.CHART_LABEL_DATE_FORMAT);
        
        this.initNotesList();
        
		// Initilize the chart
    	chartLineSeries.addAllValues(scaleValues.values, scaleValues.getResultsAsObjectList());
    	chartLineSeries.addAllLabels(scaleValues.labels);
    	chartLineSeries.setOnSeriesDrawbleListener(this);
    	
		chartLayout = (ChartLayout)contentView.findViewById(R.id.chartLayout);
		chartLayout.setLabelsColor(this.chartLabelsColor);
		chartLayout.setYMaxLabel(activityScaleObject.max_label);
    	chartLayout.setYMinLabel(activityScaleObject.min_label);
    	chartLayout.getChart().addSeries("main", chartLineSeries);
		
        
        this.notesAdapter = new NotesAdapter(this, -1, notesList, NOTE_DATE_FORMAT);
        
        notesListView = ((ListView)contentView.findViewById(R.id.list));
        notesListView.setAdapter(notesAdapter);
        notesListView.setSelection(notesListView.getCount()-1);
        
        notesListView.setOnItemClickListener(this);
        notesListView.setOnScrollListener(this);
        ((Button)contentView.findViewById(R.id.addButton)).setOnClickListener(this);
        
        this.setContentView(contentView);
	}
	
	private void initNotesList() {
		notesList.clear();
		
		// load the notes on the page
        for(Note n: activityScaleObject.getNotes(Scale.ORDERBY_ASC)) {
        	notesList.add(n);
        }
        
        // Hide the no notes message if notes exist.
        if(this.notesList.size() > 0) {
        	contentView.findViewById(R.id.noNotesMessage).setVisibility(View.GONE);
        } else {
        	contentView.findViewById(R.id.noNotesMessage).setVisibility(View.VISIBLE);
        }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.addButton:
				this.dialogAddNoteLayout.setNote("");
				this.dialogAddNoteLayout.setTimestamp(Calendar.getInstance().getTimeInMillis());
				
				this.showDialog(ADD_NOTE_DIALOG);
				break;
		}
	}

	private void setDialogNoteLayoutValues(int dialogId, String note, long timestamp) {
		switch(dialogId) {
			case ADD_NOTE_DIALOG:
				this.dialogAddNoteLayout.setNote(note);
				this.dialogAddNoteLayout.setTimestamp(timestamp);
				break;
				
			case VIEW_NOTE_DIALOG:
				this.dialogViewNoteLayout.setNote(note);
				this.dialogViewNoteLayout.setTimestamp(timestamp);
				break;
		}
	}
	
	private NoteLayout getDialogNoteLayout(int dialogId) {
		switch(dialogId) {
			case ADD_NOTE_DIALOG:
				return this.dialogAddNoteLayout;
			
			case VIEW_NOTE_DIALOG:
				return this.dialogViewNoteLayout;
		}
		return null;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		this.CURRENT_DIALOG = id;
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case ADD_NOTE_DIALOG: 
				return new AlertDialog.Builder(this)
					.setTitle("Add Note")
					.setPositiveButton("Add", this)
					.setNegativeButton("Cancel", this)
					.setView(this.dialogAddNoteLayout)
					.create();
				
			case VIEW_NOTE_DIALOG:
				return new AlertDialog.Builder(this)
					.setTitle("View Note")
					.setPositiveButton("Close", this)
					.setNegativeButton("Delete", this)
					.setView(this.dialogViewNoteLayout)
					.create();
			
			case DELETE_NOTE_DIALOG:
				return new AlertDialog.Builder(this)
					.setTitle("Delete Note")
					.setPositiveButton("Yes", this)
					.setNegativeButton("No", this)
					.setMessage("Are you sure you would like to delete this note?")
					.create();
		}
		
		return super.onCreateDialog(id);
	}
	
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(this.CURRENT_DIALOG) {
			case ADD_NOTE_DIALOG:
				// Add the new note
				if(which == AlertDialog.BUTTON_POSITIVE) {
					NoteLayout nl = getDialogNoteLayout(this.CURRENT_DIALOG);
					this.dbAdapter.open();
					
					Note n = (Note)this.dbAdapter.getTable("note").newInstance();
					n.scale_id = this.activityScaleId;
					n.result_id = 0;
					n.timestamp = nl.getTimestamp();
					n.note = nl.getNote();
					n.save();
					
					this.initNotesList();
					this.notesAdapter.notifyDataSetChanged();
				}
				this.chartLayout.invalidate();
				break;
				
			case VIEW_NOTE_DIALOG:
				// Bring up the delete confirmation
				if(which == AlertDialog.BUTTON_NEGATIVE) {
					this.showDialog(DELETE_NOTE_DIALOG);
				}
				break;
				
			case DELETE_NOTE_DIALOG:
				// Delete the note.
				if(which == AlertDialog.BUTTON_POSITIVE) {
					this.dbAdapter.open();
					this.currentlySelectedNote.delete();
					this.dbAdapter.close();
					
					this.initNotesList();
					this.notesAdapter.notifyDataSetChanged();
				}
				break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		/*if(view.getCount() == 0) {
			return;
		}
		Note firstVisibleNote = (Note)view.getItemAtPosition(firstVisibleItem);
		Note lastVisibleNote = (Note)view.getItemAtPosition(firstVisibleItem+visibleItemCount-1);
		
		long firstTimestamp = 0;
		long lastTimestamp = Calendar.getInstance().getTimeInMillis();
		
		if(firstVisibleNote != null) {
			firstTimestamp = firstVisibleNote.timestamp;
		}
		if(lastVisibleNote != null) {
			lastTimestamp = lastVisibleNote.timestamp;
		}
		
		if(this.chartLineSeries.drawablesSize() <= 0) {
			return;
		}
		
		//Log.v(TAG, "=======");
		this.chartLineSeries.deselectAllDrawableValues();
		for(int i = 0; i < this.scaleValues.results.length; i++) {
			for(int j = 0; j < this.scaleValues.results[i].length; j++) {
				long resultTimestamp = this.scaleValues.results[i][j].timestamp;
				
				if(resultTimestamp >= firstTimestamp && resultTimestamp <= lastTimestamp) {
					//Log.v(TAG, "SELECT:"+i);
					this.chartLineSeries.selectDrawableValueAt(i);
				}
			}
		}
		this.chartLayout.invalidate();*/
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onSeriesDrawableClicked(Series s, int index) {
		s.deselectAllDrawableValues();
		s.getDrawableValueAt(index).setSeleted();
		
		//Result[] results = (Result[])s.getValuesMeta()[index];
		ArrayList<Result> results = (ArrayList<Result>)s.getValuesMeta().get(index);
		Result firstResult = results.get(0);
		
		this.setDialogNoteLayoutValues(ADD_NOTE_DIALOG, "", firstResult.timestamp);
		this.showDialog(ADD_NOTE_DIALOG);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.currentlySelectedNote = (Note)arg1.getTag();
		
		this.setDialogNoteLayoutValues(VIEW_NOTE_DIALOG, this.currentlySelectedNote.note, this.currentlySelectedNote.timestamp);
		this.showDialog(VIEW_NOTE_DIALOG);
	}
}
