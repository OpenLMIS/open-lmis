/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.core.exception.DataException;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the services for handling Template entity.
 */

@Service
public class TemplateService {

  @Autowired
  TemplateRepository repository;

  public void insert(Template template) {
    repository.insert(template);
  }

  public List<Template> getAll() {
    return repository.getAll();
  }

  public Template getByName(String name) {
    return repository.getByName(name);
  }

  public Template validateFileAndCreateTemplate(String name, MultipartFile file, Long userId, String type) throws IOException {
    Template template = new Template(name, userId, type);
    validateFile(template, file);
    return template;
  }

  private void validateFile(Template template, MultipartFile file) {
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
        ArrayList<String> parameters = new ArrayList<>();
        for (JRParameter jrParameter : jrParameters) {
          if (!jrParameter.isSystemDefined()) {
            parameters.add(jrParameter.getName());
          }
        }
        template.setParameters(parameters);
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(report);
      template.setData(bos.toByteArray());
    } catch (JRException e) {
      throw new DataException("report.template.error.file.invalid");
    } catch (IOException e) {
      throw new DataException("report.template.error.reading.file");
    }
  }
}
