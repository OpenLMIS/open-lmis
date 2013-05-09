/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * CReportht © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReportTemplate extends BaseModel {
  public static final String CREATE_REPORT_ERROR_FILE_EMPTY = "report.template.error.file.empty";
  public static final String CREATE_REPORT_ERROR_FILE_TYPE = "report.template.error.file.type";
  public static final String CREATE_REPORT_ERROR_FILE_MISSING = "report.template.error.file.missing";
  public static final String CREATE_REPORT_ERROR_FILE_INVALID = "report.template.error.file.invalid";
  public static final String CREATE_REPORT_ERROR_READING_FILE = "report.template.error.reading.file";

  private String name;

  private byte[] data;

  private String parameters;

  public ReportTemplate(String name, MultipartFile file, Long modifiedBy) throws IOException {
    validateFile(file);
    this.name = name;
    //this.data = file.getBytes();
    this.modifiedBy = modifiedBy;
  }

  private void validateFile(MultipartFile file) {
    if (file == null) throw new DataException(CREATE_REPORT_ERROR_FILE_MISSING);
    if (!file.getOriginalFilename().endsWith(".jrxml"))
      throw new DataException(CREATE_REPORT_ERROR_FILE_TYPE);
    if (file.isEmpty()) throw new DataException(CREATE_REPORT_ERROR_FILE_EMPTY);
    try {
      JasperReport report = JasperCompileManager.compileReport(file.getInputStream());
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(report);
      this.data = bos.toByteArray();
    } catch (JRException e) {
      throw new DataException(CREATE_REPORT_ERROR_FILE_INVALID);
    } catch (IOException e) {
      throw new DataException(CREATE_REPORT_ERROR_READING_FILE);
    }
  }
}
