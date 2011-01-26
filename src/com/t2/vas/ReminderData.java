package com.t2.vas;

import java.util.ArrayList;

import com.t2.vas.db.tables.Group;

public class ReminderData {
	public ArrayList<Integer> daysEnabled;
	public ArrayList<TimePref> timesEnabled;
	public ArrayList<Group> visibleGroups;
}
