/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

import org.mockito.runners.MockitoJUnitRunner;
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
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.builders.reports.VaccineReportBuilder;
import org.openlmis.vaccine.domain.reports.VaccineCoverageItem;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineReportCoverageMapperIT {

  @Autowired
  VaccineReportCoverageMapper mapper;

  @Autowired
  VaccineReportMapper vaccineReportMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  ProductMapper productMapper;

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

    report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());

    vaccineReportMapper.insert(report);

    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);
  }

  private VaccineCoverageItem getVaccineCoverageItem() {
    VaccineCoverageItem item = new VaccineCoverageItem();
    item.setReportId(report.getId());
    item.setDoseId(1L);
    item.setDisplayName("the name");
    item.setDisplayOrder(1L);
    item.setTrackMale(true);
    item.setTrackFemale(false);
    item.setProductId(product.getId());
    return item;
  }

  @Test
  public void shouldInsert() throws Exception {
    VaccineCoverageItem item = getVaccineCoverageItem();

    Integer count = mapper.insert(item);
    assertThat(count, is(1));
    assertThat(item.getId(), is(notNullValue()));
  }



  @Test
  public void shouldUpdate() throws Exception {
    VaccineCoverageItem item = getVaccineCoverageItem();
    mapper.insert(item);

    item.setDisplayName("something different");
    mapper.update(item);

    VaccineCoverageItem returned = mapper.getById(item.getId());
    assertThat(returned.getDisplayName(), is(item.getDisplayName()));
  }

  @Test
  public void shouldGetById() throws Exception {
    VaccineCoverageItem item = getVaccineCoverageItem();
    mapper.insert(item);

    VaccineCoverageItem returned = mapper.getById(item.getId());
    assertThat(returned.getDisplayName(), is(item.getDisplayName()));
  }

  @Test
  public void shouldGetCoverageByReportProductDosage() throws Exception {
    VaccineCoverageItem item = getVaccineCoverageItem();
    mapper.insert(item);

    VaccineCoverageItem returned = mapper.getCoverageByReportProductDosage(report.getId(), product.getId(), 1L);
    assertThat(returned, is(item));
  }

  @Test
  public void shouldGetLineItems() throws Exception {
    VaccineCoverageItem item = getVaccineCoverageItem();
    mapper.insert(item);

    List<VaccineCoverageItem> returned = mapper.getLineItems(report.getId());
    assertThat(returned.size(), is(1));
  }
}