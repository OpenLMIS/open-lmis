/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.ReportStatus;
import org.openlmis.vaccine.domain.reports.ReportStatusChange;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportStatusChangeMapper;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportStatusChangeRepositoryTest {

  @Mock
  VaccineReportStatusChangeMapper mapper;

  @InjectMocks
  VaccineReportStatusChangeRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    ReportStatusChange change = new ReportStatusChange();
    when(mapper.insert(change)).thenReturn(1);

    repository.insert(change);
    verify(mapper).insert(change);
  }

  @Test
  public void shouldGetChangesForReport() throws Exception {
    ReportStatusChange change = new ReportStatusChange();
    when(mapper.getChangeLogByReportId(1L)).thenReturn(asList(change));

    List<ReportStatusChange> changes = repository.getChangesForReport(1L);
    assertThat(changes.size(), is(1));
    verify(mapper).getChangeLogByReportId(1L);
  }

  @Test
  public void shouldGetOperation() throws Exception {
    ReportStatusChange change = new ReportStatusChange();
    change.setStatus(ReportStatus.APPROVED);
    when(mapper.getOperationLog(1L, ReportStatus.APPROVED)).thenReturn(change);

    ReportStatusChange result = repository.getOperation(1L, ReportStatus.APPROVED);
    assertThat(result, is(change));
    verify(mapper).getOperationLog(1L, ReportStatus.APPROVED);
  }
}