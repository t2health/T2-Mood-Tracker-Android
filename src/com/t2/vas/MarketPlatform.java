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
package com.t2.vas;
//Based on method described here:
//http://stackoverflow.com/questions/7683130/how-to-support-amazon-and-android-market-links-in-same-apk

//Basic idea is that Amazon re-signs your uploaded apk files, changing the hashcode from the original. So you compare the running hashcode 
//against the stored original. 
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;

public class MarketPlatform {

	public static int hashcode = -1388539573;

	//Use this to determine your SIGNED app hashcode. Then store it literal in the var above
	public static void printHashcode(Context context)
	{
		try 
		{
			Signature[] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

			for (Signature sig : sigs)
			{
				Log.v("Info:", "hashcode: " + sig.hashCode());
			}
		} 
		catch (NameNotFoundException e)	{}
	}

	public static boolean isGoogleMarket(Context context){
		boolean isGoogleMarket = false;
		int currentSig = 1; 
		try 
		{
			Signature[] sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
			for (Signature sig : sigs)
			{
				currentSig = sig.hashCode();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (currentSig==hashcode){
			isGoogleMarket = true;
		} else {
			isGoogleMarket = false;
		}

		return isGoogleMarket;
	}
}
