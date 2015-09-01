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

package org.openlmis.vaccine.repository.mapper.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.reports.ReportStatus;
import org.openlmis.vaccine.domain.reports.ReportStatusChange;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportStatusChangeMapperIT {

  @Autowired
  private VaccineReportMapper reportMapper;

  @Autowired
  private VaccineReportStatusChangeMapper mapper;


  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  FacilityMapper facilityMapper;

  VaccineReport report;

  @Before
  public void setup(){

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);

    report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());

    reportMapper.insert(report);
  }

  @Test
  public void shouldInsert() throws Exception {

    ReportStatusChange change = new ReportStatusChange();
    change.setReportId(report.getId());
    change.setStatus(ReportStatus.DRAFT);
    Integer result = mapper.insert(change);

    assertThat(result, is(1));
    assertThat(change.getId(),is(notNullValue()));
  }

  @Test
  public void shouldGetChangeLogByReportId() throws Exception {
    ReportStatusChange change = new ReportStatusChange();
    change.setReportId(report.getId());
    change.setStatus(ReportStatus.DRAFT);
    change.setCreatedBy(1L);
    mapper.insert(change);

    List<ReportStatusChange> changes = mapper.getChangeLogByReportId(report.getId());

    assertThat(changes.size(), is(1));
    assertThat(changes.get(0).getStatus(), is(ReportStatus.DRAFT));
  }

  @Test
  public void shouldGetOperationLog() throws Exception {
    ReportStatusChange change = new ReportStatusChange();
    change.setReportId(report.getId());
    change.setStatus(ReportStatus.APPROVED);
    change.setCreatedBy(1L);
    mapper.insert(change);

    ReportStatusChange changes = mapper.getOperationLog(report.getId(), ReportStatus.APPROVED);

    assertThat(changes, is(notNullValue()));
    assertThat(changes.getStatus(), is(ReportStatus.APPROVED));
  }
}