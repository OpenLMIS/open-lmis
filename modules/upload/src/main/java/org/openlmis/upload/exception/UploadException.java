package org.openlmis.upload.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UploadException extends RuntimeException {

  public UploadException(String message) {
        super(message);
    }

}
