package com.t2.vas.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Xml;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class ImportDataActivity extends ABSNavigationActivity {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String TAG_GROUPS = "groups";
	private static final String TAG_GROUP = "group";
	private static final String TAG_SCALE = "scale";
	private static final String TAG_RESULT = "result";
	
	private static final String ATTR_NAME = "name";
	private static final String ATTR_REVERSE_PLOT = "reversePlot";
	private static final String ATTR_MIN_NAME = "minName";
	private static final String ATTR_MAX_NAME = "maxName";
	private static final String ATTR_DATE = "date";
	private static final String ATTR_VALUE = "value";
	
	private static final String VALUE_TRUE = "true";
	private static final String VALUE_FALSE = "false";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	private boolean exportData(File outFile, long[] groupIds) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outFile);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(outputStream, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, TAG_GROUPS);
			
			long nowTime = System.currentTimeMillis();
			Group currentGroup = null;
			Scale currentScale = null;
			Result currentResult = null;
			Cursor currentScalesCursor = null;
			Cursor currentResultsCursor = null;
			
			for(int i = 0; i < groupIds.length; ++i) {
				currentGroup = new Group(this.dbAdapter);
				currentGroup._id = groupIds[i];
				currentGroup.load();
				currentScalesCursor = currentGroup.getScalesCursor();
				
				serializer.startTag(null, TAG_GROUP);
				serializer.attribute(null, ATTR_NAME, currentGroup.title);
				serializer.attribute(null, ATTR_REVERSE_PLOT, currentGroup.inverseResults?VALUE_TRUE:VALUE_FALSE);
				
				while(currentScalesCursor.moveToNext()) {
					currentScale = new Scale(this.dbAdapter);
					currentScale.load(currentScalesCursor);
					
					serializer.startTag(null, TAG_GROUP);
					serializer.attribute(null, ATTR_MAX_NAME, currentScale.max_label);
					serializer.attribute(null, ATTR_MIN_NAME, currentScale.min_label);
					
					currentResultsCursor = currentScale.getResults(-1, nowTime);
					while(currentResultsCursor.moveToNext()) {
						currentResult = new Result(this.dbAdapter);
						currentResult.load(currentResultsCursor);
						
						serializer.startTag(null, TAG_RESULT);
						serializer.attribute(null, ATTR_DATE, dateFormat.format(new Date(currentResult.timestamp)));
						serializer.attribute(null, ATTR_VALUE, currentResult.value+"");
						serializer.endTag(null, TAG_RESULT);
					}
					serializer.endTag(null, TAG_SCALE);
				}
				serializer.endTag(null, TAG_GROUP);
			}
			serializer.endTag(null, TAG_GROUPS);
			
			serializer.endDocument();
			
			
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private ArrayList<ImportData> getImportData(File inFile, boolean getGroups, boolean getScales, boolean getResults) {
		ArrayList<ImportData> importData = new ArrayList<ImportData>();
		
		ImportData currentImportData = null;
		Group currentGroup = null;
		Scale currentScale = null;
		Result currentResult = null;
		ArrayList<Result> currentResultsList = null;
		
		boolean inGroup = false;
		boolean inScale = false;
		boolean inResult = false;
		
		String currentTagName = "";
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new FileReader(inFile));
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				currentTagName = parser.getName();
	
				if(eventType == XmlPullParser.START_TAG) {
					
					if(currentTagName.equals(TAG_GROUP) && !inGroup) {
						if(getGroups) {
							String rev = parser.getAttributeValue(null, ATTR_REVERSE_PLOT);
							if(rev == null) {
								rev = VALUE_FALSE;
							}
							rev = rev.toLowerCase();
							
							currentGroup = new Group(this.dbAdapter);
							currentGroup.title = parser.getAttributeValue(null, ATTR_NAME);
							currentGroup.inverseResults = rev.equals(VALUE_TRUE);
							
							currentImportData = new ImportData();
							currentImportData.group = currentGroup;
							
							importData.add(currentImportData);
						}
						
						inGroup = parser.isEmptyElementTag();
					
					} else if(currentTagName.equals(TAG_SCALE) && inGroup && !inScale) {
						if(getScales) {
							currentResultsList = new ArrayList<Result>();
							
							currentScale = new Scale(this.dbAdapter);
							currentScale.min_label = parser.getAttributeValue(null, ATTR_MIN_NAME);
							currentScale.max_label = parser.getAttributeValue(null, ATTR_MAX_NAME);
							currentScale.weight = currentImportData.scales.size();
							
							currentImportData.scales.add(currentScale);
							currentImportData.results.put(currentScale, currentResultsList);
						}
						
						inScale = parser.isEmptyElementTag();
					
					} else if(currentTagName.equals(TAG_RESULT) && inGroup && inScale) {
						if(getScales && getResults) {
							try {
								String valueStr = parser.getAttributeValue(null, ATTR_VALUE);
								String dateStr = parser.getAttributeValue(null, ATTR_DATE);
								
								if(valueStr != null && dateStr != null) {
									int intValue = Integer.parseInt(valueStr);
									Date dateValue = dateFormat.parse(dateStr);
									
									currentResult = new Result(this.dbAdapter);
									currentResult.timestamp = dateValue.getTime();
									currentResult.value = intValue;
									
									currentResultsList.add(currentResult);
								}
								
							} catch (NumberFormatException e) {
							} catch (ParseException e) {
							}
						}
						
						inResult = parser.isEmptyElementTag();
					}
					
				} else if(eventType == XmlPullParser.END_TAG) {
					if(currentTagName.equals(TAG_GROUP) && inGroup) {
						inGroup = false;
						
					} else if(currentTagName.equals(TAG_SCALE) && inGroup && inScale) {
						inScale = false;
						
					} else if(currentTagName.equals(TAG_RESULT) && inGroup && inScale && inResult) {
						inResult = false;
					}
				}
	
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return importData;
	}
	
	private class ImportData {
		public Group group;
		public ArrayList<Scale> scales = new ArrayList<Scale>();
		public HashMap<Scale,ArrayList<Result>> results = new HashMap<Scale,ArrayList<Result>>();
		
		public boolean isGroupExists(DBAdapter adapter) {
			return true;
		}
		
		public boolean isScaleExists(DBAdapter adapter, Scale scale) {
			return true;
		}
		
		public boolean isResultExists(DBAdapter adapter, Result result) {
			return true;
		}
	}
}
