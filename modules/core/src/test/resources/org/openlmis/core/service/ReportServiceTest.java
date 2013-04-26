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
import org.openlmis.core.domain.Report;
import org.openlmis.core.repository.ReportRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

  @Mock
  ReportRepository repository;

  @InjectMocks
  ReportService service;

  @Test
  public void shouldInsertReport() throws Exception {
    Report report = new Report();

    service.insert(report);

    verify(repository).insert(report);
  }


}
