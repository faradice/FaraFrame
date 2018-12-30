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


/** 
 * 
 * General interface for basic property operations 
 * 
 */
public interface IProperties {
    /**
     * @param key
     * @param value
     */
    public void setProperty(String key, String value);
    
    /**
     * @param key key to find
     * @return value or null
     */
    public String getProperty(String key);
    
    /**
     * @param key key to fined
     * @param defValue default value
     * @return value of the property or defValue if no value exists
     */
    public String getProperty(String key, String defValue);
}
 

