package com.faradice.faraframe.properties;

import java.util.List;

/**
 * A generic model to use with the property framework
 *
 * Implement this model for specific implementation or use
 * the BasicPropertyModel for standard behavior
 *
 * @author ragnar.valdimarsson
 *
 * @see BasicPropertyModel
 * @see IPropertyItem
 *
 * @param <T> Type property type of the model
 */
public interface IPropertyModel<T extends IPropertyItem> {
	String getName();

	List<T> getItems();

	T getItem(Object Id);

	int size();

	int getIdIndex();

	List<String> getColumnNames();

	List<Integer> getColumnWidth();
}
