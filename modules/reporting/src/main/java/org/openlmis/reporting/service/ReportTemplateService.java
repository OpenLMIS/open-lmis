/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.service;

import org.openlmis.reporting.repository.ReportTemplateRepository;
import org.openlmis.reporting.model.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportTemplateService {

  @Autowired
  ReportTemplateRepository repository;

  public void insert(ReportTemplate reportTemplate) {
    repository.insert(reportTemplate);
  }

  public List<ReportTemplate> getAll() {
    return repository.getAll();
  }
}
