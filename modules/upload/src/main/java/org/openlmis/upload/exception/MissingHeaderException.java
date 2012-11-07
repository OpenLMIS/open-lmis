package org.openlmis.upload.exception;

import org.supercsv.exception.SuperCsvException;

public class MissingHeaderException extends SuperCsvException {

    public MissingHeaderException(String message) {
        super(message);
    }
}
