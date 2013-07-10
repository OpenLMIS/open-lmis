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
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReportTemplate extends BaseModel {

  private String name;

  private byte[] data;

  private List<String> parameters;

  private String commaSeparatedParameters;

  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
    this.commaSeparatedParameters = commaSeparatedParameters();
  }

  public void setCommaSeparatedParameters(String commaSeparatedParameters) {
    this.commaSeparatedParameters = commaSeparatedParameters;
    if (commaSeparatedParameters != null && commaSeparatedParameters.length() > 0) {
      this.parameters = Arrays.asList(commaSeparatedParameters.split(","));
    }
  }

  public ReportTemplate(String name, MultipartFile file, Long modifiedBy) throws IOException {
    validateFile(file);
    this.name = name;
    this.modifiedBy = modifiedBy;
  }

  private String commaSeparatedParameters() {
    StringBuilder parameterString = new StringBuilder();
    for (String parameter : parameters) {
      parameterString.append(parameter).append(",");
    }
    return parameterString.toString();
  }

  private void validateFile(MultipartFile file) {
    if (file == null)
      throw new DataException("report.template.error.file.missing");
    if (!file.getOriginalFilename().endsWith(".jrxml"))
      throw new DataException("report.template.error.file.type");
    if (file.isEmpty())
      throw new DataException("report.template.error.file.empty");
    try {
      JasperReport report = JasperCompileManager.compileReport(file.getInputStream());
      JRParameter[] jrParameters = report.getParameters();
      if (jrParameters != null && jrParameters.length > 0) {
        this.parameters = new ArrayList<>();
        for (JRParameter jrParameter : jrParameters) {
          if (!jrParameter.isSystemDefined()) {
            this.parameters.add(jrParameter.getName());
          }
        }
        this.commaSeparatedParameters = commaSeparatedParameters();
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(report);
      this.data = bos.toByteArray();
    } catch (JRException e) {
      throw new DataException("report.template.error.file.invalid");
    } catch (IOException e) {
      throw new DataException("report.template.error.reading.file");
    }
  }
}
