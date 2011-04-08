package com.t2.vas.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public class ForgotPinActivity extends ABSCustomTitleActivity implements OnKeyListener, OnEditorActionListener, OnClickListener {
	private EditText answer1EditText;
	private EditText answer2EditText;
	private String answer1;
	private String answer2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.forgot_pin_activity);
		
		((TextView)this.findViewById(R.id.question1)).setText(
				SharedPref.Security.getQuestion1(sharedPref)
		);
		((TextView)this.findViewById(R.id.question2)).setText(
				SharedPref.Security.getQuestion2(sharedPref)
		);
		
		answer1 = SharedPref.Security.getAnswer1(sharedPref);
		answer2 = SharedPref.Security.getAnswer2(sharedPref);
		
		answer1EditText = (EditText)this.findViewById(R.id.answer1);
		answer1EditText.setOnKeyListener(this);
		answer1EditText.setOnEditorActionListener(this);
		
		answer2EditText = (EditText)this.findViewById(R.id.answer2);
		answer2EditText.setOnKeyListener(this);
		answer1EditText.setOnEditorActionListener(this);
		
		this.findViewById(R.id.cancelButton).setOnClickListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		checkForMatchedAnswers(v);
		return false;
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Log.v(TAG, "ACTION");
		checkForMatchedAnswers(v);
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.cancelButton:
				this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private void checkForMatchedAnswers(View v) {
		int vId = v.getId();
		
		boolean match = false;
		if(vId == R.id.answer1) {
			match = responsesMatch(
					answer1EditText.getText().toString(),
					answer1
			);
			
		} else if(vId == R.id.answer2) {
			match = responsesMatch(
					answer2EditText.getText().toString(),
					answer2
			);
		}
		
		if(match) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
	
	private boolean responsesMatch(String text1, String text2) {
		String text1New = text1.trim().toLowerCase().replaceAll("[\\s\\._-]", "");
		String text2New = text2.trim().toLowerCase().replaceAll("[\\s\\._-]", "");
		
		if(text1New.equals(text2New)) {
			return true;
		}
		return false;
	}
}
