package com.faradice.faraframe.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * This class inidcates that the property of the given specs does NOT exists on teh given bean.
 */
public class DummyPropertyDescriptor extends PropertyDescriptor{

    /**
     * The Constructor.
     * 
     * @param propertyName the property name
     * @param propertyType the property type
     * 
     * @throws IntrospectionException the introspection exception
     */
    public DummyPropertyDescriptor(final String propertyName, final Class<?> propertyType) throws IntrospectionException  {
        super("DummyTempName", propertyType != null ? propertyType : Object.class, null, null);
        setName(propertyName);
    }

}
