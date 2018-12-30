package com.faradice.faraframe.context;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * <H1> ContextHandle.</H1>
 * 
 * A ContextHandle instance is, like the name suggests a handle on a certain context.
 * By itself it does nothing but it can be used to statically access services within 
 * that context.  How this is utilized is up to the programmer but typically it would be used
 * to serve different instances of objects based on the callers context.
 * 
 * The following example shows how database pools could be served depending on context:
 * 
 * class DBService {  
 *   ContextBinding pools = new ContextBinding();
 * 
 *   public void setPool(ContextHandle handle,DatabasePool pool) {
 *       pools.put(handle,pool);
 *   }
 * 
 *   public DatabasePool getPool(ContextHandle handle) {
 *     DatabasePool pool = (DatabasePool) pools.get(handle);
 *     return pool;
 *   }
 * }
 * 
 * class TestView {
 *   public static void main(String[] args) {
 *       ContextHandle ctx = ContextHandle.newContext();
 *       DatabasePool pool = ...;
 *       DBService.setPool(ctx,pool);
 *       IViewManager mgr = ....
 *       mgr.load("myviews.xml");
 *       ...
 *   }
 * }
 * 
 * class MyView implements view {
 *   private MainContext mCtx;
 *   public void setContext(ContextHandle ctx) {
 *      mCtx = ctx;
 *   }
 * 
 *   ...
 * 
 *   public void do() {
 *     DatabasePool pool = DBService.getPool(mCtx);
 *     ....
 *   }
 * }
 * 
 * Contexts can be chained in a parent-child relationship. ContextBinding should
 * be used to keep track of objects in different contexts.
 *  
 * <p> Author Vilmundur Palmason
 * <p> Created Oct 28, 2003
 * @version  $Id: ContextHandle.java,v 1.4 2008/06/26 12:50:10 gudmfr Exp $
 */
public class ContextHandle {
    private Set<ContextHandle> mAllImplied;
	private Set<ContextHandle> mImplied = new LinkedHashSet<ContextHandle>();
	private ClassLoader classLoader;
    
    private ContextHandle(ContextHandle[] implied, ClassLoader cl) {
        if (implied != null) {
            for (ContextHandle c: implied) {
                mImplied.add(c);
            }
        }
        classLoader = cl;
    }
    
    /**
     * Create new top level context.  
     * @return ContextHandle for top level context.
     */
    public static ContextHandle newContext() {
        return new ContextHandle(null,ContextHandle.class.getClassLoader());
    }
    
    /**
     * Create new top level context.  
     * @param cl 
     * @return ContextHandle for top level context.
     */
    public static ContextHandle newContext(ClassLoader cl) {
        return new ContextHandle(null,cl);
    }
    
    /**
     * Create new context with specified parent.
     * @param implied an existing parent context.
     * @return ContextHandle for a new child context of parent. 
     */
    public static ContextHandle newContext(ContextHandle[] implied) {
        return new ContextHandle(implied,ContextHandle.class.getClassLoader());
    }
        
    /**
     * Create new context with specified parent.
     * @param implied 
     * @param cl 
     * @return ContextHandle for a new child context of parent. 
     */
    public static ContextHandle newContext(ContextHandle[] implied,ClassLoader cl) {
        return new ContextHandle(implied,cl);
    }
    
    /**
     * Get all context directly implied by this context (i.e. direct parents).
     */
    protected Set<ContextHandle> getDirectlyImplied() {
        return Collections.unmodifiableSet(mImplied);
    }
    
    /**
     * Get all contexts implied by this context (i.e. all ancestors).
     * @return
     */
    protected Set<ContextHandle> getAllImplied() {
        if (mAllImplied == null) {
            mAllImplied = new LinkedHashSet<ContextHandle>();
            LinkedList<ContextHandle> toProcess = new LinkedList<ContextHandle>();
            toProcess.add(this);
            while (toProcess.size() >0) {
                ContextHandle handle =  toProcess.remove(0);
                if (! mAllImplied.contains(handle)) {
                    mAllImplied.add(handle);
                    toProcess.addAll(handle.getDirectlyImplied());
                }
            }
            
        }
        return Collections.unmodifiableSet(mAllImplied);
    }
    
    /**
     * Get the ClassLoader for this context
     * @return the classloader for this context
     */
    public ClassLoader getClassLoader() {
    	return classLoader;
    }
    
    /**
     * Check if this context implies (inherits) another.
     * @param handle Context to check.
     * @return True if handle is ancestor of this context, false otherwise.
     */
    public boolean implies(ContextHandle handle) {
        return getAllImplied().contains(handle);
    }
    
}
