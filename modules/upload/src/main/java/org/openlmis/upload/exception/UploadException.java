package org.openlmis.upload.exception;

import lombok.Data;

@Data
public class UploadException extends RuntimeException {

  public UploadException(String message) {
        super(message);
    }

}
