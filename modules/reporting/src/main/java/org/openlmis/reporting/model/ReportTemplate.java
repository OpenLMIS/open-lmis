/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

  private String type;

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
