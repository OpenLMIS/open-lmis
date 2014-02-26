/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class VaccinationCoverageMapperIT {

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingPeriodMapper periodMapper;

  @Autowired
  DistributionMapper distributionMapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private FacilityVisitMapper facilityVisitMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  VaccinationCoverageMapper mapper;

  @Autowired
  private ProductMapper productMapper;

  Distribution distribution;
  DeliveryZone zone;
  Program program1;
  ProcessingPeriod processingPeriod;

  Facility facility;
  FacilityVisit facilityVisit;

  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program1 = make(a(defaultProgram));
    facility = make(a(defaultFacility));
    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);

    distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);

    facilityMapper.insert(facility);

    facilityVisit = new FacilityVisit(facility, distribution);
    facilityVisitMapper.insert(facilityVisit);
  }

  @Test
  public void shouldSaveVaccinationFullCoverage() throws Exception {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage(34, 78, 11, 666);
    vaccinationFullCoverage.setFacilityVisitId(facilityVisit.getId());
    vaccinationFullCoverage.setCreatedBy(1L);
    mapper.insertFullVaccinationCoverage(vaccinationFullCoverage);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM full_coverages WHERE id = " + vaccinationFullCoverage.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getInt("femaleHealthCenter"), is(34));
    assertThat(resultSet.getInt("femaleOutreach"), is(78));
    assertThat(resultSet.getInt("maleHealthCenter"), is(11));
    assertThat(resultSet.getInt("maleOutreach"), is(666));
    assertThat(resultSet.getLong("createdBy"), is(1L));
  }

  @Test
  public void shouldGetFullCoverageByFacilityVisitId() {
    VaccinationFullCoverage vaccinationFullCoverage = new VaccinationFullCoverage(34, 78, 11, 666);
    vaccinationFullCoverage.setFacilityVisitId(facilityVisit.getId());
    mapper.insertFullVaccinationCoverage(vaccinationFullCoverage);

    VaccinationFullCoverage savedVaccinationFullCoverage = mapper.getFullCoverageBy(facilityVisit.getId());

    assertThat(savedVaccinationFullCoverage, is(vaccinationFullCoverage));
  }

  @Test
  public void shouldReturnVaccinationCoverageProductMappings() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    TargetGroupProduct childTargetGroupProduct = new TargetGroupProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_target_group_products (targetGroupEntity, productCode, childCoverage) VALUES (?, ?, ?)",
      childTargetGroupProduct.getTargetGroupEntity(), childTargetGroupProduct.getProductCode(), childTargetGroupProduct.getChildCoverage());

    TargetGroupProduct adultTargetGroupProduct = new TargetGroupProduct("Pregnant Women", product.getCode(), false);
    queryExecutor.executeUpdate("INSERT INTO coverage_target_group_products (targetGroupEntity, productCode, childCoverage) VALUES (?, ?, ?)",
      adultTargetGroupProduct.getTargetGroupEntity(), adultTargetGroupProduct.getProductCode(), adultTargetGroupProduct.getChildCoverage());
    List<TargetGroupProduct> targetGroupProducts = mapper.getVaccinationProducts();

    assertThat(targetGroupProducts.size(), is(2));
    assertTrue(targetGroupProducts.contains(childTargetGroupProduct));
    assertTrue(targetGroupProducts.contains(adultTargetGroupProduct));
  }

  @Test
  public void shouldInsertChildCoverageLineItem() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    TargetGroupProduct targetGroupProduct = new TargetGroupProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_target_group_products (targetGroupEntity, productCode, childCoverage) VALUES (?, ?, ?)",
      targetGroupProduct.getTargetGroupEntity(), targetGroupProduct.getProductCode(), targetGroupProduct.getChildCoverage());

    Integer nullInteger = null;
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem("BCG", nullInteger, nullInteger, nullInteger, nullInteger);
    childCoverageLineItem.setFacilityVisitId(facilityVisit.getId());
    childCoverageLineItem.setTargetGroup(56);
    childCoverageLineItem.setCreatedBy(123L);
    mapper.insertChildCoverageLineItem(childCoverageLineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM vaccination_child_coverage_line_items WHERE facilityVisitId = " + childCoverageLineItem.getFacilityVisitId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getInt("targetGroup"), is(56));
    assertThat(resultSet.getString("vaccination"), is("BCG"));
    assertThat(resultSet.getLong("createdBy"), is(123L));
  }

  @Test
  public void shouldGetChildCoverageLineItemByFacilityVisitId() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    TargetGroupProduct targetGroupProduct = new TargetGroupProduct("BCG", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_target_group_products (targetGroupEntity, productCode, childCoverage) VALUES (?, ?, ?)",
      targetGroupProduct.getTargetGroupEntity(), targetGroupProduct.getProductCode(), targetGroupProduct.getChildCoverage());

    Integer nullInteger = null;
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem("BCG", nullInteger, nullInteger, nullInteger, nullInteger);
    childCoverageLineItem.setFacilityVisitId(facilityVisit.getId());
    childCoverageLineItem.setTargetGroup(56);
    mapper.insertChildCoverageLineItem(childCoverageLineItem);

    List<ChildCoverageLineItem> fetchedChildCoverageLineItems = mapper.getChildCoverageLineItemsBy(facilityVisit.getId());

    assertThat(fetchedChildCoverageLineItems.size(), is(1));
    assertThat(fetchedChildCoverageLineItems.get(0).getFacilityVisitId(), is(facilityVisit.getId()));
  }

  @Test
  public void shouldReturnProductVialsMapping() throws SQLException {
    Product product = make(a(ProductBuilder.defaultProduct));

    productMapper.insert(product);
    ProductVial productVial = new ProductVial("BCGVial", product.getCode(), true);

    queryExecutor.executeUpdate("INSERT INTO coverage_product_vials (vial, productCode, childCoverage) VALUES (?, ?, TRUE)",
      productVial.getVial(), productVial.getProductCode());

    List<ProductVial> productVials = mapper.getProductVials();

    assertThat(productVials.size(), is(1));
    assertThat(productVials.get(0).getVial(), is("BCGVial"));
    assertThat(productVials.get(0).getProductCode(), is(product.getCode()));
  }

  @Test
  public void shouldInsertChildCoverageOpenedVialLineItem() throws SQLException {
    String productVialName = "BCG";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    lineItem.setCreatedBy(123L);
    lineItem.setModifiedBy(123L);

    mapper.insertChildCoverageOpenedVialLineItem(lineItem);

    ResultSet resultSet = queryExecutor.execute(
      "SELECT * FROM child_coverage_opened_vial_line_items WHERE facilityVisitId = " + facilityVisit.getId());
    resultSet.next();

    assertThat(resultSet.getInt("packSize"), is(10));
    assertThat(resultSet.getString("productVialName"), is(productVialName));
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getLong("id"), is(notNullValue()));
    assertThat(resultSet.getLong("createdBy"), is(123L));
    assertThat(resultSet.getLong("modifiedBy"), is(123L));
  }

  @Test
  public void shouldUpdateChildCoverageLineItem() {
    Integer nullInteger = null;
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem("BCG", nullInteger, nullInteger, nullInteger, nullInteger);
    childCoverageLineItem.setFacilityVisitId(facilityVisit.getId());
    childCoverageLineItem.setTargetGroup(56);

    mapper.insertChildCoverageLineItem(childCoverageLineItem);

    childCoverageLineItem.setHealthCenter11Months(1234);
    childCoverageLineItem.setOutreach11Months(34);
    childCoverageLineItem.setHealthCenter23Months(43);
    childCoverageLineItem.setOutreach23Months(4234);
    childCoverageLineItem.setModifiedBy(123L);

    mapper.updateChildCoverageLineItem(childCoverageLineItem);

    assertThat(childCoverageLineItem.getHealthCenter11Months(), is(1234));
    assertThat(childCoverageLineItem.getOutreach11Months(), is(34));
    assertThat(childCoverageLineItem.getHealthCenter23Months(), is(43));
    assertThat(childCoverageLineItem.getOutreach23Months(), is(4234));
    assertThat(childCoverageLineItem.getModifiedBy(), is(123L));
  }

  @Test
  public void shouldGetChildCoverageOpenedVialLineItems() throws Exception {
    String productVialName = "BCG";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    mapper.insertChildCoverageOpenedVialLineItem(lineItem);

    List<OpenedVialLineItem> openedVialLineItems = mapper.getChildCoverageOpenedVialLineItemsBy(facilityVisit.getId());

    assertThat(openedVialLineItems.size(), is(1));
    assertThat(openedVialLineItems.get(0).getProductVialName(), is("BCG"));
  }

  @Test
  public void shouldGetAdultCoverageOpenedVialLineItems() throws Exception {
    String productVialName = "Tetanus";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    mapper.insertAdultCoverageOpenedVialLineItem(lineItem);

    List<OpenedVialLineItem> openedVialLineItems = mapper.getAdultCoverageOpenedVialLineItemsBy(facilityVisit.getId());

    assertThat(openedVialLineItems.size(), is(1));
    assertThat(openedVialLineItems.get(0).getProductVialName(), is("Tetanus"));
  }

  @Test
  public void shouldUpdateChildCoverageOpenedVialLineItem() throws SQLException {
    String productVialName = "BCG";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    mapper.insertChildCoverageOpenedVialLineItem(lineItem);

    lineItem.setOpenedVials(55);
    lineItem.setModifiedBy(123L);
    mapper.updateChildCoverageOpenedVialLineItem(lineItem);

    ResultSet resultSet = queryExecutor.execute(
      "SELECT * FROM child_coverage_opened_vial_line_items WHERE facilityVisitId = " + facilityVisit.getId());

    resultSet.next();
    assertThat(resultSet.getInt("openedVials"), is(55));
    assertThat(resultSet.getLong("modifiedBy"), is(123L));
  }

  @Test
  public void shouldInsertAdultCoverageLineItem() throws SQLException {

    AdultCoverageLineItem lineItem = new AdultCoverageLineItem();
    lineItem.setFacilityVisitId(facilityVisit.getId());
    lineItem.setTargetGroup(45);
    lineItem.setDemographicGroup("Pregnant Women");
    mapper.insertAdultCoverageLineItem(lineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM vaccination_adult_coverage_line_items WHERE demographicGroup = 'Pregnant Women'");

    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("id"), is(lineItem.getId()));
    assertThat(resultSet.getLong("facilityVisitId"), is(lineItem.getFacilityVisitId()));
  }

  @Test
  public void shouldReturnAdultCoverageLineItemsByFacilityVisitId() {
    AdultCoverageLineItem adultCoverageLineItem = new AdultCoverageLineItem();
    adultCoverageLineItem.setFacilityVisitId(facilityVisit.getId());
    adultCoverageLineItem.setTargetGroup(56);
    adultCoverageLineItem.setDemographicGroup("Pregnant Women");
    mapper.insertAdultCoverageLineItem(adultCoverageLineItem);

    List<AdultCoverageLineItem> fetchedAdultCoverageLineItems = mapper.getAdultCoverageLineItemsBy(facilityVisit.getId());

    assertThat(fetchedAdultCoverageLineItems.size(), is(1));
    assertThat(fetchedAdultCoverageLineItems.get(0).getFacilityVisitId(), is(facilityVisit.getId()));
  }

  @Test
  public void shouldUpdateAdultCoverageLineItems() throws Exception {

    Integer nullInteger = null;
    AdultCoverageLineItem adultCovergaeLineItem = new AdultCoverageLineItem("Pregnant Women", nullInteger, nullInteger, nullInteger, nullInteger);
    adultCovergaeLineItem.setFacilityVisitId(facilityVisit.getId());
    adultCovergaeLineItem.setTargetGroup(56);

    mapper.insertAdultCoverageLineItem(adultCovergaeLineItem);

    adultCovergaeLineItem.setHealthCenterTetanus1(1234);
    adultCovergaeLineItem.setOutreachTetanus1(34);
    adultCovergaeLineItem.setHealthCenterTetanus2To5(43);
    adultCovergaeLineItem.setOutreachTetanus2To5(4234);
    adultCovergaeLineItem.setModifiedBy(123L);

    mapper.updateAdultCoverageLineItem(adultCovergaeLineItem);

    AdultCoverageLineItem returnedLineItem = mapper.getAdultCoverageLineItemsBy(facilityVisit.getId()).get(0);

    assertThat(returnedLineItem.getHealthCenterTetanus1(), is(1234));
    assertThat(returnedLineItem.getOutreachTetanus1(), is(34));
    assertThat(returnedLineItem.getHealthCenterTetanus2To5(), is(43));
    assertThat(returnedLineItem.getOutreachTetanus2To5(), is(4234));
    assertThat(returnedLineItem.getModifiedBy(), is(123L));
  }


  @Test
  public void shouldInsertAdultCoverageOpenedVialLineItem() throws SQLException {
    String productVialName = "Tetanus";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    lineItem.setCreatedBy(123L);

    mapper.insertAdultCoverageOpenedVialLineItem(lineItem);

    ResultSet resultSet = queryExecutor.execute(
      "SELECT * FROM adult_coverage_opened_vial_line_items WHERE facilityVisitId = " + facilityVisit.getId());

    resultSet.next();
    assertThat(resultSet.getInt("packSize"), is(10));
    assertThat(resultSet.getString("productVialName"), is(productVialName));
    assertThat(resultSet.getLong("facilityVisitId"), is(facilityVisit.getId()));
    assertThat(resultSet.getLong("id"), is(notNullValue()));
    assertThat(resultSet.getLong("createdBy"), is(123L));
  }

  @Test
  public void shouldUpdateAdultCoverageOpenedVialLineItem() throws SQLException {
    String productVialName = "Tetanus";
    OpenedVialLineItem lineItem = new OpenedVialLineItem(facilityVisit.getId(), productVialName, null, 10);
    mapper.insertAdultCoverageOpenedVialLineItem(lineItem);

    lineItem.setOpenedVials(55);
    lineItem.setModifiedBy(123L);
    mapper.updateAdultCoverageOpenedVialLineItem(lineItem);

    ResultSet resultSet = queryExecutor.execute(
      "SELECT * FROM adult_coverage_opened_vial_line_items WHERE facilityVisitId = " + facilityVisit.getId());

    resultSet.next();
    assertThat(resultSet.getInt("openedVials"), is(55));
    assertThat(resultSet.getLong("modifiedBy"), is(123L));

  }
}