/*
 * 
 */

package com.t2.vas.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.t2.vas.R;

public class StudyEnrollmentActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        String encodedData = uri.getLastPathSegment();

        byte[] decodeBytes = Base64.decode(encodedData, Base64.DEFAULT);
        String unencodedData = new String(decodeBytes);
        String participantNumber = unencodedData.substring(0, 4);
        String recipientEmail = unencodedData.substring(4);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putString(getString(R.string.prf_study_participant_number), participantNumber)
                .putString(getString(R.string.prf_study_recipient_email), recipientEmail)
                .commit();

        Toast.makeText(this, "Study enrollment successful!", Toast.LENGTH_LONG).show();

        finish();
        
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);

    }
    
}
