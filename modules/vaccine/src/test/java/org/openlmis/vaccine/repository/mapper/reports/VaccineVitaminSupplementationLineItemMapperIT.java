package org.openlmis.vaccine.repository.mapper.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

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
import org.openlmis.vaccine.domain.reports.VitaminSupplementationLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-applicationContext-vaccine.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineVitaminSupplementationLineItemMapperIT {

  @Autowired
  VitaminSupplementationLineItemMapper mapper;

  @Autowired
  VaccineReportMapper reportMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  private ProcessingPeriod processingPeriod;

  private Facility facility;

  VaccineReport report;

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

    report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setFacilityId(facility.getId());
    report.setProgramId(1L);
    report.setPeriodId(processingPeriod.getId());
    reportMapper.insert(report);
  }

  @Test
  public void shouldInsert() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    lineItem.setReportId(report.getId());
    lineItem.setVaccineVitaminId(1L);
    lineItem.setDisplayOrder(1L);
    lineItem.setVitaminName("Vitamin Z");
    lineItem.setVitaminAgeGroupId(1L);

    Integer count = mapper.insert(lineItem);

    assertThat(count, is(1));
    assertThat(lineItem.getId(), is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    lineItem.setReportId(report.getId());
    lineItem.setVaccineVitaminId(1L);
    lineItem.setDisplayOrder(1L);
    lineItem.setVitaminName("Vitamin Z");
    lineItem.setVitaminAgeGroupId(1L);

     mapper.insert(lineItem);

    lineItem.setFemaleValue(23L);
    lineItem.setMaleValue(44L);
    mapper.update(lineItem);

    VitaminSupplementationLineItem returnedObject = mapper.getLineItems(report.getId()).get(0);
    assertThat(returnedObject.getFemaleValue(), is(lineItem.getFemaleValue()));
    assertThat(returnedObject.getMaleValue(), is(lineItem.getMaleValue()));
  }

  @Test
  public void shouldGetLineItems() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    lineItem.setReportId(report.getId());
    lineItem.setVaccineVitaminId(1L);
    lineItem.setDisplayOrder(1L);
    lineItem.setVitaminName("Vitamin Z");
    lineItem.setVitaminAgeGroupId(1L);

    mapper.insert(lineItem);
    List<VitaminSupplementationLineItem> list = mapper.getLineItems(report.getId());
    assertThat(list.size(), is(1));
  }
}