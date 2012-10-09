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
