/* 
 * Copyright (c) 2007 deCODE Genetics Inc. 
 * All Rights Reserved. 
 * 
 * This software is the confidential and proprietary information of 
 * deCODE Genetics Inc. ("Confidential Information"). You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with deCODE. 
 * gudmfr 22.10.2007
 */
package com.faradice.faraframe.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copies standard output and error of subprocesses to standard output and
 * error of the parent process.
 */
public class PumpStreamHandler  {

    private Thread inputThread;
    private Thread errorThread;

    private OutputStream out, err;

    /**
     * Construct PumpStreamHandler
     * @param out
     * @param err
     */
    public PumpStreamHandler(OutputStream out, OutputStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * 
     * Construct PumpStreamHandler
     * @param outAndErr
     */
    public PumpStreamHandler(OutputStream outAndErr) {
        this(outAndErr, outAndErr);
    }

    /**
     * 
     * Construct PumpStreamHandler
     */
    public PumpStreamHandler() {
        this(System.out, System.err);
    }

    /**
     * 
     * @param is
     */
    public void setProcessOutputStream(InputStream is) {
        createProcessOutputPump(is, out);
    }

    /**
     * 
     * @param is
     */
    public void setProcessErrorStream(InputStream is) {
        createProcessErrorPump(is, err);
    }

    /**
     * 
     * @param os
     */
    public void setProcessInputStream(@SuppressWarnings("unused") OutputStream os) {
        // Nothing to do
    }


    /**
     * 
     */
    public void start() {
        inputThread.start();
        errorThread.start();
    }

    /**
     * 
     */
    public void stop() {
        try {
            inputThread.join();
        } catch (InterruptedException e) {/* Nothing to do*/}
        try {
            errorThread.join();
        } catch (InterruptedException e) {/* Nothing to do*/}
        try {
            err.flush();
        } catch (IOException e) {/* Nothing to do*/}
        try {
            out.flush();
        } catch (IOException e) {/* Nothing to do*/}
    }

    protected OutputStream getErr() {
        return err;
    }

    protected OutputStream getOut() {
        return out;
    }

    protected void createProcessOutputPump(InputStream is, OutputStream os) {
        inputThread = createPump(is, os);
    }

    protected void createProcessErrorPump(InputStream is, OutputStream os) {
        errorThread = createPump(is, os);
    }


    /**
     * Creates a stream pumper to copy the given input stream to the
     * given output stream.
     */
    protected Thread createPump(InputStream is, OutputStream os) {
        final Thread result = new Thread(new StreamPumper(is, os));
        result.setDaemon(true);
        return result;
    }
    
    static class StreamPumper implements Runnable {

        // TODO: make SIZE and SLEEP instance variables.
        // TODO: add a status flag to note if an error occured in run.

        private static final int SLEEP = 5;
        private static final int SIZE = 128;
        private InputStream is;
        private OutputStream os;
        private boolean finished;

        /**
         * Create a new stream pumper.
         *
         * @param is input stream to read data from
         * @param os output stream to write data to.
         */
        public StreamPumper(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
        }


        /**
         * Copies data from the input stream to the output stream.
         *
         * Terminates as soon as the input stream is closed or an error occurs.
         */
        public void run() {
            synchronized (this) {
                // Just in case this object is reused in the future
                finished = false;
            }

            final byte[] buf = new byte[SIZE];

            int length;
            try {
                while ((length = is.read(buf)) > 0) {
                    os.write(buf, 0, length);
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException e) { /* Nothing to do*/ }
                }
            } catch (Exception e) {
                // ignore
            } finally {
                synchronized (this) {
                    finished = true;
                    notify();
                }
            }
        }

        /**
         * Tells whether the end of the stream has been reached.
         * @return true is the stream has been exhausted.
         **/
        public synchronized boolean isFinished() {
            return finished;
        }

        /**
         * This method blocks until the stream pumper finishes.
         * @throws InterruptedException 
         * @see #isFinished()
         **/
        public synchronized void waitFor() throws InterruptedException {
            while (!isFinished()) {
                wait();
            }
        }
    }    
}
