package com.faradice.faraframe.context;
/**
 * <H1> IContextObject - Top level interface for framework components. </H1>
 *  
 * This interface defines two aspects of a framework component.  First of all,
 * it lives in a certain context.  This can be used to access services and other
 * components sharing or shared through this context.
 * 
 * Second, it has a live cycle:
 * 1. Component is constructed with the empty constructor (which must exist and be public).
 * 2. Context is passed to component using setContext().
 * 3. Component is activated by init().
 * 4. any other methods can be called.
 * 5. Component is permanently deactivated by destroy().  No further methods will be called.
 * 
 * 
 * <p> Author Vilmundur Palmason
 * <p> Created Oct 28, 2003
 * @version  $Id: IContextObject.java,v 1.1 2007/06/01 14:41:11 gudmfr Exp $
 */
public interface IContextObject {
    
    /**
     * This method will be called (only once) some time after construction and before any other
     * methods are called.
     * @param ctx  Main context this component lives in.
     */
    public void setContext(ContextHandle ctx);
    
    /**
     * Perform initialization.
     * This method will be called (only once) some time after the context has been set.
     * After this method has completed, any other methods can be called. 
     */
    public void init();
        
        
    /**
     * Cleanup any resources.
     * This method will be called (only once) to indicate that this component will not be used
     * again.
     */
    public void destroy();
}
