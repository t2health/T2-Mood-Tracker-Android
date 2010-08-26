package com.t2.vas.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;

public class AboutActivity extends ABSInfoActivity {
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       this.setContentResId(R.string.about_text);
       VASAnalytics.onEvent(VASAnalytics.EVENT_ABOUT_ACTIVITY);
	}
}
