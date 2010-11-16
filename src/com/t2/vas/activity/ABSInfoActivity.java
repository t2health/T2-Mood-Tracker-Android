package com.t2.vas.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.t2.vas.R;

public class ABSInfoActivity extends ABSActivity {
	private static final String TAG = ABSInfoActivity.class.getName();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.abs_info_activity);
        this.findViewById(R.id.closeButton).setOnClickListener(this);
	}

	public void setContentResId(int id) {
		String content = this.getString(id);

		((TextView)this.findViewById(R.id.content)).setText(content);
		/*
        content = "<html><body style=\"background-color: transparent;\">"+ content +"</body></html>";
Log.v(TAG, "SET CONTENT:"+content);
        WebView wv = (WebView)this.findViewById(R.id.content);
        wv.loadData(content, "text/html", "utf-8");*/
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.closeButton:
				this.setResult(Activity.RESULT_OK);
				this.finish();
				return;
		}
		super.onClick(v);
	}
}
