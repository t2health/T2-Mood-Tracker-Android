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
package com.t2.vas.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.t2.vas.Global;
import com.t2.vas.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

public class BackupRestore 
{

	public static final String TAG = BackupRestore.class.getName();

	/** Directory that files are to be read from and written to **/
	protected static final File DATABASE_DIRECTORY =
			new File(Environment.getExternalStorageDirectory(),"T2MoodTracker");

	/** File path of Db to be imported **/
	protected static final File IMPORT_FILE =
			new File(DATABASE_DIRECTORY,"T2MoodTracker.db");

	public static final String PACKAGE_NAME = "com.t2.vas";
	public static final String DATABASE_NAME = Global.Database.name;

	/** Contains: /data/data/com.example.app/databases/example.db **/
	private static final File DATA_DIRECTORY_DATABASE =
			new File(Environment.getDataDirectory() +
					"/data/" + PACKAGE_NAME +
					"/databases/" + DATABASE_NAME );

	/** Saves the application database to the
	 * export directory **/
	public static  boolean backupDb(Context ctx)
	{
		if( ! SdIsPresent() ) return false;

		File dbFile = DATA_DIRECTORY_DATABASE;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String filename = "MoodTracker_Backup_";
		filename += dateFormat.format(new Date(System.currentTimeMillis()));
		filename += ".db";

		File exportDir = DATABASE_DIRECTORY;
		File file = new File(exportDir, filename);

		if (!exportDir.exists()) 
		{
			exportDir.mkdirs();
		}

		try {
			file.createNewFile();
			copyFile(dbFile, file);
			// Show a message dialog
			new AlertDialog.Builder(ctx)
			.setTitle("Backup Completed")
			.setMessage("Backup saved to SDCard as: " + file.getName())
			.setCancelable(true)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			return true;
		} catch (IOException e) 
		{
			// Show an error dialog
			new AlertDialog.Builder(null)
			.setTitle("Error")
			.setMessage("Failed to backup database. Check your SDCard!")
			.setCancelable(true)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			return false;
		}
	}

	/** Replaces current database with the IMPORT_FILE if
	 * import database is valid and of the correct type **/
	public static boolean restoreDb(Context ctx, File importFile)
	{
		if( ! SdIsPresent() )
			{
			// Show a message dialog
						new AlertDialog.Builder(ctx)
						.setTitle("Restore Database")
						.setMessage("Unable to restore database. Check your SDCard!")
						.setCancelable(true)
						.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create()
						.show();
				return false;
			}

		File exportFile = DATA_DIRECTORY_DATABASE;
		//File importFile = IMPORT_FILE;


		if (!importFile.exists()) 
		{
			// Show a message dialog
			new AlertDialog.Builder(ctx)
			.setTitle("Restore Database")
			.setMessage("Unable to restore database. Check your SDCard!")
			.setCancelable(true)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			return false;
		}

		try 
		{
			exportFile.createNewFile();
			copyFile(importFile, exportFile);
			// Show a message dialog
			new AlertDialog.Builder(ctx)
			.setTitle("Restore Database")
			.setMessage("Restore completed")
			.setCancelable(true)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			return true;
		} 
		catch (IOException e) 
		{
			// Show a message dialog
			new AlertDialog.Builder(ctx)
			.setTitle("Restore Database")
			.setMessage("Unable to restore database. Check your SDCard!")
			.setCancelable(true)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create()
			.show();
			return false;
		}
	}

	private static void copyFile(File src, File dst) throws IOException 
	{
		@SuppressWarnings("resource")
		FileChannel inChannel = new FileInputStream(src).getChannel();
		@SuppressWarnings("resource")
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try 
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} 
		finally 
		{
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/** Returns whether an SD card is present and writable **/
	public static boolean SdIsPresent() 
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}