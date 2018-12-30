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
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * A SimpleFormatter that prints one line instead of two.
 * 
 * @version $Id: SimpleOneLineFormatter.java,v 1.9 2008/03/19 12:32:53 gudmfr Exp $
 */

public class SimpleOneLineFormatter extends Formatter {

    private Date date = new Date();

    // private final static String format = "{0}: {1,date,yyyy.MM.dd}
    // {1,time,HH:mm:ss.SSS} - {2}";

    private final static String format = "{0}| {1,date,MM.dd} {1,time,HH:mm:ss} | {2} | {3}";

    private MessageFormat formatter = new MessageFormat(format);
    private Object args[] = new Object[4];

    private String lineSeparator = System.getProperty("line.separator");

    /**
     * Construct the SimpleOneLineFormatter
     */
    public SimpleOneLineFormatter() {
        // Nothing to do
    }

    @Override
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        // String x;
        String level = appendSpace(record.getLevel().getLocalizedName(), 7);
        args[0] = level;
        date.setTime(record.getMillis());
        args[1] = date;
        args[2] = appendSpace(cutBeginning(record.getSourceClassName() + "."
                + record.getSourceMethodName(), 30), 30);
        args[3] = formatMessage(record);

        formatter.format(args, sb, null);

        sb.append(lineSeparator);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
             // Nothing to do
            }
        }

        return sb.toString();
    }

    /**
     * Method appendSpace: apends white spaces to a string
     */
    private String appendSpace(String str, int length) {
        StringBuffer s = new StringBuffer(str);
        while (s.length() < length)
            s.append(' ');
        return s.toString();
    }

    /**
     * Cuts of the beginning of a string if it is longer that a certain length
     * 
     * @param s
     *            The string
     * @param maxLength
     * @return a new String that is not longer than maxLength
     */
    private String cutBeginning(String s, int maxLength) {
        if (s.length() < maxLength) {
            return s;
        }
        return s.substring(s.length() - maxLength);
    }

    /**
     * Localize and format the message string from a log record. This method is provided
     * as a convenience for Formatter subclasses to use when they are performing
     * formatting.
     * <p>
     * The message string is first localized to a format string using the record's
     * ResourceBundle. (If there is no ResourceBundle, or if the message key is not found,
     * then the key is used as the format string.) The format String uses java.text style
     * formatting.
     * <ul>
     * <li>If there are no parameters, no formatter is used.
     * <li>Otherwise, if the string contains "{0" then java.text.MessageFormat is used to
     * format the string.
     * <li>Otherwise no formatting is performed.
     * </ul>
     * <p>
     * 
     * @param record
     *            the log record containing the raw message
     * @return a localized and formatted message
     */
    @Override
    public synchronized String formatMessage(LogRecord record) {
        String msgFormat = record.getMessage();
        java.util.ResourceBundle catalog = record.getResourceBundle();
        if (catalog != null) {
            try {
                msgFormat = catalog.getString(record.getMessage());
            } catch (java.util.MissingResourceException ex) {
                // Drop through. Use record message as format
                msgFormat = record.getMessage();
            }
        }
        // Do the formatting.
        try {
            Object parameters[] = record.getParameters();
            if (parameters == null || parameters.length == 0) {
                // No parameters. Just return format string.
                return msgFormat;
            }
            // Is is a java.text style format?
            if (msgFormat.indexOf("{0") >= 0) {
                return java.text.MessageFormat.format(msgFormat, parameters);
            }
            return msgFormat;

        } catch (Exception ex) {
            // Formatting failed: use localized format string.
            return msgFormat;
        }
    }

}
