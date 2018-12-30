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
 * Refactored:  17.3.2005  
 */
import java.util.EventObject;
import java.util.logging.LogRecord;

/**
 *
 * @author hii
 * @version $Revision: 1.2 $ $Date: 2005/11/21 14:18:48 $
 */
public class LogChangeEvent extends EventObject {
    private static final long serialVersionUID = 3257284721296946231L;
	
    /**
     * Log record added
     */
    public static final int RECORD_ADDED = 1;

    /**
     * Log record removed
     */
    public static final int RECORD_REMOVED = 2;

    private int eventType = -1;
    private LogRecord record;

    /**
     * Logs 
     * @param source
     * @param eventType
     * @param record
     */
    public LogChangeEvent(Object source, int eventType, LogRecord record) {
        super(source);
        this.eventType = eventType;
        this.record = record;
    }

    /**
     * Returns the EventType
     * @return int
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * Returns the LogRecord
     * @return LogRecord
     */
    public LogRecord getRecord() {
        return record;
    }

} // end class LogChangeEvent

/**
 * $Log: LogChangeEvent.java,v $
 * Revision 1.2  2005/11/21 14:18:48  garpur
 * removal of warnings
 *
 * Revision 1.1  2005/03/18 10:44:58  oskarh
 * logging classes refatored from generic.jar
 *
 * Revision 1.1  2003/06/30 15:59:07  hii
 * fix DecodeTable and DCMenu and add log stuff
 *
 */
