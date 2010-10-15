package com.t2.vas;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.t2.chart.Label;
import com.t2.chart.SeriesAdapterData;
import com.t2.chart.Value;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DataExport {
	public static final void share(Context context, long groupId) {
		Uri exportData = exportData(context, groupId);
		
		if(exportData == null) {
			return;
		}
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/csv");
		shareIntent.putExtra(Intent.EXTRA_STREAM, exportData);
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Global.SHARE_SUBJECT);
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Global.SHARE_MESSAGE);

		context.startActivity(Intent.createChooser(shareIntent, "Title for chooser"));
	}
	
	private static final Uri exportData(Context context, long groupId) {
		DBAdapter dbAdapter = new DBAdapter(context.getApplicationContext(), Global.Database.name, Global.Database.version);
        dbAdapter.open();
		
		Group currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
		currentGroup._id = groupId;
		currentGroup.load();
		
		long[] range = currentGroup.getResultsTimestampRange();
		
		if(range.length == 0) {
			return null;
		}
		
		try {
			FileWriter fw = new FileWriter(Global.EXPORT_OUTPUT_FILE, false);
			Scale currentScale = null;
			ArrayList<Scale> scales = currentGroup.getScales();
			for(int i = 0; i < scales.size(); i++) {
				StringBuffer sb = new StringBuffer();
				currentScale = scales.get(i);
				
				
				ScaleResultsSeriesDataAdapter srsda = new ScaleResultsSeriesDataAdapter(
						dbAdapter, 
						range[0], 
						currentScale._id, 
						ScaleResultsSeriesDataAdapter.GROUPBY_DAY, 
						Global.CHART_LABEL_DATE_FORMAT
				);
				
				SeriesAdapterData data = srsda.getData();
				
				if(i == 0) {
					sb.append("\""+ currentGroup.title.replace("\"", "\"\"") +"\",");
					
					ArrayList<Label> labels = data.getLabels();
					int size = labels.size();
					for(int j = 0; j < size; j++) {
						String val = labels.get(j).getLabelValue().toString().replace("\"", "\"\"");
						sb.append("\""+ val +"\"");
						
						if(j < size-1) {
							sb.append(",");
						}
					}
					sb.append("\n");
				}
				
				sb.append("\""+ currentScale.max_label.replace("\"", "\"\"") +"\n"+ currentScale.min_label.replace("\"", "\"\"") +"\",");
				ArrayList<Value> values = data.getValues();
				int size = values.size();
				for(int j = 0; j < size; j++) {
					Value value = values.get(j);
					String val = "";
					if(value.getValue() != null) {
						val = value.getValue().toString().replace("\"", "\"\"");
					}
					sb.append("\""+ val +"\"");
					
					if(j < size-1) {
						sb.append(",");
					}
				}
				sb.append("\n");
				
				fw.write(sb.toString());
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			dbAdapter.close();
			return null;
		}
		
		dbAdapter.close();
		return Uri.fromFile(Global.EXPORT_OUTPUT_FILE);
	}
}
