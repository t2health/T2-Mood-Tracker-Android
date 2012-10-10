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


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;

import com.t2.vas.R;

public class WebViewActivity extends ABSActivity {
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_CONTENT = "content";
	
	public static final String EXTRA_TITLE_ID = "titleId";
	public static final String EXTRA_CONTENT_ID = "contentId";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		
		int titleId = intent.getIntExtra(EXTRA_TITLE_ID, -1);
		int contentId = intent.getIntExtra(EXTRA_CONTENT_ID, -1);
		
		String titleString = intent.getStringExtra(EXTRA_TITLE);
		String contentString = intent.getStringExtra(EXTRA_CONTENT);
		
		if(titleString == null && titleId == -1) {
			this.finish();
		}
		if(contentString == null && contentId == -1) {
			this.finish();
		}
		
		if(titleId != -1) {
			titleString = getString(titleId);
		}
		if(contentId != -1) {
			contentString = getString(contentId);
		}
		
		if(titleString == null || contentString == null) {
			this.finish();
		}
		
		this.setTitle(titleString);
		this.setContentView(R.layout.webview_layout);
		
		WebView wv = (WebView)this.findViewById(R.id.webview);
		wv.setBackgroundColor(Color.BLACK); // make the bg transparent
		wv.loadDataWithBaseURL("fake:/blah", contentString, "text/html", "utf-8", null);
	}

}
