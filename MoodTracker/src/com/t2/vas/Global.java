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
package com.t2.vas;

import java.io.File;

import android.os.Environment;


public class Global {
	public static class Database {
		public static final String name = "VAS_DATA";
		public static final int version = 6;
		public static final boolean CREATE_FAKE_DATA = false;
		public static final int CREATE_FAKE_AMOUNT = 0;
	}

	public static final boolean DEV_MODE = false;
	public static final String FLURRY_KEY = "AI6NAUCMM6QCZYHLV9B4";
	public static final String NOTES_LONG_DATE_FORMAT = "MMM d, yyyy h:mm a";
	public static final String NOTES_SECTION_DATE_FORMAT = "MMM, yyyy";
	public static final String REMINDER_TIME_FORMAT = "h:mm a";
	public static final String SHARE_TIME_FORMAT = "MMM d, yyyy";
	public static final String EXPORT_TIME_FORMAT = "yyyy-mm-dd hh:mm:ss";
	public static final String REMOTE_STACK_TRACE_URL = "http://www2.tee2.org/trace/report.php";

	public static final File EXPORT_DIR = new File(Environment.getExternalStorageDirectory(), "T2MoodTracker");
	public static int tabPage = 0;
}
