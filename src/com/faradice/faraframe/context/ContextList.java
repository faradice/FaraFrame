package com.faradice.faraframe.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <H1> ContextList - Context based list. </H1>
 * 
 * This behaves similar to standard list except that values are added, removed
 * and accessed based on Context.
 * 
 * Values can be added in any context, and will be visible in that context and all
 * child contexts.  In any given context, only values added in exactly that
 * context can be removed. 
 * 
 * <p> Author Vilmundur Palmason
 * <p> Created Nov 6, 2003
 * @version  $Id: ContextList.java,v 1.3 2008/06/26 12:50:10 gudmfr Exp $
 * @param <T> 
 */
public class ContextList<T> {

    /**
     * Contains actual value lists, one for each context.
     * Key: Context, Value: List<T>
     */
    private Map<ContextHandle,List<T>> mLists;
    
	/**
	 * Constructor for ContextList.
	 * 
	 */
	public ContextList() {
		mLists = new HashMap<ContextHandle,List<T>>();
	}

    /**
     * Add new object for this list visible in the specified context - means that object
     * will be part of this list when queried by the context or any descendants.
     * @param handle Context to use when adding
     * @param o Object to add to list.
     */
    public void add(ContextHandle handle,T o) {
        getExactList(handle).add(o);
    }

    /**
     * Remove object from list for specified context.   Object is removed only 
     * if it was added using the specified context, it will not be removed from parent lists.
     * @param handle Context to use when removing.
     * @param o Object to remove from list.
     */
    public void remove(ContextHandle handle,T o) {
        getExactList(handle).remove(o);
    }
    
	private List<T> getExactList(ContextHandle handle) {
        List<T> list = mLists.get(handle);
        if (list == null) {
            list = new ArrayList<T>();
            mLists.put(handle,list);
        }
        return list;
	}

    /**
     * Return a new list object containing all visible items from parent context
     * as well as the passed context.  Normally, the parent contexts are search breadth first,
     * the first value from the topmost context are first, last value from the passed context
     * is last.  If reverse is true, the list order is exactly reversed.
     * Any changes to the returned List do not affect the underlying data.  
     * @param handle Context to get list from
     * @param reverse If returned list is to be in reverse order.
     * @return List<T> of all objects visible from specific context.
     */

    public List<T> toList(ContextHandle handle,boolean reverse) {
        final Set<ContextHandle> allContexts;
        if (handle == null) {
        	allContexts = new HashSet<ContextHandle>();
        } else {
        	allContexts = handle.getAllImplied();
        }
        
        List<T> result = new LinkedList<T>();
        for (ContextHandle h : allContexts) {
            List<T> l = getExactList(h);
            if (reverse) {
                for (int i=l.size()-1;i>=0;i--) {
                    result.add(l.get(i));
                }
            } else {
                result.addAll(0,l);
            }
		}
        return result;
    }
    
    
}
