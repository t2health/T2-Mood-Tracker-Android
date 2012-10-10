/*
 * 
 */
package com.t2.vas.importexport;

import java.util.ArrayList;
import java.util.HashMap;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class ImportGroups {
	public Group group;
	public ArrayList<Scale> scales = new ArrayList<Scale>();
	public HashMap<Scale,ArrayList<Result>> results = new HashMap<Scale,ArrayList<Result>>();
	
	public int scaleCount;
	public int resultCount;
	public long resultMinTimestamp;
	public long resultMaxTimestamp;
	
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