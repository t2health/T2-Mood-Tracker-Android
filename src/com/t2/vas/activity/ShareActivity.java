package com.t2.vas.activity;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.t2.vas.R;

public class ShareActivity extends ABSExportActivity implements OnClickListener, OnItemClickListener {
	private ArrayList<Uri> files;
	
	@Override
	protected String getFinishButtonText() {
		return getString(R.string.share_button);
	}
	
	@Override
	protected String getProgressMessage() {
		return getString(R.string.share_progress_message);
	}
	
	@Override
	protected String getExportFilename(long fromTime, long toTime) {
		return "t2mt-data.csv";
	}
	
	@Override
	protected void onDataExported(ArrayList<Uri> uris) {
		this.files = uris;
		
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		shareIntent.setType("text/csv");
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(
				Intent.createChooser(shareIntent, getString(R.string.share_data_title))
		);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Delete the export files, they are no longer needed.
		if(this.files != null) {
			for(int i = 0; i < this.files.size(); ++i) {
				try {
					File file = new File(new URI(this.files.get(i).toString()));
					if(file.exists()) {
						file.delete();
					}
				} catch (URISyntaxException e) {
					//e.printStackTrace();
				}
			}
		}
	}
}
