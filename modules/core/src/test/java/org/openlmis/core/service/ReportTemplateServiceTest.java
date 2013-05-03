/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.repository.ReportTemplateRepository;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReportTemplateServiceTest {

  @Mock
  ReportTemplateRepository repository;

  @InjectMocks
  ReportTemplateService service;

  @Test
  public void shouldInsertReport() throws Exception {
    ReportTemplate reportTemplate = spy(new ReportTemplate());

    service.insert(reportTemplate);

    verify(repository).insert(reportTemplate);
  }


}
