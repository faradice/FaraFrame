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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pumps the std out and std into loggers.
 *
 * @author gisli
 * @version $Revision: 1.1 $ $Date: 2008/09/08 11:24:50 $
 */

public class StdStreamLogPumper extends PumpStreamHandler {
    /**
     * Creates a new instance of this class.
     *
     * @param logger    logger for both std out and err.
     * @param outLevel  the loglevel used to log standard output
     * @param errLevel  the loglevel used to log standard error
     */
    public StdStreamLogPumper(Logger logger, Level outLevel, Level errLevel) {
        this(logger, outLevel, logger, errLevel);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param stdOutlogger  logger for std out.
     * @param outLevel      the loglevel used to log standard output
     * @param stdErrLogger  logger for std err.
     * @param errLevel      the loglevel used to log standard error
     */
    public StdStreamLogPumper(Logger stdOutlogger, Level outLevel, Logger stdErrLogger, Level errLevel) {
        this(stdOutlogger, outLevel, null, stdErrLogger, errLevel, null);
    }

    /**
     * Creates a new instance of this class.
     *
     * @param stdOutlogger  logger for std out.
     * @param outlevel      the loglevel used to log standard output
     * @param outPrefix 
     * @param stdErrLogger  logger for std err.
     * @param errlevel      the loglevel used to log standard error
     * @param errPrefix 
     */
    public StdStreamLogPumper(Logger stdOutlogger, Level outlevel, String outPrefix, Logger stdErrLogger, Level errlevel, String errPrefix) {
        super(new LoggingOutputStream(stdOutlogger, outlevel, outPrefix),
              new LoggingOutputStream(stdErrLogger, errlevel, errPrefix));
    }

    /**
     * Stop the logging.
     */
    @Override
    public void stop() {
        super.stop();
        try {
            getErr().close();
            getOut().close();
        } catch (IOException e) {
            // plain impossible
            throw new RuntimeException(e);
        }
    }


}

/*
 * $Log: StdStreamLogPumper.java,v $
 * Revision 1.1  2008/09/08 11:24:50  gudmfr
 * Reduce dependency on tools_libext
 *
 * Revision 1.4  2007/10/22 14:33:12  gudmfr
 * Removed dependency on classes with in ant
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
 * Revision 1.2  2003/09/25 14:11:33  gudmfr
 * organize imports
 *
 * Revision 1.1  2003/06/26 14:36:56  gisli
 * New
 *
 * Revision 1.1  2003/06/26 14:33:46  gisli
 * Moved from oreimpl to orealg to avoid dependencies
 *
 * Revision 1.2  2003/06/23 11:44:32  gisli
 * Moved logging util to general
 *
 * Revision 1.1  2003/06/23 11:07:10  gisli
 * Refactoring of the Ore system.
 *
 */