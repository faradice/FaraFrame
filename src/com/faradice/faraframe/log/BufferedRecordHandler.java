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
 * Refactored 17.3.2005 
 */

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.faradice.faraframe.util.DeletgatePrintStream;

/**
 * Log handler
 * See java.util.logging.Handler for further details    
 * @author hii
 * @version $Id: BufferedRecordHandler.java,v 1.7 2009/05/18 14:39:24 gudmfr Exp $
 */
public class BufferedRecordHandler extends Handler {

	/**
	 * Level for normal logging
	 */
	public static Level LEVEL_OUT;

	/**
	 * Level for logging of errors
	 */
    public static Level LEVEL_ERR;

    private int recordBufferSize;
    private TextRecordOutputStream errorStream = null;
    private TextRecordOutputStream outputStream = null;
    private ArrayList<LogListener> loggListeners = new ArrayList<LogListener>(1);
    private ArrayList<LogRecord> recordList;

    /**
     * Constructor 
     * See java.util.logging.Handler for further details    
     */
    public BufferedRecordHandler() {
        LogManager manager = LogManager.getLogManager();
        String cname = BufferedRecordHandler.class.getName();

        // Get the BufferedRecordHandler level from logging conf -------------------------------------------
        String stringLevel = manager.getProperty(cname + ".level");
        try {
            this.setLevel(Level.parse(stringLevel.trim()));
        } catch (Exception ex) {
            this.setLevel(Level.ALL);
        }

        // Get the BufferedRecordHandler record buffer size from logging conf -----------------------------
        String stringRecordBufferSize = manager.getProperty(cname + ".buffer_size");
        try {
            recordBufferSize = Integer.parseInt(stringRecordBufferSize.trim());
        } catch (Exception ex) {
            recordBufferSize = 5000;
        }
        recordList = new ArrayList<LogRecord>(recordBufferSize);

        // Get the BufferedRecordHandler err_log from logging conf ----------------------------------------
        String stringErrStreamPrefix = manager.getProperty(cname + ".err_prefix");
        if (stringErrStreamPrefix != null && stringErrStreamPrefix.length() > 0) {
            stringErrStreamPrefix = stringErrStreamPrefix.trim() + " ";
        } else {
            stringErrStreamPrefix = "";
        }

        Level errStreamLevel = null;
        try {
            errStreamLevel = Level.parse(manager.getProperty(cname + ".err_level").trim());
        } catch (Exception ex) {
            errStreamLevel = Level.WARNING;
        }
        LEVEL_ERR = new StreamLevel(errStreamLevel, "_ERR");

        errorStream = new TextRecordOutputStream(LEVEL_ERR, stringErrStreamPrefix);
        errorStream.setEnabled(true);
        System.setErr(new DeletgatePrintStream(new PrintStream(errorStream), System.err));

        // Get the BufferedRecordHandler out_log from logging conf ----------------------------------------

        String stringOutStreamPrefix = manager.getProperty(cname + ".out_prefix");
        if (stringOutStreamPrefix != null && stringOutStreamPrefix.length() > 0) {
            stringOutStreamPrefix = stringOutStreamPrefix.trim() + " ";
        } else {
            stringOutStreamPrefix = "";
        }

        Level outStreamLevel = null;
        try {
            outStreamLevel = Level.parse(manager.getProperty(cname + ".out_level").trim());
        } catch (Exception ex) {
            outStreamLevel = Level.INFO;
        }
        LEVEL_OUT = new StreamLevel(outStreamLevel, "_OUT");

        outputStream = new TextRecordOutputStream(LEVEL_OUT, stringOutStreamPrefix);
        outputStream.setEnabled(true);
        System.setOut(new DeletgatePrintStream(new PrintStream(outputStream), System.out));
    }

    /**
     * See java.util.logging.Handler for further details    
     */
    @Override
    public void publish(LogRecord record) {

        // Check if record should be logged.
        if (!isLoggable(record)) {
            return;
        }

        // If buffer full remove oldest record and notify listeners.
        if (recordList.size() >= recordBufferSize) {
            LogRecord tempRecord = recordList.get(0);
            recordList.remove(0);
            try {
                for (int i = 0; i < loggListeners.size(); i++) {
                    loggListeners.get(i).loggChange(new LogChangeEvent(this, LogChangeEvent.RECORD_ADDED, tempRecord));
                }
            } catch (Exception ex) {
                reportError(null, ex, ErrorManager.WRITE_FAILURE);
            }
        }

        // Store record in buffer and notify listeners of new record
        recordList.add(record);
        if (loggListeners.size() > 0) {
            try {
                for (int i = 0; i < loggListeners.size(); i++) {
                    loggListeners.get(i).loggChange(new LogChangeEvent(this, LogChangeEvent.RECORD_ADDED, record));
                }
            } catch (Exception ex) {
                reportError(null, ex, ErrorManager.WRITE_FAILURE);
            }
        }

    }

    /**
     * See java.util.logging.Handler for further details    
     */
    @Override
    public void flush() {
        recordList.clear();
    }

    @Override
    public void close() throws SecurityException {
        flush();
    }

    /**
     * See java.util.logging.Handler for further details    
     * @return the records that have been logged.
     */
    public LogRecord[] getLoggRecords() {
        return recordList.toArray(new LogRecord[0]);
    }

    /**
     * Add the given listener to our listener list.    
     * @param listener the log listener that will listen to us.
     */
    public void addLoggListener(LogListener listener) {
        loggListeners.add(listener);
    }

    /**
     * Remove the given listener from our listener list.    
     * @param listener the log listener that will be removed.
     */
    public void removeLoggListener(LogListener listener) {
        loggListeners.remove(listener);
    }

    private class TextRecordOutputStream extends OutputStream {

        private Level level;
        private String prefix;
        private boolean isEnabled = false;

        TextRecordOutputStream(Level level, String prefix) {
            this.level = level;
            this.prefix = prefix;
        }

        void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        @Override
        public void write(int b) {
            /* Nothing to do*/
        }

        @Override
        public void write(byte b[]) {
            if (isEnabled) {
                String message = new String(b);
                if (message.trim().length() > 0) {
                    publish(new LogRecord(level, prefix + message));
                }
            }
        }

        @Override
        public void write(byte b[], int off, int len) {
            if (isEnabled) {
                String message = new String(b, off, len);
                if (message.trim().length() > 0) {
                    publish(new LogRecord(level, prefix + message));
                }
            }
        }

    } // end inner class TextOutputStream

    private static class StreamLevel extends Level {
        private static final long serialVersionUID = 3618136732311107381L;

        StreamLevel(Level baseLevel, String sufix) {
            super(baseLevel.getLocalizedName() + sufix, baseLevel.intValue() + 1);
        }

    }

}
