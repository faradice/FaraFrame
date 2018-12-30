/*
 * Copyright (c) 2009 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */
package com.faradice.faraframe.log;

import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Log wraps the standard Java logging facility, providing simpler API and better performing. The API is printf like,
 * i.e. it expects pattern and varargs that are passed into String.format function to create the log string The delayed
 * string concatenation is faster performing in case nothing is logged. 
 * 
 * Casual logging can be done by Log.global.fine (or finer, info, etc). 
 * Serious loggers create there own instance to use i.e. 
 * Log log = new Log(X.class);  // same as new Log(X.class.getPackage().getName());
 * log.fine (or finer, info, etc)
 * 
 * There is also a simple timing facility for the log, i.e. by call startTiming a stop watch starts. Calls to time after that will report
 * the time elapsed since startTiming was called, using the info level.
 * @version $Id: Log.java,v 1.12 2011/01/19 12:38:04 gudmfr Exp $
 */
public final class Log {
    /**
     * The global logger used for casual logging
     */
    public static final Log echo = new Log("com.decode");

    private final java.util.logging.Logger logger; // The java logger used
    private long startTime;

    /**
     * Construct Log for the specified subsystem
     * @param subsystem The name of the subsystem
     */
    public Log(String subsystem) {
        logger = Logger.getLogger(subsystem);
    }
    /**
     * Construct Log for the specified subsystem
     * @param clazz The name of the class to log for
     */
    public Log(Class<?> clazz) {
        this(clazz.getName());
    }
    /**
     * Construct Log for the specified subsystem
     * @param pack The name of the Package to log for
     */
    public Log(Package pack) {
        this(pack.getName());
    }
    
    /**
     * Log using WARNING level
     * @param th The throwable that is resulting the log
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void warning(Throwable th, String pattern, Object... args) {
        log(th, Level.WARNING, pattern, args);
    }
    /**
     * Log using SEVERE level
     * @param th The throwable that is resulting the log
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void severe(Throwable th, String pattern, Object... args) {
        log(th, Level.SEVERE, pattern, args);
    }
    
    /**
     * Log the exception
     * @param th
     */
    public void fine(Throwable th) {
    	log(th, Level.FINE, "");
    }


    /**
     * Log using INFO level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void info(String pattern, Object... args) {
        log(Level.INFO, pattern, args);
    }
    /**
     * Log using WARNING level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void warning(String pattern, Object... args) {
        log(Level.WARNING, pattern, args);
    }
    /**
     * Log using SEVERE level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void severe(String pattern, Object... args) {
        log(Level.SEVERE, pattern, args);
    }
    /**
     * Log using FINE level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void fine(String pattern, Object... args) {
        log(Level.FINE, pattern, args);
    }
    /**
     * Log using FINER level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void finer(String pattern, Object... args) {
        log(Level.FINER, pattern, args);
    }
    /**
     * Log using FINEST level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void finest(String pattern, Object... args) {
        log(Level.FINEST, pattern, args);
    }
    /**
     * Log using CONFIG level
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void config(String pattern, Object... args) {
        log(Level.CONFIG, pattern, args);
    }
    
    /**
     * Set the level on which all messages of that level and higher are logged.
     * @param level The minimum level to log
     */
    public void setMinimumLogLevel(Level level) {
        logger.setLevel(level);
    }
    
    /**
     * Query for the minimum log level
     * @return The level
     */
    public Level getMinimumLogLevel() {
        return logger.getLevel() != null ? logger.getLevel() : Level.INFO;
    }
    
    /**
     * Start a stopwatch timing measurements, calls to time will use this to calculate time elapsed
     */
    public void startTiming() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Log using INFO level prefixing the message with the time elapsed since timing started
     * @param pattern The pattern to use, see String.format
     * @param args The variable arguments to populate the pattern with, see String.format
     */
    public void time(String pattern, Object... args) {
        info(formatTime(startTime) + pattern, args);
    }

    /**
     * @param start The start time to format time relative to
     * @return A formatted time string of time elapsed from start
     */
    public static String formatTime(long start) {
        long diff = System.currentTimeMillis()-start;
        int hours = (int)(diff/(1000*60*60));
        int hdiff = hours*1000*60*60;
        int min = (int)((diff-hdiff)/(1000*60));
        int mdiff = min*1000*60;
        int sec = (int)((diff-hdiff-mdiff)/1000);
        int ms = (int)((diff-hdiff-mdiff-(sec*1000)));
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours+"h");
        }
        if (hours > 0 || min > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(min+"m ");
        }
        sb.append(sec+"s " + ms + "ms) ");
        return '(' + sb.toString();
    }
    
    /**
     * General log call
     * @param level The level to log
     * @param pattern The pattern to use, see String.format. If args is null, String.format() will not be applied to the pattern string.
     * @param args The variable arguments to populate the pattern with, see String.format
    */
    public void log(Level level, String pattern, Object... args) {        
    	if (logger.isLoggable(level)) {        	
    		assert pattern != null || args.length == 0;
            logger.log(level, args.length > 0 ? String.format(pattern, args) : (pattern == null ? "" : pattern));
        }
    }
    

    /**
     * General log call
     * @param level The level to log
     * @param th The throwable that is resulting the log
     * @param pattern The pattern to use, see String.format. If args is null, String.format() will not be applied to the pattern string.
     * @param args The variable arguments to populate the pattern with, see String.format
    */
    public void log(Throwable th, Level level, String pattern, Object... args) {
        if (logger.isLoggable(level)) {
            assert pattern != null || args.length == 0;
            logger.log(level, args.length > 0 ? String.format(pattern, args) : (pattern == null ? "" : pattern), th);
        }
    }
}