package com.t2.vas.importexport;

import com.t2.vas.importexport.ImportExport.ImportRowHandler;

class StatsImportRowHandler implements ImportRowHandler {
	public ImportFileStats fileStats;
	int currentGroupIndex;
	private GroupStat currentGroupStat;
	private ScaleStat currentScaleStat;
	
	public StatsImportRowHandler() {
		reset();
	}
	
	public void reset() {
		fileStats = new ImportFileStats();
		currentGroupIndex = -1;
	}

	@Override
	public void onGroupFound(String name, boolean inverseResults) {
		currentGroupStat = new GroupStat();
		currentGroupStat.title = name;
		fileStats.addGroup(currentGroupStat);
	}

	@Override
	public void onScaleFound(String minLabel, String maxLabel) {
		currentScaleStat = new ScaleStat();
		currentScaleStat.minLabel = minLabel;
		currentScaleStat.maxLabel = maxLabel;
		currentGroupStat.addScale(currentScaleStat);
	}

	@Override
	public void onResultFound(long timestamp, int value) {
		currentGroupStat.addResult(timestamp, value);
		currentScaleStat.addResult(timestamp, value);
	}

	@Override
	public void onNoteFound(long timestamp, String note) {
		fileStats.addNote(timestamp);
	}
}
