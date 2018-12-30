package com.faradice.faraframe.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: gisli
 * Date: 23.11.2003
 * Time: 10:52:16
 * To change this template use Options | File Templates.
 * 
 * Dynamic properties are properties that are defined with methods getValue(propName), setValue(propName, propValue).
 * 
 * NOTE:  This class breaks the property descriptor that assumes the getter has 0 args and
 * the setter one args, instead the getter has 1 arg and the setter 2.
 * Two accomplish that the setter and getter validation is changed.
 * 
 * Equals does not work properly for this class.
 */
public class DynamicPropertyDescriptor extends PropertyDescriptor{

    Class<?> propertyType;
    Method readMethod;
    Method writeMethod;

    /**
     * The Constructor.
     * 
     * @param propertyName the property name
     * @param propertyType the property type
     * @param obj the obj
     * 
     * @throws IntrospectionException the introspection exception
     * @throws NoSuchMethodException the no such method exception
     */
    public DynamicPropertyDescriptor(final String propertyName, final Class<?> propertyType, Object obj) throws IntrospectionException, NoSuchMethodException {
        this(propertyName, propertyType, obj.getClass().getMethod("getClientProperty", new Class[]{Object.class}),
                obj.getClass().getMethod("putClientProperty", new Class[]{Object.class, Object.class}));
    }

    /**
     * The Constructor.
     * 
     * @param propertyName the property name
     * @param propertyType the property type
     * @param readMethod the read method
     * @param writeMethod the write method
     * 
     * @throws IntrospectionException the introspection exception
     */
    public DynamicPropertyDescriptor(final String propertyName, final Class<?> propertyType, final Method readMethod, final Method writeMethod) throws IntrospectionException  {
        // Call the parent const. with null and dummy values.  These will be replaced below.
        super("DummyName", null, null);

        setName(propertyName);
        this.propertyType = propertyType;

        setReadMethod(readMethod);
        setWriteMethod(writeMethod);
    }

    /**
     * Gets the property type.
     * 
     * @return the property type
     */
    @Override public synchronized Class<?> getPropertyType() {
        return propertyType;
    }

    /**
     * Sets the property type.
     * 
     * @param propertyType the property type
     */
    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    /**
     * Gets the read method.
     * 
     * @return the read method
     */
    @Override public synchronized Method getReadMethod() {
        return readMethod;
    }

    /**
     * Sets the read method.
     * 
     * @param getter the new read method
     * 
     * @throws IntrospectionException the introspection exception
     */
    @Override public synchronized void setReadMethod(Method getter) throws IntrospectionException {

        readMethod = getter;

	    if (readMethod != null) {
            if (readMethod.getParameterTypes().length != 1) {
                throw new IntrospectionException("bad read method arg count");
            }
            if (readMethod.getReturnType() == Void.TYPE) {
                throw new IntrospectionException("read method " +
                        readMethod.getName() + " returns void");
            }
	    }
    }

    /**
     * Gets the write method.
     * 
     * @return the write method
     */
    @Override public synchronized Method getWriteMethod() {
        return super.getWriteMethod();
    }

    /**
     * Sets the write method.
     * 
     * @param setter the new write method
     * 
     * @throws IntrospectionException the introspection exception
     */
    @Override public synchronized void setWriteMethod(Method setter) throws IntrospectionException {
        writeMethod = setter;

        if (writeMethod != null) {
            Class<?> params[] = writeMethod.getParameterTypes();
            if (params.length != 2) {
                throw new IntrospectionException("bad write method arg count");
            }
        }
    }

    /**
     * Gets the value.
     * 
     * @param bean the bean
     * 
     * @return The value
     */
    public Object getValue(Object bean) {
        try {
            return invoke(bean, getReadMethod(), new Object[]{getName()});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // Ignore the error.
            return null;
        }
    }
    
    private static Object invoke(Object bean, Method method, Object[] args) throws IllegalAccessException {
        try {
            if (method != null) {
                return method.invoke(bean, args);
            }
        } catch (InvocationTargetException ite) {
            String errMsg = ite.getTargetException().getMessage();
            throw new RuntimeException(errMsg, ite.getTargetException());
        }
        return null;
    }
    

    /**
     * Equals.
     * 
     * @param obj the obj
     * 
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }
}
