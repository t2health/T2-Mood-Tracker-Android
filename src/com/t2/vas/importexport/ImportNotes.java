package com.t2.vas.importexport;

import java.util.ArrayList;

import com.t2.vas.db.tables.Note;

public class ImportNotes {
	public ArrayList<Note> notes = new ArrayList<Note>();
	public int notesCount;
	public long notesMinTimestamp;
	public long notesMaxTimestamp;
}
