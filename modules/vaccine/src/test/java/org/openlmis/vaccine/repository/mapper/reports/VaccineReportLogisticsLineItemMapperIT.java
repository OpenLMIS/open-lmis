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
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.builders.reports.LogisticsLineItemBuilder;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-applicationContext-vaccine.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportLogisticsLineItemMapperIT {

  @Autowired
  VaccineReportMapper vaccineReportMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  VaccineReportLogisticsLineItemMapper logisticsLineItemMapper;

  @Autowired
  FacilityMapper facilityMapper;

  private VaccineReport report;

  private Product product;

  @Before
  public void setUp() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
      with(scheduleId, processingSchedule.getId()),
      with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);

    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    vaccineReportMapper.insert(report);
  }

  @Test
  public void shouldInsert() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setReportId(report.getId());
    lineItem.setProductId(product.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    assertThat(count, is(1));
    assertThat(lineItem.getId(),is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setReportId(report.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    lineItem.setRemarks("the Remark");
    logisticsLineItemMapper.update(lineItem);

    List<LogisticsLineItem> lineItemList = logisticsLineItemMapper.getLineItems(report.getId());
    assertThat(lineItem.getRemarks(), is(lineItemList.get(0).getRemarks()));
  }

  @Test
  public void shouldGetLineItems() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setReportId(report.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    List<LogisticsLineItem> lineItemList = logisticsLineItemMapper.getLineItems(report.getId());
    assertThat(lineItemList, hasSize(1));
  }
}