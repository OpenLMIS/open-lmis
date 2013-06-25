/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.repository;

import org.openlmis.core.exception.DataException;
import org.openlmis.reporting.model.ReportTemplate;
import org.openlmis.reporting.repository.mapper.ReportTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportTemplateRepository {

  @Autowired
  ReportTemplateMapper mapper;

  public void insert(ReportTemplate reportTemplate) {
    try {
      mapper.insert(reportTemplate);
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("report.template.name.already.exists");
    }
  }

  public List<ReportTemplate> getAll() {
    return mapper.getAll();
  }
}
