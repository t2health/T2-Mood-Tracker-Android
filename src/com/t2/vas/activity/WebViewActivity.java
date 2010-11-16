package com.t2.vas.activity;


import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.t2.vas.R;

public class WebViewActivity extends CustomTitle {
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
		this.setContentView(R.layout.webview_activity);
		
		WebView wv = (WebView)this.findViewById(R.id.webview);
		wv.loadDataWithBaseURL("fake:/blah", contentString, "text/html", "utf-8", null);
	}

}
