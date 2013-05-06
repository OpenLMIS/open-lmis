/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.reporting.model.ReportTemplate;
import org.openlmis.core.exception.DataException;
import org.openlmis.reporting.repository.mapper.ReportTemplateMapper;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.reporting.repository.ReportTemplateRepository.REPORT_WITH_SAME_NAME_ALREADY_EXISTS;

@RunWith(MockitoJUnitRunner.class)
public class ReportTemplateRepositoryTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ReportTemplateMapper reportTemplateMapper;
  @InjectMocks
  private ReportTemplateRepository reportTemplateRepository;

  @Test
  public void shouldInsertReportTemplate() throws Exception {
    ReportTemplate template = new ReportTemplate();
    reportTemplateRepository.insert(template);
    verify(reportTemplateMapper).insert(template);
  }

  @Test
  public void shouldThrowDataExceptionIfReportWithSameNameAlreadyExists() throws Exception {
    ReportTemplate template = new ReportTemplate();
    doThrow(DataIntegrityViolationException.class).when(reportTemplateMapper).insert(template);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(REPORT_WITH_SAME_NAME_ALREADY_EXISTS);
    reportTemplateRepository.insert(template);
  }
}
