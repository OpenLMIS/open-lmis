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
