package com.faradice.faraframe.properties;

/**
 * Interface that defines a row in a Swing Table
 * Use this with TableModel for swing tables
 *
 * @author ragnar.valdimarsson
 */
public interface ITableRow {
	/**
	 * Gets the value of a column in in the table row
	 * @param colIndex Index of the column in the model
	 * @return The value of the column
	 */
	Object getValueAt(int colIndex);

	/**
	 * Finds a name of a column
	 * @param colIndex The index of the column in the model
	 * @return The name of the column
	 */
	String getColumnName(int colIndex);

	/**
	 * Gets number of columns in the table row
	 * @return number of columns in the row
	 */
	int getColumnCount();
}
