/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.repository.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepository {

  @Autowired
  ReportMapper mapper;

  public void insert(ReportTemplate reportTemplate) {
    mapper.insert(reportTemplate);
  }
}
