/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.repository;

import org.apache.log4j.Logger;
import org.openlmis.core.exception.DataException;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.openlmis.reporting.repository.mapper.TemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Repository class for template related database operations.
 */

@Repository
public class TemplateRepository {

  @Autowired
  private TemplateMapper mapper;

  private static final Logger logger = Logger.getLogger(TemplateRepository.class);

  public void insertWithParameters(Template template) {
    try {
      mapper.insert(template);
      for(TemplateParameter parameter : template.getParameters()){
        parameter.setTemplateId(template.getId());
        mapper.insertParameter(parameter);
      }
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("unexpected.exception");
    }
  }

  public List<Template> getAllTemplatesForUser(Long userId) {
    return mapper.getAllTemplatesForUser(userId);
  }

  public Template getByName(String name) {
    return mapper.getByName(name);
  }

  public Template getById(Long id) {
    return mapper.getById(id);
  }

  public Template getLWById(Long id) {
    Template t = mapper.getLWById(id);
    if(t == null) return null;

    // run select sql and populate returned values for every template parameter
    for(TemplateParameter tp : t.getParameters()) {
      if(isBlank(tp.getSelectSql()) == false) {
        logger.debug("Template Parameter " + t.getName() + " has select sql: " + tp.getSelectSql());
        List<String> selectValues = mapper.runSelectSql(tp.getSelectSql());
        logger.debug("Template Parameter " + t.getName() + " select values: " + selectValues);
        tp.setSelectValues(selectValues);
      }
    }

    return t;
  }
}
