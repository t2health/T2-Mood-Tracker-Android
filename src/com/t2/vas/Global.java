package com.t2.vas;

public class Global {
	public static class Database {
		public static final String name = "VAS_DATA";
		public static final int version = 1;
		public static final boolean CREATE_FAKE_DATA = false;
	}

	public static final boolean DEV_MODE = false;
	public static final String FLURRY_KEY = "AI6NAUCMM6QCZYHLV9B4";
	public static final String ANALYTICS_KEY = FLURRY_KEY;
	public static final String CHART_LABEL_DATE_FORMAT = "MMM d";

	public static final String REMOTE_STACK_TRACE_URL = "http://www2.tee2.org/trace/report.php";
}
