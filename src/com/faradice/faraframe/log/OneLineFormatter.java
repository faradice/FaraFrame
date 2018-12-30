package com.faradice.faraframe.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for oneline log entries 
 */
public class OneLineFormatter extends Formatter {
    static private final HashSet<String> ignoredCalls = new HashSet<String>();
    static {
        ignoredCalls.add("com.faradice.faraframe.log.Log");
        ignoredCalls.add("org.apache.commons.logging.impl.Jdk14Logger");
        ignoredCalls.add("java.util.logging.Logger");
        ignoredCalls.add("com.decode.db.services.DbTransactProxy");
    }
    @Override public String format(LogRecord rec) {
        String s = String.format("%1$s [%2$tY-%2$tm-%2$td %2$tH:%2$tM:%2$tS %3$s %4$s]\n",
            rec.getMessage(), rec.getMillis(), inferCaller(), rec.getLevel());
        if (rec.getThrown() != null) {
            StringWriter sw = new StringWriter();
            try {
                PrintWriter pw = new PrintWriter(sw);
                rec.getThrown().printStackTrace(pw);
                pw.close();
                return s + sw.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try { sw.close(); } catch (Exception ex) { /* nothing to do */ }
            }
        }
        return s;
    }

    private String inferCaller() {
        // Get the stack trace.
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        String caller = inferCaller(stack, "java.util.logging.Logger");//"com.faradice.faraframe.log.Log");
        if (caller.startsWith("com.decode")) {
            return caller.substring(11);
        } 
        return caller;
    }
    private String inferCaller(StackTraceElement[] stack, String assumedLogClass) {
        // First, search back to a method in the Logger class.
        int ix = 0;
        while (ix < stack.length) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals(assumedLogClass)) {
            break;
            }
            ix++;
        }
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            // Do not report ignored classes and dynamic proxies as the start calling frame
            if (!ignoredCalls.contains(cname) && !cname.startsWith("$Proxy")) {
               // We've found the relevant frame.
                return cname + "." + frame.getMethodName();
            }
            ix++;
        }
        return "";
    }
}