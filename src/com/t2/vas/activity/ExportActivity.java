package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.widget.Toast;

import com.t2.vas.R;

public class ExportActivity extends ABSExportActivity {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	
	@Override
	protected void onDataExported(ArrayList<Uri> uris) {
		if(uris.size() > 0) {
			Toast.makeText(this, R.string.files_exported, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.fail_to_export_files, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected String getExportFilename(long fromTime, long toTime) {
		String fileName = "data_";
		fileName += dateFormat.format(new Date(System.currentTimeMillis()));
		fileName += ".csv";
		return fileName;
	}

	@Override
	protected String getProgressMessage() {
		return getString(R.string.export_progress_message);
	}

}
