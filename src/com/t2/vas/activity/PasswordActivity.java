package com.t2.vas.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.t2.vas.R;

public class PasswordActivity extends Activity implements android.view.View.OnClickListener {
	private static final String TAG = PasswordActivity.class.getName();

	public static final int MODE_UNLOCK = 1;
	public static final int MODE_UPDATE = 2;
	public static final int MODE_SET = 3;
	private static int CURRENT_MODE;

	private static final int INTERFACE_CONFIRM_PASSWORD = 10;
	private static final int INTERFACE_NEW_PASSWORD1 = 11;
	private static final int INTERFACE_NEW_PASSWORD2 = 12;
	private static int CURRENT_INTERFACE;

	private static String CURRENT_PASSWORD;

	private static View viewConfirmPassword;
	private static View viewSetPassword1;
	private static View viewSetPassword2;

	private LayoutInflater inflater;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.password_activity);

        Intent intent = this.getIntent();
        CURRENT_MODE = intent.getIntExtra("mode", -1);
        CURRENT_PASSWORD = intent.getStringExtra("current_password");

        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewConfirmPassword = inflater.inflate(R.layout.password_widget, null);
        viewSetPassword1 = inflater.inflate(R.layout.password_widget, null);
        viewSetPassword2 = inflater.inflate(R.layout.password_widget, null);

        this.findViewById(R.id.positiveButton).setOnClickListener(this);
        this.findViewById(R.id.negativeButton).setOnClickListener(this);
        
        /*viewConfirmPassword.findViewById(R.id.positiveButton).setOnClickListener(this);
        viewConfirmPassword.findViewById(R.id.negativeButton).setOnClickListener(this);

        viewSetPassword1.findViewById(R.id.positiveButton).setOnClickListener(this);
        viewSetPassword1.findViewById(R.id.negativeButton).setOnClickListener(this);
        viewSetPassword2.findViewById(R.id.positiveButton).setOnClickListener(this);
        viewSetPassword2.findViewById(R.id.negativeButton).setOnClickListener(this);*/

        if(CURRENT_MODE < 0) {
        	this.finish();
        	return;
        }

        if(CURRENT_MODE == MODE_UPDATE && (CURRENT_PASSWORD == null || CURRENT_PASSWORD.length() <= 0)) {
        	CURRENT_MODE = MODE_SET;
        }

        switch(CURRENT_MODE) {
        	case MODE_UNLOCK:
        		this.showInterface(INTERFACE_CONFIRM_PASSWORD);
        		break;

        	case MODE_UPDATE:
    			this.showInterface(INTERFACE_CONFIRM_PASSWORD);
        		break;

        	case MODE_SET:
        		this.showInterface(INTERFACE_NEW_PASSWORD1);
        		break;
        }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.negativeButton:
				this.setResult(Activity.RESULT_CANCELED, this.getIntent());
				this.finish();
				return;
			case R.id.positiveButton:
				this.onClickPositiveButton(v);
				break;
		}
	}

	private static boolean passwordsMatch(String currentPassword, String newPassword) {
		return currentPassword.equals(newPassword);
	}

	private void onClickPositiveButton(View v) {
		String viewConfirmPasswordString = ((EditText)viewConfirmPassword.findViewById(R.id.password)).getText().toString();
		String viewEnterPassword1String = ((EditText)viewSetPassword1.findViewById(R.id.password)).getText().toString();
		String viewEnterPassword2String = ((EditText)viewSetPassword2.findViewById(R.id.password)).getText().toString();

		switch(CURRENT_INTERFACE) {
			case INTERFACE_CONFIRM_PASSWORD:

				// Passwords match, end activity with ok.
				if(CURRENT_MODE == MODE_UNLOCK && passwordsMatch(CURRENT_PASSWORD, viewConfirmPasswordString)) {
					this.setResult(Activity.RESULT_OK);
					this.finish();
					return;

				// Passwords do not match, show confirm dialog again.
				} else if(CURRENT_MODE == MODE_UNLOCK) {
					this.showInterface(INTERFACE_CONFIRM_PASSWORD);
					((TextView)viewConfirmPassword.findViewById(R.id.text1)).setText(R.string.password_confirm_message_error);
					return;

				// Passwords match, bring up password1 dialog.
				} else if(CURRENT_MODE == MODE_UPDATE && passwordsMatch(CURRENT_PASSWORD, viewConfirmPasswordString)) {
					this.showInterface(INTERFACE_NEW_PASSWORD1);
					return;

				// Passwords do not match, show confirm dialog again.
				} else if(CURRENT_MODE == MODE_UPDATE) {
					this.showInterface(INTERFACE_CONFIRM_PASSWORD);
					return;
				}

				break;

			case INTERFACE_NEW_PASSWORD1:
				// Show password2 dialog if password1 contains text.
				if(viewEnterPassword1String.trim().length() > 0) {
					this.showInterface(INTERFACE_NEW_PASSWORD2);
					return;

				// Show password1 dialog again.
				} else {
					this.showInterface(INTERFACE_NEW_PASSWORD1);
					return;
				}

			case INTERFACE_NEW_PASSWORD2:
				// Passwords match, return result ok and the new password.
				if(passwordsMatch(viewEnterPassword1String, viewEnterPassword2String)) {
					Log.v(TAG, "MATCH");
					this.getIntent().putExtra("new_password", viewEnterPassword1String);
					this.setResult(Activity.RESULT_OK, this.getIntent());
					this.finish();
					return;

				// Passwords do not match, show password1 dialog.
				} else {
					this.showInterface(INTERFACE_NEW_PASSWORD1);
					((TextView)viewSetPassword1.findViewById(R.id.text1)).setText(R.string.password_new_message1_error);
					return;
				}
		}
	}


	private void showInterface(int id) {
		View vi = this.getInterface(id);
		if(vi == null) {
			return;
		}

		CURRENT_INTERFACE = id;

		switch(id) {
			case INTERFACE_CONFIRM_PASSWORD:
				((TextView)vi.findViewById(R.id.text1)).setText(R.string.password_confirm_message);
				((Button)this.findViewById(R.id.positiveButton)).setText(R.string.password_unlock);
				this.setWrapperView(vi);
				break;

			case INTERFACE_NEW_PASSWORD1:
				((TextView)vi.findViewById(R.id.text1)).setText(R.string.password_new_message1);
				((Button)this.findViewById(R.id.positiveButton)).setText(R.string.password_next);
				this.setWrapperView(vi);
				break;

			case INTERFACE_NEW_PASSWORD2:
				((TextView)vi.findViewById(R.id.text1)).setText(R.string.password_new_message2);
				((Button)this.findViewById(R.id.positiveButton)).setText(R.string.password_set);
				this.setWrapperView(vi);
				break;
		}

		((TextView)vi.findViewById(R.id.text1)).requestFocus();
	}

	private View getInterface(int id) {
		switch(id) {
			case INTERFACE_CONFIRM_PASSWORD:
				return viewConfirmPassword;

			case INTERFACE_NEW_PASSWORD1:
				return viewSetPassword1;

			case INTERFACE_NEW_PASSWORD2:
				return viewSetPassword2;
		}
		return null;
	}
	
	private void setWrapperView(View v) {
		ViewGroup ww = (ViewGroup)this.findViewById(R.id.widgetWrapper);
		ww.removeAllViews();
		ww.addView(v);
	}
}
