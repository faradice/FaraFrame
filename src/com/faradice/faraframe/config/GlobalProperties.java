/* 
 * Copyright (c) 2006 deCODE Genetics Inc. 
 * All Rights Reserved. 
 * 
 * This software is the confidential and proprietary information of 
 * deCODE Genetics Inc. ("Confidential Information"). You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with deCODE. 
 */ 
package com.faradice.faraframe.config; 

import java.util.logging.Logger;


/** 
 * 
 * Global property provider holding one static instance of
 * a property object. 
 * 
 * This class was created to be backward compatible with a Persistant 
 * and global legacy property system that uses Database to store proerties
 * 
 * Before using this class, the setProvider must be called with the
 * provider that stores the properties
 * 
 * @version $Id: GlobalProperties.java,v 1.2 2010/12/08 09:59:30 gudmfr Exp $ 
 * 
 */
public class GlobalProperties {
    private static final Logger logger = Logger.getLogger(GlobalProperties.class.getName());
    private static IProperties properties;
    
    /**
     * @param provider The propery provider
     */
    public static void setProvider(IProperties provider) {
        properties = provider;
    }

    /**
     * @param key property key
     * @param value property value
     */
    public static void setProperty(String key, String value) {
        if (properties != null) {
            properties.setProperty(key, value);
        } else {
            logger.warning("Global properties provider has not been set");
        }
    }
    
    /**
     * @param key the key
     * @return property value or null if key not found
     */
    public static String getProperty(String key) {
        String value = null;
        if (properties != null) {
            return properties.getProperty(key);
        }
		logger.warning("Global properties provider has not been set");    
        return value;
    }
    
    /**
     * @param key key to fined
     * @param defValue default value
     * @return value of the property or defValue if no value exists
     */
    public static String getProperty(String key, String defValue) {
        String value = getProperty(key);
        if (value == null) {
            value = defValue;
        }
        return value;
    }
    
}
 

