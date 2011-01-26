package com.t2.vas.activity;

import com.t2.vas.R;

import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends ABSNavigation {

	public static final String EXTRA_TARGET = "target";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String targetString = getIntent().getStringExtra(EXTRA_TARGET);
		if(targetString == null) {
			targetString = "";
		}
		targetString = targetString.trim();
		if(targetString.length() > 0) {
			targetString = "#"+targetString;
		}
		
		this.setContentView(R.layout.webview_layout);
		
		WebView wv = (WebView)this.findViewById(R.id.webview);
		wv.setBackgroundColor(0); // make the bg transparent
		wv.loadUrl("file:///android_asset/help.html"+ targetString);
	}

}
