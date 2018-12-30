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

package com.faradice.faraframe.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Everything written to the logging output stream is written to its logger.
 *
 * @author gisli
 * @version $Revision: 1.1 $ $Date: 2008/09/08 11:24:50 $
 */

/**
 * Helper classes
 */
public class LoggingOutputStream extends OutputStream {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean skip = false;
    private Level level = Level.WARNING;
    private Logger logger = null;
    private String prefix = null;

    /**
     * Creates a new instance of this class.
     *
     * @param logger
     * @param level         loglevel used to log data written to this stream.
     */
    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    /**
     * Creates a new instance of this class.
     *
     * @param logger
     * @param level      loglevel used to log data written to this stream.
     * @param prefix
     */
    public LoggingOutputStream(Logger logger, Level level, String prefix) {
        this.logger = logger;
        this.level = level;
        this.prefix = prefix;
    }


    /**
     * Write the data to the buffer and flush the buffer, if a line
     * separator is detected.
     *
     * @param cc data to log (byte).
     */
    @Override
    public void write(int cc) {
        final byte c = (byte) cc;
        if ((c == '\n') || (c == '\r')) {
            if (!skip) {
              processBuffer();
            }
        } else {
          buffer.write(cc);
        }
        skip = (c == '\r');
    }


    /**
     * Converts the buffer to a string and sends it to <code>processLine</code>
     */
    protected void processBuffer() {
        processLine(buffer.toString());
        buffer.reset();
    }

    /**
     * Logs a line to the log system of ant.
     *
     * @param line the line to log.
     */
    protected void processLine(String line) {
        processLine(line, level);
    }

    /**
     * Logs a line to the log system of ant.
     *
     * @param line the line to log.
     */
    protected void processLine(String line, Level theLevel) {
        if (logger != null && theLevel != null) {
            logger.log(theLevel, prefix != null ? prefix + " " + line  : line);
        }
    }


    /**
     * Writes all remaining
     */
    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }

    @Override
    public void flush() {
        if (buffer.size() > 0) {
          processBuffer();
        }
    }

    /**
     * Query for the current logging level
     * @return The current logging level
     */
    public Level getLoggingLevel() {
        return level;
    }

    /**
     * Query for the current prefix
     * @return The current prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the current prefix
     * @param prefix The prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


}


/*
 * $Log: LoggingOutputStream.java,v $
 * Revision 1.1  2008/09/08 11:24:50  gudmfr
 * Reduce dependency on tools_libext
 *
 * Revision 1.3  2005/06/06 11:52:15  gudmfr
 * Remove Warnings
 *
 * Revision 1.2  2005/02/07 15:41:54  gudmfr
 * Added comments
 *
 * Revision 1.1  2005/02/07 09:14:47  gudmfr
 * Removal of general library
 *
 * Revision 1.4  2003/10/24 08:31:17  gudmfr
 * Removed throws decl. of exception that are never thrown
 *
 * Revision 1.3  2003/09/25 14:11:33  gudmfr
 * organize imports
 *
 * Revision 1.2  2003/07/01 03:00:25  gisli
 * Added extra data validation (null check)
 *
 * Revision 1.1  2003/06/23 11:44:32  gisli
 * Moved logging util to general
 *
 * Revision 1.1  2003/06/23 11:07:10  gisli
 * Refactoring of the Ore system.
 *
 */