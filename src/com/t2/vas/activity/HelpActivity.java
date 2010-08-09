package com.t2.vas.activity;

import com.t2.vas.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends ABSInfoActivity implements OnClickListener {
	private static final String TAG = HelpActivity.class.getName();

	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       Intent intent = this.getIntent();

       int messageId = intent.getIntExtra("help_string_resource_id", 0);
       if(messageId == 0) {
       	this.finish();
       	return;
       }

       String content = this.getString(messageId);
       if(content == null) {
       	this.finish();
       	return;
       }

       this.setContentResId(messageId);
	}
}
