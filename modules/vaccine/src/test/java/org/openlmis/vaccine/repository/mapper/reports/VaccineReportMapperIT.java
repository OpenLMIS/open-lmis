package org.openlmis.vaccine.repository.mapper.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportMapperIT {

  @Autowired
  VaccineReportMapper vaccineReportMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  FacilityMapper facilityMapper;


  private ProcessingPeriod processingPeriod;

  private Facility facility;

  @Before
  public void setUp(){

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);
  }

  @Test
  public void shouldInsertVaccineReport() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    Integer count = vaccineReportMapper.insert(report);

    assertThat(count, is(1));
    assertThat(report.getId(), is(notNullValue()));
  }

  @Test
  public void shouldGetById() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    Integer count = vaccineReportMapper.insert(report);

    VaccineReport newReport = vaccineReportMapper.getById(report.getId());
    assertThat(newReport, is(report));
  }

  @Test
  public void testGetByPeriodFacilityProgram() throws Exception {

  }

  @Test
  public void shouldGetByIdWithFullDetails() throws Exception {

  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    vaccineReportMapper.insert(report);

    report.setStatus("New Status");
    report.setMajorImmunizationActivities("Major Immunization Activities were ... this and that.");
    vaccineReportMapper.update(report);


    VaccineReport newReport = vaccineReportMapper.getById(report.getId());
    assertThat(newReport, is(report));
  }

  @Test
  public void testGetScheduleFor() throws Exception {

  }

  @Test
  public void testGetLastReport() throws Exception {

  }
}