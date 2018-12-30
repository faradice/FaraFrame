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
 * StatusListener is a simple notfication callbac
 * @version $Id: StatusListener.java,v 1.3 2005/08/17 09:54:06 gudmfr Exp $
 */
public interface StatusListener {
    /**
     * Notify the update of a progress detailing object name and progress
     * @param percent The percentage (0..100) done of the work
     * @param object_name The name of the object being worked on.
     */
    public void update(int percent, String object_name);

    /**
     * Pusth the provided path on a stack of paths to process
     * @param path The path string to push on top of the path stack
     */
    public void pushPath(String path);

    /**
     * Pop the current to path from the stack
     */
    public void popPath();

    /**
     * Set the name of the current operation
     * @param op_name The operation name
     */
    public void setOperationName(String op_name);
}
