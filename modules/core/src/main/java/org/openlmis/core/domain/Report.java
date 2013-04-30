/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Data
@NoArgsConstructor
public class Report extends BaseModel {
  public static final String ERROR_JASPER_UPLOAD_EMPTY = "error.jasper.upload.empty";
  public static final String ERROR_JASPER_UPLOAD_TYPE = "error.jasper.upload.type";
  public static final String ERROR_JASPER_UPLOAD_FILE_MISSING = "error.jasper.upload.file.missing";

  private String name;

  private byte[] data;

  private String parameters;

  public Report(MultipartFile file, Integer modifiedBy) throws IOException {
    validateFile(file);
    this.name = file.getName();
    this.data = file.getBytes();
    this.modifiedBy = modifiedBy;
  }

  private void validateFile(MultipartFile file) {
    if (file == null) throw new DataException(ERROR_JASPER_UPLOAD_FILE_MISSING);
    if (!file.getName().endsWith(".jrxml")) throw new DataException(ERROR_JASPER_UPLOAD_TYPE);
    if (file.isEmpty()) throw new DataException(ERROR_JASPER_UPLOAD_EMPTY);
  }

  public void validate() {

  }
}
