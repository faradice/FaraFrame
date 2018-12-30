package com.faradice.faraframe.util; 
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
 * Date created: Nov 20, 2002
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A Simple PrintStream that delegates its logic to two other PrintStreams
 * @author josep
 * @version $Revision: 1.3 $ $Date: 2011/04/15 10:00:48 $
 */
public class DeletgatePrintStream extends PrintStream {
    private PrintStream m_oOut1;
    private PrintStream m_oOut2;
    private boolean m_bError = false;

    /**
     * Takes two printstreams
     * @param out1 The First
     * @param out2 The Second
     */
    public DeletgatePrintStream( PrintStream out1, PrintStream out2 ) {
        super( new ByteArrayOutputStream( ) );
        m_oOut1 = out1;
        m_oOut2 = out2;
    }

    @Override
    public void flush() {
        m_oOut1.flush( );
        m_oOut2.flush( );
    }

    @Override
    public void close() {
        m_oOut1.close( );
        m_oOut2.close( );
    }

    @Override
    public boolean checkError() {
        return m_oOut1.checkError( )  || m_oOut2.checkError( ) || m_bError;
    }

    @Override
    protected void setError( ) {
        m_bError = true;
    }

    @Override
    public void write(int b) {
        m_oOut1.write( b );
        m_oOut2.write( b );
    }

    @Override
    public void write(byte buf[], int off, int len) {
        m_oOut1.write( buf, off, len );
        m_oOut2.write( buf, off, len );
    }

    @Override
    public void print(boolean b) {
        m_oOut1.print( b );
        m_oOut2.print( b );
    }

    @Override
    public void print(char c) {
        m_oOut1.print( c );
        m_oOut2.print( c );
    }

    @Override
    public void print(int i) {
        m_oOut1.print( i );
        m_oOut2.print( i );
    }

    @Override
    public void print(long l) {
        m_oOut1.print( l );
        m_oOut2.print( l );
    }

    @Override
    public void print(float f) {
        m_oOut1.print( f );
        m_oOut2.print( f );
    }

    @Override
    public void print(double d) {
        m_oOut1.print( d );
        m_oOut2.print( d );
    }

    @Override
    public void print(char s[]) {
        m_oOut1.print( s );
        m_oOut2.print( s );
    }

    @Override
    public void print(String s) {
        m_oOut1.print( s );
        m_oOut2.print( s );
    }

    @Override
    public void print(Object obj) {
        m_oOut1.print( obj );
        m_oOut2.print( obj );
    }

    @Override
    public void println() {
        m_oOut1.println( );
        m_oOut2.println( );
    }

    @Override
    public void println(boolean x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(char x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(int x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(long x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(float x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(double x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(char x[]) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(String x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }

    @Override
    public void println(Object x) {
        m_oOut1.println( x );
        m_oOut2.println( x );
    }
}