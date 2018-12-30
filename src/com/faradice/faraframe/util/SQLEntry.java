package com.faradice.faraframe.util;


/**
 * Entry for data item that is read from a database.
 * Supports CSV presentation of database rows
 *
 * @author ragnar.valdimarsson
 *
 */
public class SQLEntry {
	public static final String DELIMATOR = ",";
	public static final String QUOTE_CHARACTER = "\"";
	private String entry;
	private Object raw;
	private boolean isFirst = false;

	public SQLEntry(Object ce, boolean isFirst) {
		raw = ce;
		if (ce == null) ce = "";
		this.entry = ce.toString().trim();
		this.entry = this.entry.replace("\n", "\\n");
		this.isFirst = isFirst;
	}

	public String getCSV() {
		String result = QUOTE_CHARACTER+entry+QUOTE_CHARACTER;
		if (!isFirst) result = DELIMATOR+result;
		return result;
	}

	public String get() {
		return entry.replace("\\n", " ");
	}

	public String toString() {
		return getDirect().toString();
	}

	public Object getDirect() {
		return raw;
	}
}
