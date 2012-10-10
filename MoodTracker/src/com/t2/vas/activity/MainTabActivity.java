/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */

package com.t2.vas.activity;

import com.t2.vas.Analytics;
import com.t2.vas.AppSecurityManager;
import com.t2.vas.Eula;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.editor.GroupActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class MainTabActivity extends ABSNavigationActivity implements OnItemClickListener {

    public static final int TabMenu1 = 1111;
    public static final int TabMenu2 = 1112;
    public static final int TabMenu3 = 1113;
    public static final int TabMenu4 = 1114;
    public static final int TabMenu5 = 1115;


	public static final int NOTE_ACTIVITY = 355;

	
    private static final int DIALOG_STATISTICS = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show the eula if it hasn't been shown
        Eula.show(this);

        @SuppressWarnings("deprecation")
        TabHost tabHost = getTabHost();
        tabHost.getTabWidget().setStripEnabled(false);
        TabSpec tabspec = null;

        // Rate Tab
        tabspec = tabHost.newTabSpec("Rate");
        tabspec.setIndicator("Rate", getResources().getDrawable(R.drawable.tabrate));
        Intent rateIntent = new Intent(this, MainRateActivity.class);
        tabspec.setContent(rateIntent);
        tabHost.addTab(tabspec);

        // Results Tab
        tabspec = tabHost.newTabSpec("Results");
        tabspec.setIndicator("Results", getResources().getDrawable(R.drawable.tabresults));
        Intent resultsIntent = new Intent(this, MainResultsActivity.class);
        tabspec.setContent(resultsIntent);
        tabHost.addTab(tabspec);

        // Support Tab
        tabspec = tabHost.newTabSpec("Support");
        tabspec.setIndicator("Support", getResources().getDrawable(R.drawable.tabsupport));
        Intent supportIntent = new Intent(this, MainSupportActivity.class);
        tabspec.setContent(supportIntent);
        tabHost.addTab(tabspec);

        // Settings Tab
        tabspec = tabHost.newTabSpec("Settings");
        tabspec.setIndicator("Settings", getResources().getDrawable(R.drawable.tabsettings));
        Intent settingsIntent = new Intent(this, MainSettingsActivity.class);
        tabspec.setContent(settingsIntent);
        tabHost.addTab(tabspec);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if this is the tab activity so that security screen doesn't
        // open twice (one for tabactivity, and one for child activity)
        // if (this.getParent() instanceof MainTabActivity)
        AppSecurityManager.getInstance().onResume(this, SharedPref.Security.isEnabled(sharedPref));
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_STATISTICS:
                ProgressDialog dlg = new ProgressDialog(this);
                dlg.setIndeterminate(true);
                dlg.setTitle("Send Log");
                dlg.setMessage("Please wait, generating log file...");
                return dlg;
        }
        return super.onCreateDialog(id);

    }

    public void populateTabMenu(Menu menu) {

        menu.setQwertyMode(true);

        MenuItem item1 = menu.add(0, TabMenu1, 0, R.string.add_group_title);
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_add);
		}
		MenuItem item2 = menu.add(0, TabMenu2, 0, "Add Note");
		{
			item2.setIcon(android.R.drawable.ic_menu_agenda);
		}
        MenuItem item3 = menu.add(0, TabMenu3, 0, "Help");
        {
            item3.setIcon(android.R.drawable.ic_menu_help);
        }
        MenuItem item4 = menu.add(0, TabMenu4, 0, "Logout & Exit");
        {
            item4.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        }
        MenuItem item5 = menu.add(0, TabMenu5, 0, "Send Study Log");
        {
            item5.setIcon(android.R.drawable.stat_sys_upload);
        }
    }

    public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case TabMenu1:
			Intent a = new Intent(this, GroupActivity.class);
			a.putExtra(GroupActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			a.putExtra(GroupActivity.EXTRA_GROUP_ID, 0L);
			this.startActivityForResult(a, 123);
			break;
		case TabMenu2:
			Intent n = new Intent(this, AddEditNoteActivity.class);
			//n.putExtra(AddEditNoteActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivityForResult(n, NOTE_ACTIVITY);
			break;
		case TabMenu3:
			Intent i;
			i = new Intent(this, HelpActivity.class);
			i.putExtra(HelpActivity.EXTRA_TARGET, this.getHelpTarget());
			//i.putExtra(HelpActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			break;
		case TabMenu4:
			AppSecurityManager.getInstance().setIsUnlocked(false);
			this.finish();
			break;
		case TabMenu5:
            showDialog(DIALOG_STATISTICS);
            new AsyncTask<Void, Void, Uri>() {
                @Override
                protected Uri doInBackground(Void... params) {
                    return Analytics.generateStatisticsCsv(MainTabActivity.this);
                }

                @Override
                protected void onPostExecute(Uri result) {
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL,
                            new String[] {
                                sharedPref.getString(getString(R.string.prf_study_recipient_email), null)
                            });
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mood Tracker Log File");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                            "Attached is a Mood Tracker log file.");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, result);
                    dismissDialog(DIALOG_STATISTICS);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            }.execute((Void) null);
		}
		return false;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateTabMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(TabMenu5).setVisible(
                !TextUtils.isEmpty(sharedPref.getString(
                        getString(R.string.prf_study_participant_number), null)));
        return super.onPrepareOptionsMenu(menu);
    }

    /** when menu button option selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return applyMenuChoice(item) || super.onOptionsItemSelected(item);
    }

}
