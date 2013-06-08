/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.repository.ReportTemplateRepository;
import org.openlmis.reporting.model.ReportTemplate;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ReportTemplateServiceTest {

  @Mock
  ReportTemplateRepository repository;

  @InjectMocks
  ReportTemplateService service;

  @Test
  public void shouldInsertReport() throws Exception {
    ReportTemplate reportTemplate = Mockito.spy(new ReportTemplate());

    service.insert(reportTemplate);

    Mockito.verify(repository).insert(reportTemplate);
  }


}
