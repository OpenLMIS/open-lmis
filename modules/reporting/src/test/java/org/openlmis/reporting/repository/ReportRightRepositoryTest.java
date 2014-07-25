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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.ReportRight;
import org.openlmis.reporting.repository.mapper.ReportRightMapper;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ReportRightRepositoryTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ReportRightMapper reportRightMapper;

  @InjectMocks
  private ReportRightRepository reportRightRepository;

  @Test
  public void shouldInsertReportTemplate() throws Exception {
    ReportRight reportRight = new ReportRight();

    reportRightRepository.insert(reportRight);

    verify(reportRightMapper).insert(reportRight);
  }

  @Test
  public void shouldThrowDataExceptionIfReportWithSameNameAlreadyExists() throws Exception {
    ReportRight reportRight = new ReportRight();
    doThrow(DataIntegrityViolationException.class).when(reportRightMapper).insert(reportRight);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.name.already.exists");
    reportRightRepository.insert(reportRight);
  }
}
