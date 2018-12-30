package com.faradice.faraframe.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <H1> ContextMap - allow context binding using key-value pairs.</H1>
 * 
 * Combines ContextBinding with Map funcions.  Values are bound to a context and
 * to a key.   
 * 
 * The methods get,put,remove,containsKey and keySet all behave similar
 * to standard Map methods with the addition of a ContextHandle parameter.  
 * 
 * These make key/value pairs bound in parent contexts visible and allow 
 * overriding them in child contexts.  
 * 
 * In addition, getExact and containsKeyExact can be used to query only the 
 * specified context without checking parent contexts.
 * 
 * 
 * <p> Author Vilmundur Palmason
 * <p> Created Oct 30, 2003
 * @version  $Id: ContextMap.java,v 1.4 2008/09/04 15:14:43 gudmfr Exp $
 * @param <K> 
 * @param <V> 
 */
public class ContextMap<K,V> {
    
    /**
     * Holds context mapping.
     * Key:  Key as received in method calls.
     * Value: ContextBinding objects.
     */
    private Map<K,ContextBinding<V>> mMap;
    
    /**
     * Create empty context map.  Keys will be compared using hashValue() and equals().
     * Ordering is not preserved.
     */
	public ContextMap() {
        mMap = new HashMap<K,ContextBinding<V>>();
	}
 
 
    /**
     * Create empty context map.  Keys will be stored and ordered depending on
     * the map implementation passed.  This ordering will be the same for all
     * contexts.
     * @param keyMap 
     */
    
    public ContextMap(Map<K,ContextBinding<V>> keyMap) {
        keyMap.clear();
        mMap = keyMap;
    }
    
    /**
     * Bind value to key in specified context.  The same key-value binding
     * will be visible in child contexts.  Note that null values can be bound
     * also.  To remove binding use remove().  
     * @param handle 
     * @param key 
     * @param value 
     */
    public void put(ContextHandle handle,K key,V value) {
        ContextBinding<V> binding =  mMap.get(key);
        if (binding == null) {
            binding = new ContextBinding<V>();
            mMap.put(key,binding);
        }
        binding.set(handle,value);
    }
    
    /**
     * Get value bound to key in specified context.   If no binding exists in
     * parent binding (if any) will be used.
     * @param handle 
     * @param key 
     * @return the value
     */

    public V get(ContextHandle handle,Object key) {
        ContextBinding<V> binding = mMap.get(key);
        if (binding == null) return null;
        return binding.get(handle);
    }
    
    /**
     * Get value bound to key in exactly this context.  Parent context is
     * not used.
     * @param handle 
     * @param key 
     * @return  the value
     */
    public V getExact(ContextHandle handle,K key) {
        ContextBinding<V> binding =  mMap.get(key);
        if (binding == null) return null;
        return binding.getExact(handle);
    }
    
    /**
     * Check if specified key is bound.  Parent context is used if no exact
     * binding exists.
     * @param handle 
     * @param key 
     * @return  True if key is bound, else false
     */
    public boolean containsKey(ContextHandle handle,Object key) {
        ContextBinding<V> binding = mMap.get(key);
        if (binding == null) return false;
        Set<ContextHandle> ctxSet = new HashSet<ContextHandle>(binding.boundContexts());
        Set<ContextHandle> allImplied = handle.getAllImplied();
        ctxSet.retainAll(allImplied);
        return (ctxSet.size() >0);
    }
    
    
    /**
     * Check if specified key is bound.  Parent context is not used.
     * @param handle 
     * @param key 
     * @return True if key is bound, else false
     */
    public boolean containsKeyExact(ContextHandle handle,K key) {
        ContextBinding<V> binding = mMap.get(key);
        if (binding == null) return false;
        return binding.boundContexts().contains(handle);
    }
    
    /**
     * Remove binding for key in this context.  Subsequent calls to get() will
     * always use parent context if available.
     * @param handle 
     * @param key 
     */
    public void remove(ContextHandle handle,Object key) {
        ContextBinding<V> binding = mMap.get(key);
        if (binding != null) {
            binding.remove(handle);
            if (binding.boundContexts().size() == 0) {
                mMap.remove(key);
            }
        }
    }

    /**
     * Remove all bindings for specified context.  This is the same as calling
     * remove() on each key in keySet()
     * @param handle 
     */ 
    public void removeAll(ContextHandle handle) {
        // Requires seperate set because we are removing from mMap inside loop.
        Set<K> toIterate = new HashSet<K>(mMap.keySet());
        for (K key: toIterate) {
            remove(handle,key);
    	}
    }
    
    /**
     *  Get the keyset for specified context.  This will return all keys bound
     * in the contex or any parent context. The set cannot be modified. 
     * @param handle 
     * @return the keyset 
     */
    public Set<K> keySet(ContextHandle handle) {
        Set<K> allKeys =  mMap.keySet(); 
        Set<K> result = new HashSet<K>();
        for (K key: allKeys) {
			if (containsKey(handle,key)) {
                result.add(key);
			}
		}
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Get java.util.Map view of this instance as seen from the specified context.  
     * This is 'live' but cannot be modified.
     * Exact methods supported: get(),keySet(),size(),isEmpty(),containsKey(),
     * @param handle 
     * @return  a map
     * 
     */

    public Map<K,V> asMap(final ContextHandle handle) {
        Map<K,V> m = new Map<K,V>() {

			public int size() {
                return ContextMap.this.keySet(handle).size();
			}

			public void clear() {
                throw new UnsupportedOperationException("Not supported");
			}

			public boolean isEmpty() {
                return size()==0;
			}
			public boolean containsKey(Object key) {
                return ContextMap.this.containsKey(handle,key);
			}

			public boolean containsValue(Object value) {
                throw new UnsupportedOperationException("Not supported");
			}

			public Collection<V> values() {
                throw new UnsupportedOperationException("Not supported");
			}

			public void putAll(Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException("Not supported");
			}

			public Set<Entry<K,V>> entrySet() {
                throw new UnsupportedOperationException("Not supported");
			}

			public Set<K> keySet() {
                return ContextMap.this.keySet(handle);
   			}

			public V get(Object key) {
                return ContextMap.this.get(handle,key);
			}
            
			public V remove(Object key) {
                throw new UnsupportedOperationException("Not supported");
			}

			public V put(K key, V value) {
                throw new UnsupportedOperationException("Not supported");
			}
		};
        return m;
    }
    
}
