package com.faradice.faraframe.context;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <H1> ContextBinding - bind objects to contexts. </H1>
 * Provides a simple way to associate object instances with contexts.
 * 
 * This class supports inheritance between contexts, i.e. if an instance has not
 * been bound to a child context, it's parents binding will be used.
 * 
 * Binding an object to a child context will override the binding in the child
 * context only.
 * 
 *  
 * <p> Author Vilmundur Palmason
 * <p> Created Oct 28, 2003
 * @version  $Id: ContextBinding.java,v 1.3 2008/06/26 12:50:10 gudmfr Exp $
 * @param <T> 
 */
public class ContextBinding<T> {
    private Map<ContextHandle,T> mContextMap = new HashMap<ContextHandle,T>();
    
    /**
     * Bind a specific object instance to a context.  Previous binding
     * for the same context will be lost.  Note that passing a null object will not
     * revert to using the binding of the parent context, use remove() instead.
     * @param context Context to bind to
     * @param ob Object bound to context.
     */
    public void set(ContextHandle context,T ob) {
        mContextMap.put(context,ob);    
    }
    
    /**
     * Get the bound object instance for the context.  If no binding exists the
     * parent binding (if any) is used.
     * @param context Context to get binding for
     * @return bound object.  If no bound object return first binding from ancestor context.
     *         Returns null if no binding.
     */
    public T get(ContextHandle context) {
        if (mContextMap.containsKey(context)) return mContextMap.get(context);
        if (context == null) return null;
        return getFirst(context.getAllImplied());
    }
    
    private T getFirst(Set<ContextHandle> contexts) {
        for (ContextHandle handle: contexts) {
			if (mContextMap.containsKey(handle)) return mContextMap.get(handle);
		}
        return null;
    }

    
    /**
     * Get exact binding - i.e. parent context will never be used.
     * @param context Context to get binding for
     * @return Bound object or null if no binding in this context.
     */
    
    public T getExact(ContextHandle context) {
        return mContextMap.get(context);
    }
    
    /**
     * Remove and return the instance bound to this context.
     * @param context Context to remove binding for
     * @return Previously bound object or null if no object was bound. 
     */
    public T remove(ContextHandle context) {
        return mContextMap.remove(context);
    }
    
    /**
     *  Get a set of all contexts that have actual bindings.
     *  @return Set of ContextHandle objects.  This set cannot be modified.  
     */
    
    protected Set<ContextHandle> boundContexts() {
        return Collections.unmodifiableSet(mContextMap.keySet());
    }
}
