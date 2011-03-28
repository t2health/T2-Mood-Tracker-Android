package com.t2.vas;


public class Global {
	public static class Database {
		public static final String name = "VAS_DATA";
		public static final int version = 3;
		public static final boolean CREATE_FAKE_DATA = false;
	}

	public static final boolean UNLOCK_HIDDEN_FEATURES = false;
	public static final boolean DEV_MODE = false;
	public static final String FLURRY_KEY = "AI6NAUCMM6QCZYHLV9B4";
	public static final String ANALYTICS_KEY = FLURRY_KEY;
	public static final String NOTES_LONG_DATE_FORMAT = "MMM d, yyyy h:mm a";
	public static final String NOTES_SECTION_DATE_FORMAT = "MMM, yyyy";
	public static final String REMINDER_TIME_FORMAT = "h:mm a";
	public static final String SHARE_TIME_FORMAT = "MMM d, yyyy";
	public static final String EXPORT_TIME_FORMAT = "yyyy-mm-dd hh:mm:ss";
	public static final String REMOTE_STACK_TRACE_URL = "http://www2.tee2.org/trace/report.php";
}
