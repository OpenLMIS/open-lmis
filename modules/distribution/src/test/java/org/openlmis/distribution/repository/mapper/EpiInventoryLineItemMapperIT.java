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
import org.openlmis.distribution.builder.DistributionBuilder;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiInventory;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.FacilityVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-distribution.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EpiInventoryLineItemMapperIT {

  @Autowired
  private EpiInventoryLineItemMapper mapper;

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
  private FacilityMapper facilityMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private FacilityVisitMapper facilityVisitMapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;

  DeliveryZone zone;
  Program program1;
  ProcessingPeriod processingPeriod;
  Distribution distribution;
  Facility facility;
  private ProductCategory category;

  private EpiInventory epiInventory;

  private FacilityVisit facilityVisit;
  private ProgramProduct programProduct;

  @Before
  public void setUp() throws Exception {
    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    zone = make(a(defaultDeliveryZone));
    deliveryZoneMapper.insert(zone);

    program1 = make(a(defaultProgram));
    programMapper.insert(program1);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));
    periodMapper.insert(processingPeriod);
    Long createdBy = 1L;

    distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1),
      with(DistributionBuilder.createdBy, createdBy)));
    distributionMapper.insert(distribution);

    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    epiInventory = new EpiInventory();
    facilityVisit = new FacilityVisit(facility, distribution);
    facilityVisitMapper.insert(facilityVisit);


    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    category = new ProductCategory("C1", "Category 1", 1);
    productCategoryMapper.insert(category);

    programProduct = new ProgramProduct(program1, product, 10, true);
    programProduct.setProductCategory(category);
    programProductMapper.insert(programProduct);

  }

  private List resultSetToList(ResultSet rs) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    List<Map<String, Object>> list = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> row = new HashMap<>(columns);
      for (int i = 1; i <= columns; ++i) {
        row.put(md.getColumnName(i), rs.getObject(i));
      }
      list.add(row);
    }
    return list;
  }

  @Test
  public void shouldInsertEpiInventoryLineItems() throws Exception {

    EpiInventoryLineItem lineItem = new EpiInventoryLineItem();
    lineItem.setFacilityVisitId(facilityVisit.getId());
    lineItem.setProgramProductId(programProduct.getId());
    lineItem.setProductName("name name");
    lineItem.setProductCode("code 1");
    lineItem.setProductDisplayOrder(2);
    lineItem.setIdealQuantity(76);
    lineItem.setCreatedBy(1L);

    Product product1 = make(a(ProductBuilder.defaultProduct, with(code, "P11")));
    productMapper.insert(product1);
    ProgramProduct programProduct1 = new ProgramProduct(program1, product1, 10, true);
    programProduct1.setProductCategory(category);
    programProductMapper.insert(programProduct1);

    EpiInventoryLineItem lineItem2 = new EpiInventoryLineItem();
    lineItem2.setFacilityVisitId(facilityVisit.getId());
    lineItem2.setProgramProductId(programProduct1.getId());
    lineItem2.setProductName("name name");
    lineItem2.setProductCode("code 2");
    lineItem2.setProductDisplayOrder(1);
    lineItem2.setIdealQuantity(76);
    lineItem2.setCreatedBy(1L);

    mapper.insertLineItem(lineItem);
    mapper.insertLineItem(lineItem2);

    List list;
    try (ResultSet resultSet = queryExecutor.execute("SELECT * FROM epi_inventory_line_items WHERE facilityVisitId = ?", facilityVisit.getId())) {
      list = resultSetToList(resultSet);
    }

    assertThat(list.size(), is(2));
  }

  @Test
  public void shouldUpdateEpiInventoryLineItems() throws Exception {

    EpiInventoryLineItem lineItem = new EpiInventoryLineItem();
    lineItem.setFacilityVisitId(facilityVisit.getId());
    lineItem.setProgramProductId(programProduct.getId());
    lineItem.setProductName("name name");
    lineItem.setProductCode("code 1");
    lineItem.setProductDisplayOrder(2);
    lineItem.setIdealQuantity(76);
    lineItem.setExistingQuantity(66);
    lineItem.setDeliveredQuantity(56);
    lineItem.setSpoiledQuantity(777);
    lineItem.setCreatedBy(1L);
    mapper.insertLineItem(lineItem);

    lineItem.setSpoiledQuantity(6767);
    lineItem.setExistingQuantity(77);
    lineItem.setDeliveredQuantity(78);
    lineItem.setModifiedBy(3L);

    mapper.updateLineItem(lineItem);

    List<EpiInventoryLineItem> updatedLineItems = mapper.getLineItemsBy(facilityVisit.getId());

    assertThat(updatedLineItems.size(), is(1));
    assertThat(updatedLineItems.get(0).getSpoiledQuantity(), is(lineItem.getSpoiledQuantity()));
    assertThat(updatedLineItems.get(0).getExistingQuantity(), is(lineItem.getExistingQuantity()));
    assertThat(updatedLineItems.get(0).getDeliveredQuantity(), is(lineItem.getDeliveredQuantity()));
    assertThat(updatedLineItems.get(0).getModifiedBy(), is(lineItem.getModifiedBy()));
  }

  @Test
  public void shouldGetAllLineItemsByFacilityVisitId() {
    EpiInventoryLineItem lineItem = new EpiInventoryLineItem();
    lineItem.setFacilityVisitId(facilityVisit.getId());
    lineItem.setProgramProductId(programProduct.getId());
    lineItem.setProductName("name name");
    lineItem.setProductCode("code 1");
    lineItem.setProductDisplayOrder(2);
    lineItem.setIdealQuantity(76);
    lineItem.setCreatedBy(1L);

    Product product1 = make(a(ProductBuilder.defaultProduct, with(code, "P11")));
    productMapper.insert(product1);
    ProgramProduct programProduct1 = new ProgramProduct(program1, product1, 10, true);
    programProduct1.setProductCategory(category);
    programProductMapper.insert(programProduct1);

    EpiInventoryLineItem lineItem2 = new EpiInventoryLineItem();
    lineItem2.setFacilityVisitId(facilityVisit.getId());
    lineItem2.setProgramProductId(programProduct1.getId());
    lineItem2.setProductName("name name");
    lineItem2.setProductCode("code 2");
    lineItem2.setProductDisplayOrder(1);
    lineItem2.setIdealQuantity(76);
    lineItem2.setCreatedBy(1L);

    mapper.insertLineItem(lineItem);
    mapper.insertLineItem(lineItem2);

    List<EpiInventoryLineItem> lineItems = mapper.getLineItemsBy(facilityVisit.getId());

    assertThat(lineItems, is(asList(lineItem2, lineItem)));
  }
}
