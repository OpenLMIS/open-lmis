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
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RightService;
import org.openlmis.reporting.model.ReportRight;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.openlmis.reporting.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.openlmis.core.domain.RightType.REPORTING;

/**
 * Exposes the services for handling Template entity.
 */

@Service
public class TemplateService {

  public static final String PDF_VIEW = "pdf";
  public static final String USER_ID_PARAM = "userId";

  @Autowired
  TemplateRepository repository;

  @Autowired
  MessageService messageService;

  @Autowired
  RightService rightService;

  @Autowired
  ReportRightService reportRightService;

  public List<Template> getAllTemplatesForUser(Long userId) {
    return repository.getAllTemplatesForUser(userId);
  }

  public Template getByName(String name) {
    return repository.getByName(name);
  }

  public Template getById(Long id) {
    return repository.getById(id);
  }

  public Template getLWById(Long id) {
    return repository.getLWById(id);
  }

  public void validateFileAndInsertTemplate(Template template, MultipartFile file) throws IOException {
    throwIfTemplateWithSameNameAlreadyExists(template.getName());
    validateFile(template, file);
    repository.insertWithParameters(template);

    Right right = new Right(template.getName(), REPORTING);
    rightService.insertRight(right);

    ReportRight reportRight = new ReportRight(template, right);
    reportRightService.insert(reportRight);
  }

  private void validateFile(Template template, MultipartFile file) {
    throwIfFileIsNull(file);
    throwIfIncorrectFileType(file);
    throwIfFileIsEmpty(file);
    try {
      JasperReport report = JasperCompileManager.compileReport(file.getInputStream());
      JRParameter[] jrParameters = report.getParameters();
      if (jrParameters != null && jrParameters.length > 0) {

        ArrayList<TemplateParameter> parameters = new ArrayList<>();

        for (JRParameter jrParameter : jrParameters) {
          if (!jrParameter.isSystemDefined()) {
            parameters.add(createParameter(template.getCreatedBy(), jrParameter));
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

  private TemplateParameter createParameter(Long createdBy, JRParameter jrParameter) {
    String[] propertyNames = jrParameter.getPropertiesMap().getPropertyNames();
    if (propertyNames.length > 1) {
      throw new DataException(messageService.message("report.template.extra.properties", jrParameter.getName()));
    }
    String displayName = jrParameter.getPropertiesMap().getProperty("displayName");
    if (isBlank(displayName)) {
      throw new DataException(messageService.message("report.template.parameter.display.name.missing", jrParameter.getName()));
    }
    TemplateParameter templateParameter = new TemplateParameter();
    templateParameter.setName(jrParameter.getName());
    templateParameter.setDisplayName(displayName);
    templateParameter.setDescription(jrParameter.getDescription());
    templateParameter.setDataType(jrParameter.getValueClassName());
    if (jrParameter.getDefaultValueExpression() != null) {
      templateParameter.setDefaultValue(jrParameter.getDefaultValueExpression().getText().replace("\"", "").replace("\'", ""));
    }
    templateParameter.setCreatedBy(createdBy);
    return templateParameter;
  }

  private void throwIfTemplateWithSameNameAlreadyExists(String name) {
    if (repository.getByName(name) != null) {
      throw new DataException("report.template.name.already.exists");
    }
  }

  private void throwIfFileIsEmpty(MultipartFile file) {
    if (file.isEmpty())
      throw new DataException("report.template.error.file.empty");
  }

  private void throwIfIncorrectFileType(MultipartFile file) {
    if (!file.getOriginalFilename().endsWith(".jrxml"))
      throw new DataException("report.template.error.file.type");
  }

  private void throwIfFileIsNull(MultipartFile file) {
    if (file == null)
      throw new DataException("report.template.error.file.missing");
  }

  public Map<String, Object> getParametersMap(Template template, int userId, HttpServletRequest request, String format) throws ParseException {
    List<TemplateParameter> templateParameters = template.getParameters();
    Map<String, String[]> requestParameterMap = request.getParameterMap();
    Map<String, Object> map = new HashMap<>();

    if (templateParameters != null) {
      for (TemplateParameter templateParameter : templateParameters) {
        String templateParameterName = templateParameter.getName();

        if (templateParameterName.equalsIgnoreCase(USER_ID_PARAM)) {
          map.put(templateParameterName, userId);
        }

        for (String requestParamName : requestParameterMap.keySet()) {

          if (templateParameterName.equalsIgnoreCase(requestParamName)) {
            String requestParamValue = request.getParameter(templateParameterName);

            if (!(isBlank(requestParamValue) || requestParamValue.equals("null") || requestParamValue.equals("undefined"))) {
              map.put(templateParameterName, templateParameter.getParsedValueOf(requestParamValue));
            }
          }
        }
      }
    }
    String viewFormat = format == null ? PDF_VIEW : format;
    map.put("format", viewFormat);

    return map;
  }
}
