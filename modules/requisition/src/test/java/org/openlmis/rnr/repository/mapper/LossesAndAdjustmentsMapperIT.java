/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class LossesAndAdjustmentsMapperIT {
  public static final Long MODIFIED_BY = 1L;
  public static final Long HIV = 1L;

  @Autowired
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;

  private RnrLineItem rnrLineItem;
  private LossesAndAdjustments lossAndAdjustment;

  @Before
  public void setUp() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    ProductCategory category = new ProductCategory("C1", "Category Name", 1);
    productCategoryMapper.insert(category);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5"));
    programProduct.setProductCategory(category);
    programProductMapper.insert(programProduct);

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct("warehouse",
      programProduct, 3.3);
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);

    Rnr requisition = new Rnr(facility, new Program(HIV), processingPeriod, false, MODIFIED_BY, 1L);
    requisition.setStatus(INITIATED);
    requisitionMapper.insert(requisition);

    rnrLineItem = new RnrLineItem(requisition.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(rnrLineItem, null);
    lossAndAdjustment = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsType = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsType.setName("CLINIC_RETURN");
    lossAndAdjustment.setType(lossesAndAdjustmentsType);
    lossAndAdjustment.setQuantity(20);
  }

  @Test
  public void shouldInsertLossesAndAdjustments() {
    lossesAndAdjustmentsMapper.insert(rnrLineItem, lossAndAdjustment);

    List<LossesAndAdjustments> lossesAndAdjustmentsList = lossesAndAdjustmentsMapper.getByRnrLineItem(
      rnrLineItem.getId());
    LossesAndAdjustments lineItemLossAndAdjustment = lossesAndAdjustmentsList.get(0);

    assertThat(lossesAndAdjustmentsList.size(), is(1));
    assertThat(lineItemLossAndAdjustment.getQuantity(), is(lossAndAdjustment.getQuantity()));
    assertThat(lineItemLossAndAdjustment.getType().getName(), is(lossAndAdjustment.getType().getName()));
    assertThat(lineItemLossAndAdjustment.getModifiedBy(), is(MODIFIED_BY));
    assertThat(lineItemLossAndAdjustment.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldDeleteLossesAndAdjustmentForLineItem() throws Exception {
    lossesAndAdjustmentsMapper.insert(rnrLineItem, lossAndAdjustment);
    lossesAndAdjustmentsMapper.deleteByLineItemId(rnrLineItem.getId());
    assertThat(lossesAndAdjustmentsMapper.getByRnrLineItem(rnrLineItem.getId()).size(), is(0));
  }

  @Test
  public void shouldReturnAllLossesAndAdjustmentsTypesAccordingToDisplayOrder() {
    List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes = lossesAndAdjustmentsMapper.getLossesAndAdjustmentsTypes();
    assertThat(lossesAndAdjustmentsTypes.size(), is(9));
    assertThat(lossesAndAdjustmentsTypes.get(0).getDisplayOrder(), is(1));
    assertThat(lossesAndAdjustmentsTypes.get(1).getDisplayOrder(), is(2));
    assertThat(lossesAndAdjustmentsTypes.get(2).getDisplayOrder(), is(3));
  }

}
