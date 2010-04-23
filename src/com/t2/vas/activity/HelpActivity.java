package com.t2.vas.activity;

import com.t2.vas.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = this.getIntent();
        
        int messageId = intent.getIntExtra("string_resource_id", 0);
        if(messageId == 0) {
        	this.finish();
        	return;
        }
        
        String message = this.getString(messageId);
        if(message == null) {
        	this.finish();
        	return;
        }
        
        this.setContentView(R.layout.help_activity);
        
        ((TextView)this.findViewById(R.id.message)).setText(message);
        //((Button)this.findViewById(R.id.closeButton)).setOnClickListener(this);
	}

	/*@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.closeButton:
				this.finish();
				break;
		}
	}*/
}
