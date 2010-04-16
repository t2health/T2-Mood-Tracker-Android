package com.t2.vas.activity;

import com.t2.vas.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = new Intent(this, FormActivity.class);
        //Intent i = new Intent(this, GroupPreference.class);
        this.startActivity(i);
        this.finish();
	}
}
