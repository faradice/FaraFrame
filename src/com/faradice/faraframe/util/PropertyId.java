/*
 * Copyright (c) 2002 deCODE Genetics Inc.
 * All Rights Reserved
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc.  ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 *
 */
 
package com.faradice.faraframe.util;

/**
 * PropertyId is a simple data class that contains the name and type of a bean property.
 *
 * It is indented to be used to identify bean properties, where ProperytDescriptors can not be used
 * (as they need accessor methods when constructed).
 *
 * Both the name and type of the property can be null and will then be treaeted as an wildcard (
 * when matching the property to other properties)
 *
 * @author gisli
 * @version $Revision: 1.3 $ $Date: 2008/08/19 14:13:11 $
 */

public class PropertyId {

    private String propertyName;    // Name of the property (can be null)
    private Class<?> propertyType;     // Type of the property (can be null)

    /**
     * Construct new PropertyId.
     * @param propertyName  name of the property (can be null)
     * @param propertyType  type of the property (can be null)
     */
    public PropertyId(String propertyName, Class<?> propertyType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    /**
     * Get the property name.
     * @return  name of the property.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Get the property type.
     * @return type of the property.
     */
    public Class<?> getPropertyType() {
        return propertyType;
    }

     // NOTE:  This equals which is not the same as a matching method (where for example null matches everything)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof PropertyId)) return false;

        PropertyId objProp = (PropertyId) obj;
        return ((propertyName == null && objProp.getPropertyName() == null)
                    || propertyName.equals(objProp.getPropertyName()))
               &&
               ((propertyType == null && objProp.getPropertyType() == null)
                    || propertyType.equals(objProp.getPropertyType()));
    }

    @Override
    public int hashCode() {
        // The hascode for the string of the args.
        return (propertyName + "#" + propertyType).hashCode();
    }

}

/*
 * $Log: PropertyId.java,v $
 * Revision 1.3  2008/08/19 14:13:11  gudmfr
 * Addressed warnings
 *
 * Revision 1.2  2008/04/01 11:09:44  gudmfr
 * clenaup
 *
 * Revision 1.1  2007/06/01 14:41:11  gudmfr
 * Split tools_framework code into model related part and gui related part. Moved model related part to tools_generic - allows services to interact with framework stuff without depending on the project.
 *
 * Revision 1.2  2006/05/16 15:47:45  vilm
 * Code cleanup + infer generics
 *
 * Revision 1.1.1.1  2004/04/06 14:43:19  build
 * no message
 *
 * Revision 1.2  2003/11/26 09:48:18  gisli
 * Some refactoring.
 *
 * Revision 1.1  2003/11/19 13:29:16  gisli
 * Refactoring of the action framework.  Added bean action manager to the action registry.
 *
 */