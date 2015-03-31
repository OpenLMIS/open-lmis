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
import org.openlmis.vaccine.builders.reports.DiseaseLineItemBuilder;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportMapperIT {

  @Autowired
  private VaccineReportMapper vaccineReportMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private VaccineReportDiseaseLineItemMapper diseaseLineItemMapper;


  private ProcessingPeriod processingPeriod;

  private Facility facility;

  @Before
  public void setUp(){

    facility = make(a(FacilityBuilder.defaultFacility));
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
    assertThat(newReport.getId(), is(report.getId()));
  }

  @Test
  public void shouldGetByPeriodFacilityProgram() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    Integer count = vaccineReportMapper.insert(report);

    VaccineReport returnedReport = vaccineReportMapper.getByPeriodFacilityProgram(facility.getId(), processingPeriod.getId(), 1L);
    assertThat(returnedReport.getId(), is(report.getId()));
  }


  @Test
  public void shouldGetNullWhenPeriodDoesNotMatchByPeriodFacilityProgram() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    Integer count = vaccineReportMapper.insert(report);

    VaccineReport returnedReport = vaccineReportMapper.getByPeriodFacilityProgram(facility.getId(),processingPeriod.getId() + 1, 1L);
    assertThat(returnedReport, is(nullValue()));
  }

  @Test
  public void shouldGetByIdWithFullDiseaseLineItemDetails() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    Integer count = vaccineReportMapper.insert(report);

    DiseaseLineItem diseaseLineItem = make(a(DiseaseLineItemBuilder.defaultDiseaseLineItem));
    diseaseLineItem.setReportId(report.getId());
    diseaseLineItemMapper.insert(diseaseLineItem);

    VaccineReport returnedReport = vaccineReportMapper.getByIdWithFullDetails(report.getId());

    assertThat(returnedReport.getDiseaseLineItems(), hasSize(1));
    assertThat(returnedReport.getDiseaseLineItems(), hasItem(diseaseLineItem));
    //TODO insert and check all the other line item types too.
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
    assertThat(newReport.getMajorImmunizationActivities(), is(report.getMajorImmunizationActivities()));
  }

  @Test
  public void shouldSaveImmunizationSessions() throws Exception{
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());

    report.setFixedImmunizationSessions(20L);
    report.setOutreachImmunizationSessions(40L);
    report.setOutreachImmunizationSessionsCanceled(50L);
    vaccineReportMapper.insert(report);

    VaccineReport returnedReport = vaccineReportMapper.getById(report.getId());
    assertThat(report.getFixedImmunizationSessions(), is(returnedReport.getFixedImmunizationSessions()));
    assertThat(report.getOutreachImmunizationSessions(), is(returnedReport.getOutreachImmunizationSessions()));
    assertThat(report.getOutreachImmunizationSessionsCanceled(), is(returnedReport.getOutreachImmunizationSessionsCanceled()));
  }

  @Test
  public void shouldUpdateImmunizationSessions() throws Exception{
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());

    vaccineReportMapper.insert(report);
    report.setFixedImmunizationSessions(20L);
    report.setOutreachImmunizationSessions(40L);
    report.setOutreachImmunizationSessionsCanceled(50L);
    vaccineReportMapper.update(report);
    VaccineReport returnedReport = vaccineReportMapper.getById(report.getId());
    assertThat(report.getFixedImmunizationSessions(), is(returnedReport.getFixedImmunizationSessions()));
    assertThat(report.getOutreachImmunizationSessions(), is(returnedReport.getOutreachImmunizationSessions()));
    assertThat(report.getOutreachImmunizationSessionsCanceled(), is(returnedReport.getOutreachImmunizationSessionsCanceled()));
  }
}