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
