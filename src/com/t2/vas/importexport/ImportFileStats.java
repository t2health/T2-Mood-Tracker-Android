package com.t2.vas.importexport;

import java.util.ArrayList;

public class ImportFileStats {
	public ArrayList<GroupStat> groups = new ArrayList<GroupStat>();
	
	public int notesCount = 0;
	public long minNoteTimestamp = -1;
	public long maxNoteTimestamp = -1;

	public GroupStat getGroupStat(String title) {
		GroupStat g;
		for(int i = 0; i < groups.size(); ++i) {
			g = groups.get(i);
			if(g.title.equals(title)) {
				return g;
			}
		}
		return null;
	}
	
	public long getMinResultTimestamp() {
		long currentts = -1;
		long tmpts = -1;
		for(int i = 0; i < groups.size(); ++i) {
			tmpts = groups.get(i).minResultTimestamp;
			if(tmpts < currentts || currentts == -1) {
				currentts = tmpts;
			}
		}
		return currentts;
	}
	
	public long getMaxResultTimestamp() {
		long currentts = -1;
		long tmpts = -1;
		for(int i = 0; i < groups.size(); ++i) {
			tmpts = groups.get(i).maxResultTimestamp;
			if(tmpts > currentts || currentts == -1) {
				currentts = tmpts;
			}
		}
		return currentts;
	}
	
	public int addGroup(GroupStat g) {
		this.groups.add(g);
		return this.groups.size()-1;
	}
	
	public void addNote(long timestamp) {
		++notesCount;
		if(timestamp < minNoteTimestamp || minNoteTimestamp == -1) {
			minNoteTimestamp = timestamp;
		}
		if(timestamp > maxNoteTimestamp || maxNoteTimestamp == -1) {
			maxNoteTimestamp = timestamp;
		}
	}
}
