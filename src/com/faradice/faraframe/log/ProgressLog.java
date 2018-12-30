/* 
 * Copyright (c) 2007 deCODE Genetics Inc. 
 * All Rights Reserved. 
 * 
 * This software is the confidential and proprietary information of 
 * deCODE Genetics Inc. ("Confidential Information"). You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with deCODE. 
 * gudmfr 17.8.2007
 */
package com.faradice.faraframe.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.faradice.faraframe.config.SystemConfig;
import com.faradice.faraframe.util.Files;


/**
 * ProgressLog allows collecting log entries with support for timing progress. It
 * works with the following concepts:
 * 1. Log - a collections of strings, one each line in a log
 * 2. Time track - track time duration from log start, i.e. timed event
 * 3. Mark duration - collect multiple marks and allow adding a one log entry
 * @version $Id: ProgressLog.java,v 1.4 2010/10/18 09:12:09 gudmfr Exp $
 */
public class ProgressLog {
    private ArrayList<String> logNotes; // A formal log to collect
    private ArrayList<String> marks; // A set of marks collected
    private long start; // The time when the timers where last reset
    private long lastTimed; // The start of the current interval
    private static final int msInSec = 1000;
    private static final int msInMin = msInSec * 60;
    private static final int msInHour = msInMin * 60;
    
    /**
     * Create a new ProgresLog object. Note the resetTimers must be called before time note usage.
     */
    public ProgressLog() {
        logNotes = new ArrayList<String>();
        marks = new ArrayList<String>();
    }

    /**
     * Create a new ProgressLog object ready to be used for timed notes.
     * @param initialNote The initial note to add
     */
    public ProgressLog(String initialNote) {
        this();
        resetTimers(initialNote);
    }
    
    /**
     * Reset the logger
     */
    public void reset() {
        logNotes.clear();
        marks.clear();
    }
    
    /**
     * Reset timers so that timing starts now
     * @param note The note to attach to this event
     */
    public void resetTimers(String note) {
        logNotes.add(note + "[" + new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").format(new Date()) + "]");
        start = lastTimed = System.currentTimeMillis();
    }
    
    /**
     * Add a timed note the log and reset the last timed log to now
     * @param text The log note (to which a time will be added using the internal timer since last log)
     */
    public void logTime(String text) {
        long t = System.currentTimeMillis();
        addTimedLogNote(text, t - lastTimed);
        lastTimed = t;
    }
    
    /**
     * Log the specified exception to the log
     * @param ex
     */
    public void logException(Throwable ex) {
    	logNotes.add(ex.getMessage());
    	for (StackTraceElement se : ex.getStackTrace()) {    		
    		logNotes.add(se.toString());
    	}
    }
    
    /**
     * Release the current marks and add to the log
     */
    public void logMarks() {
        logNotes.add(releaseMarks());
    }
    
    /**
     * Add a timed note the log
     * @param text The log note (to which a time will be added using the internal timers from last reset)
     */
    public void logTotalTime(String text) {
        addTimedLogNote(text, System.currentTimeMillis() - start);
    }
    
    
    /**
     * Add a timed note the log
     * @param text The log note (to which a time will be added)
     * @param time The time to report
     */
    public void addTimedLogNote(String text, long time) {
        String note = text + " [Time " + format(time) + "]";
        logNotes.add(note);
    }
    
    /**
     * Mark the current duration
     * @param text The text to prefix the duration with
     */
    public void mark(String text) {
        marks.add(text + (format(System.currentTimeMillis() - lastTimed)));
    }
    
    /**
     * Create a new mark
     */
    public void newMark() {
        marks.clear();
        lastTimed = System.currentTimeMillis();
    }
    
    /**
     * Get a message for the timed marks, set the last timed event to now.
     * @return The collected marks as one line string message,
     */
    public String releaseMarks() {
        StringBuilder sb = new StringBuilder();
        for (String s : marks) {
            sb.append(s);
            sb.append(", ");
        }
        sb.append("total " + format(System.currentTimeMillis() - lastTimed));
        return sb.toString();
    }
    
    /**
     * Format the specified duration message
     * @param time The duration to report
     * @return A string representation of the specified time duration
     */
    public static String format(long time) {
        final long h = time / msInHour;
        final long m = (time % msInHour) / msInMin;
        final long s = ((time % msInHour) % msInMin) / msInSec;
        StringBuilder sb = new StringBuilder();
        if (h > 0) {
            sb.append(h + "h:");
        }
        if (m > 0 || sb.length() > 0) {
            sb.append(m + "m:");
        }
        sb.append(s + "s");
        return sb.toString();
    }

    /**
     * Write the log to the specified print writer
     * @param out The print writer to use
     */
    public void write(PrintWriter out) {
        for (String s : logNotes) {
            out.println(s);
        }
    }
    
    /**
     * Write the log to a running weekday log
     * @param property The root directory containing the weekday log
     */
    public void writeWeekdayLog(String property) {
    	final String path = SystemConfig.getFilePath(property);
        if (path == null || path.trim().length() == 0) {
            System.err.println(property + " not configured. No log will be written to the file system!");
        } else {
            final Date now = new Date();
            try {
	            final FileOutputStream outstream = Files.createWeekdayOutputStream(path, now);
	            final PrintWriter out = new PrintWriter(outstream);
	            try {
	                write(out);
	
	                SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
	                out.println("Run completed at " + tf.format(now));
	            } finally {
	                // Close everything
	                out.println();
	                out.flush();
	                out.close();
	            }
            } catch (FileNotFoundException ex) {
            	ex.printStackTrace();
            	System.err.println("Could not create log file at " + path);
            }
        }    	
    }
}
