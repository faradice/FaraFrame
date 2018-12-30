package com.faradice.faraframe.properties;

/**
 * A validator that can be pluged into the Property Editor to
 * make sure all values that the user enters containt valid data
 *
 * @author ragnar.valdimarsson
 *
 */
public interface IPropertyValidator {

	boolean isValid(IPropertyItem item);

}
