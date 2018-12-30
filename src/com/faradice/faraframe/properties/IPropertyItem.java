package com.faradice.faraframe.properties;

import java.util.List;

/**
 * Interface for property management and editing
 *
 * @see BasicPropertyItem

 * @author ragnar.valdimarsson
 *
 */
public interface IPropertyItem extends ITableRow {
	/**
	 *  returns the object id
	 */
	Object getId();

	/**
	 * Gets the value of a property
	 * @param key property name
	 * @return The value
	 */
	Object get(String key);

	/**
	 * Sets a value of a property
	 * @param key name of the property
	 * @param value The value of the property
	 */
	void set(String key, Object value);

	/**
	 * Sets the properties unique Id
	 * @param value The value of the Id
	 */
	void setId(Object value);

	/**
	 * If a property has constraints, such as a defined set of values return those,
	 * return empty array if there are no constraints
	 * @param key
	 * @return A list of constraints
	 */
	String[] getConstaints(String key);

	/**
	 * Class type of the field
	 * @param key The field key
	 * @return The class (type) of the field
	 */
	Class<?> getType(String key);

	/**
	 * returns the value of the field as it is stored in
	 * @param key the key of the field
	 * @param fieldValue  The value as it is represented in a field / editor
	 * @return The value of the field
	 */
	Object convertFieldToValue(String key, Object fieldValue);

	/**
	 *
	 * @param key The key of the field
	 * @return The value as it should be displayed in an editor (field)
	 */
	Object getFieldValue(String key);

	/**
	 * @return  Fields that can be edited in an editor
	 */
	String[] getEditableItems();

	/**
	 * @return  All property items (fields)
	 */
	String[] getItems();

	void setColumns(List<String> columns);

	void setIdIndex(int index); // TODO

	int getIdIndex();


}
