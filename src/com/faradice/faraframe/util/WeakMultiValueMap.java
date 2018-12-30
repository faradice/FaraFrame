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

import java.util.Collection;
import java.util.WeakHashMap;

/**
 * Weak Map that allows for multiple values for each key.
 *
 * The values per key are not ordered.
 *
 * @author gisli
 * @version $Revision: 1.4 $ $Date: 2008/07/25 09:45:21 $
 */

/**
 * Map that allows for multiple values for each key.
 */
public class WeakMultiValueMap extends MultiValueMap {

    /**
     * Construct WeakMultiValueMap
     */
    public WeakMultiValueMap() {
        super(new WeakHashMap<Object, Collection<Object>>());
    }

}
