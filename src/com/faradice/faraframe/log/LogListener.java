package com.faradice.faraframe.log; 
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
 * Date created: Jun 26, 2003
 */

/**
 *
 * @author hii
 * @version $Revision: 1.1 $ $Date: 2005/03/18 10:44:58 $
 */
public interface LogListener {

    /**
     * Logg has changed
     * @param evt
     */
    public void loggChange(LogChangeEvent evt);

} // end class LogListener

/**
 * $Log: LogListener.java,v $
 * Revision 1.1  2005/03/18 10:44:58  oskarh
 * logging classes refatored from generic.jar
 *
 * Revision 1.1  2003/06/30 15:59:07  hii
 * fix DecodeTable and DCMenu and add log stuff
 *
 */
 
