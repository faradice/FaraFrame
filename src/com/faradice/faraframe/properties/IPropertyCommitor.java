package com.faradice.faraframe.properties;

/**
 * A commitor that will be called from the PropertyEditor
 * when a user commits editing input
 *
 * @see PropertyEditor
 *
 * @author ragnar.valdimarsson
 *
 */
public interface IPropertyCommitor {

	boolean commit(IPropertyItem item) throws Exception;

}
