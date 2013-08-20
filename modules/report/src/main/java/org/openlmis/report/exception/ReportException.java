/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.exception;

/**
 */
public class ReportException extends RuntimeException {

    private String message;

    public ReportException(String message){
        super(message);
        this.message = message;
    }

}
