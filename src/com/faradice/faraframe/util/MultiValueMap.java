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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map that allows for multiple values for each key.
 *
 * The values per key are not ordered.
 *
 * @author gisli
 * @version $Revision: 1.9 $ $Date: 2010/07/09 11:17:25 $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MultiValueMap {

    protected Map<Object, Collection<Object>> map;  // Contains the map data.
    protected Class<? extends Collection> collectionClass = HashSet.class;

    /**
     * Construct an empty map.
     */
    public MultiValueMap() {
        this(new HashMap<Object, Collection<Object>>());
    }

    /**
     * Construct an empty map.
     * @param map The map to use
     */
    public MultiValueMap(Map<Object, Collection<Object>> map) {
        this.map = map;
    }

    /**
     * Get the type of the underlying collction.
     * @return type of the underlying collection.
     */
    public Class<? extends Collection> getCollectionClass() {
        return collectionClass;
    }

    /**
     * Set the type of the underlying collction.
     * @param collectionClass
     */
    public void setCollectionClass(Class<? extends Collection> collectionClass) {
        if (!Collection.class.isAssignableFrom(collectionClass))  {
            throw new RuntimeException("Incompatible types:  " + collectionClass != null ? collectionClass.getName() : null + " is not a collection");
        }
        this.collectionClass = collectionClass;
    }

    /**
     * Get all values for the given key.
     *
     * @param key   key whose associated value is to be returned.
     * @return Collection containing all values for the given key.  The collection
     *         is not ordered.
     * @see java.util.Map#get
     */
    public Collection<? extends Object> get(Object key) {
        Collection<Object> collection = map.get(key);
        if (collection != null) {
            return collection;
        }

        return null;
    }

    /**
     * Add a new value to the list of values associated with the given key.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @see java.util.Map#put
     */
    public void put(Object key, Object value) {
        Collection<Object> collection = map.get(key);
        if (collection == null) {
            try {
                collection = collectionClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Invalid collection class, instance could not be created", e);
            }
            map.put(key, collection);
        }
        collection.add(value);
    }

    /**
     * Add a new key to the list of keys (with empty collection).  If the
     * key exists nothing is done.
     *
     * @param key   the key
     * @see java.util.Map#put
     */
    public void put(Object key) {
        Collection<Object> collection = map.get(key);
        if (collection == null) {
            collection = new HashSet<Object>();
            map.put(key, collection);
        }
    }

    /**
     * Remove the the given value from the list of values associated with the given key.
     *
     * @param key
     * @param value element to be removed from this collection, if present.
     * @see java.util.Map#remove
     */
    public void remove(Object key, Object value) {
        Collection<Object> collection = map.get(key);
        if (collection != null) {
            collection.remove(value);
        }
    }

    /**
     * Returns a set view of the keys contained in this map.
     *
     * @return  a set view of the keys contained in this map.
     * @see java.util.Map#keySet
     */
    public Set<Object> keySet() {
        return map.keySet();
    }

    /**
     * Returns a collection view of the values contained in this map.  The values are combined list of
     * all the values of all the keys.  The values are not ordered.
     *
     * @return a collection view of the values contained in this map.
     * todo If this is heavily used it must be optimized.
     * @see java.util.Map#values
     */
    public Collection<Object> values() {
        ArrayList<Object> values = new ArrayList<Object>();
        Collection<Collection<Object>> collectionCollection =  map.values();
        for (Collection<Object> keyCollection : collectionCollection) {
            values.addAll(keyCollection);
        }
        return values;
    }

    /**
     *  Clear the map.
     */
    public void clear() {
        map.clear();
    }
}
