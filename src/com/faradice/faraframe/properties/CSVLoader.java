package com.faradice.faraframe.properties;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CSVLoader implements IDataLoader<BasicPropertyItem> {
	public static final String ROW_ID = "ROW_ID";

	private String fileName;    // Perhaps change to a stream
	private String DomainFile;  // Create mapper for specific types
	private BasicPropertyModel<BasicPropertyItem> model = null;
	private HashMap<Integer, String> columnIndex = new HashMap<Integer, String>();
	private List<String> columnNames = new ArrayList<String>();
	private int idIndex = -1;
	private CSVReader reader = null;

	public CSVLoader(String fileName) {
		this (fileName, -1);
	}

	public CSVLoader(String fileName, int idIndex) {
		this.fileName = fileName;
		this.idIndex = idIndex;
	}

	public  BasicPropertyModel<BasicPropertyItem> load() throws IOException {
		try {
			reader = new CSVReader(new FileReader(fileName));
			// Read and parse Header
			String[] header = reader.readNext();
			prepareModel(header);

			String[] row = null;
			int rowIndex = 0;
			while ((row = reader.readNext()) != null) {
				BasicPropertyItem item = new BasicPropertyItem();
				item.setColumns(columnNames);
				item.setIdIndex(columnNames.size()-1);
				for (int col = 0; col < row.length; col++) {
					item.set(columnNames.get(col), row[col]);
				}
				String key = ROW_ID;
				Object keyValue = rowIndex++;
				if (idIndex != -1) {
					key = columnNames.get(idIndex);
					keyValue = item.get(key);
				}

				item.set(key, keyValue);
				model.add(item);
			}
			return model;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public int getRowCount() {
		return model.size();
	}

	private void prepareModel(String[] header) {
		columnNames.addAll(Arrays.asList(header));
		columnNames.add("ROW_ID");
		model = new BasicPropertyModel<BasicPropertyItem>(fileName, columnNames.size() -1 , columnNames);
		for (int i=0; i<columnNames.size(); i++) {
			columnIndex.put(i, columnNames.get(i));
		}
	}

}
