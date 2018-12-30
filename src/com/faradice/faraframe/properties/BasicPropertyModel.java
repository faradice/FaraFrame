package com.faradice.faraframe.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.faradice.faraframe.util.SQLDB;
import com.faradice.faraframe.util.SQLEntry;

/**
 * A General and basic implementation of the property model.
 * Contains the most common and general usage of property models.
 *
 * @author ragnar.valdimarsson
 *
 * @param <T>  The type of the property item
 */
public class BasicPropertyModel<T extends IPropertyItem> implements IPropertyModel<T>, Serializable {

	private String name = null;
	private int idIndex = 0;
	private List<String> columnNames = new ArrayList<String>();
	private List<Integer> columnWidth = new ArrayList<Integer>();
	private int cursor = 0;

	private LinkedHashMap<Object, T> propertyMap = new LinkedHashMap<Object, T>();

	public BasicPropertyModel() {
		super();
	}

	public BasicPropertyModel(String name) {
		this.name = name;
	}

	public BasicPropertyModel(String name, int idIndex, List<String> columnNames) {
		this(name);
		this.idIndex = idIndex;
		this.columnNames = columnNames;
	}

	public BasicPropertyModel(String name, int idIndex, List<String> columnNames, List<Integer> columnWidth) {
		this(name, idIndex, columnNames);
		this.columnWidth = columnWidth;
	}

	public BasicPropertyModel(List<T> properties) {
		this();
		setColumnNames(properties.get(0));
		setIdIndex(properties.get(0).getIdIndex());
		add(properties);
	}

	public String getName() {
		return name;
	}

	public List<T> getItems() {
		return new ArrayList<T>(propertyMap.values());
	}

	public T getNext() {
		return getItems().get(cursor++);
	}
	
	public boolean hasNext() {
		return (propertyMap.size() > cursor);
	}
	
	public void reset() {
		cursor = 0;
	}

	public void add(Object key, T item) {
		propertyMap.put(key, item);
	}

	
	public void add(T item) {
		add(item.getId(), item);
	}

	public void add(List<T> items) {
		for (T item : items) {
			add(item);
		}
	}

	public T add(Object[] entries, Class<T> type) {
		T propertyItem = getInstanceOf(type);
		propertyItem.setColumns(getColumnNames());
		propertyItem.setIdIndex(getIdIndex());

		// TODO add more fields if there are more items than fields
		String[] fields = propertyItem.getItems();
		int i=0;
		for (Object entry : entries) {
			propertyItem.set(fields[i++], entry);
		}
		add(propertyItem);
		return propertyItem;
	}

	public void add(SQLEntry[] entries, Class<T> type) {
		Object[] values = new Object[entries.length];
		for (int i=0; i< values.length; i++) {
			values[i] = entries[i].getDirect();
		}
		add(values, type);
	}

	public void add(List<SQLEntry[]> entries, Class<T> type) {
		for (SQLEntry[] row : entries) {
			add(row, type);
		}
	}

	public void addObjects(List<Object[]> entries, Class<T> type) {
		for (Object[] row : entries) {
			add(row, type);
		}
	}

	public void loadFromSQL(SQLDB connection, String sqlQuery, Class<T> type) {
		List<SQLEntry[]> entries = connection.executeQuery(sqlQuery);
		if (columnNames.size() < 1) {
			setColumnNames(connection.getColumnsOfQuery());
		}
		add(entries, type);
	}

	public T getItem(Object Id) {
		return propertyMap.get(Id);
	}

	public int size() {
		return propertyMap.size();
	}

	private T getInstanceOf(Class<T> type) {
		T propertyItem = null;;
		try {
			propertyItem = type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return propertyItem;
	}

	/**
	 * @param columnNames list of column names
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public void setColumnNames(T property) {
		this.columnNames = Arrays.asList(property.getItems());
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public List<Integer> getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(List<Integer> width ) {
		columnWidth = width;
	}

	public int getIdIndex() {
		return idIndex;
	}

	public void setIdIndex(int index) {
		this.idIndex = index;
	}
	
	/**
	 * @param item Item to delete
	 * @return Deleted item or null if item not found
	 */
	public T delete(T item) {
		return propertyMap.remove(item.getId());
	}
}


