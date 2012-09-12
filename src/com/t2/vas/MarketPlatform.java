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
