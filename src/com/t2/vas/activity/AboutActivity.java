package com.t2.vas.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.t2.vas.R;

public class AboutActivity extends ABSInfoActivity {
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       this.setContentResId(R.string.about_text);
	}
}
