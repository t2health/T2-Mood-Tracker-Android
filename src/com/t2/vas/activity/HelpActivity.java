package com.t2.vas.activity;

import com.t2.vas.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = this.getIntent();
        
        String message = intent.getStringExtra("message");
        if(message == null) {
        	this.finish();
        	return;
        }
        
        this.setContentView(R.layout.help_activity);
        
        ((TextView)this.findViewById(R.id.message)).setText(message);
	}
}
