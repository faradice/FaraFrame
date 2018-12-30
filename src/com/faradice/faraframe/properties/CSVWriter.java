package com.faradice.faraframe.properties;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.faradice.faraframe.util.SQLEntry;

public class CSVWriter<T extends BasicPropertyItem> {
	private final String fileName;
	private String[] columnsToWrite = null;

	public CSVWriter(String fileName) {
		this.fileName = fileName;
	}

	public void setColumsToWrite(String[] columns) {
		this.columnsToWrite = columns;
	}

	private String[] getKeys(List<T> items) {
		String[] keys = columnsToWrite;
		if (keys == null) {
			Set<String> allKeys = new HashSet<String>();
			for (T item : items) {
				allKeys.addAll(item.getKeySet());
			}
			keys = allKeys.toArray(new String[0]);
		}
		return keys;
	}

	public void writeModel(IPropertyModel<T> model) throws Exception {
		String[] keys = getKeys(model.getItems());

		SQLEntry[] header = new SQLEntry[keys.length];
		for (int i=0; i<header.length; i++) {
			header[i] = new SQLEntry(keys[i], i==0);
		}
		List<SQLEntry[]> entries = new ArrayList<SQLEntry[]>();
		entries.add(header);

		for (T item : model.getItems()) {
			boolean isFirst = true;
			SQLEntry[] row = new SQLEntry[keys.length];
			for (int col=0; col<keys.length; col++) {
				Object value = item.get(keys[col]);
				row[col] = new SQLEntry(value, isFirst);
				isFirst = false;
			}
			entries.add(row);
		}
		printEntries(entries);
	}

    private void printEntries(List<SQLEntry[]> entities) throws Exception {
    	PrintWriter pw = new PrintWriter(fileName);
    	for (SQLEntry[] row  : entities) {
    		int colNumber = 0;
    		for (SQLEntry entry : row) {
  				pw.print(entry.getCSV());
    			colNumber++;
    		}
    		pw.println();
    	}
    	pw.close();
    }


}
