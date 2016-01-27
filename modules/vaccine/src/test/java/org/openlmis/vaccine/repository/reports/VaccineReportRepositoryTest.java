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

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportMapper;
import org.openlmis.vaccine.service.reports.VaccineLineItemService;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportRepositoryTest {

  @Mock
  VaccineLineItemService lineItemService;

  @Mock
  VaccineReportMapper mapper;

  @InjectMocks
  VaccineReportRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    VaccineReport report = new VaccineReport();
    repository.insert(report);
    verify(mapper).insert(report);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineReport report = new VaccineReport();
    repository.update(report, 2L);
    verify(mapper).update(report);
  }

  @Test
  public void shouldGetById() throws Exception {
    repository.getById(20L);
    verify(mapper).getById(20L);
  }

  @Test
  public void shouldGetByIdWithFullDetails() throws Exception {
    repository.getByIdWithFullDetails(20L);
    verify(mapper).getByIdWithFullDetails(20L);
  }

  @Test
  public void shouldGetByProgramPeriod() throws Exception {
    repository.getByProgramPeriod(20L, 10L, 3L);
    verify(mapper).getByPeriodFacilityProgram(20L, 10L, 3L);
  }

  @Test
  public void shouldGetLastReport() throws Exception {
    repository.getLastReport(20L, 2L);
    verify(mapper).getLastReport(20L, 2L);
  }

  @Test
  public void shouldGetScheduleFor() throws Exception {
    repository.getScheduleFor(29L, 2L);
    verify(mapper).getScheduleFor(29L, 2L);
  }

  @Test
  public void shouldGetSubmittedPeriods() throws Exception{
    repository.getReportedPeriodsForFacility(2L,1L);
    verify(mapper).getReportedPeriodsForFacility(2L, 1L);
  }

}