/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.defaultFacilityApprovedProduct;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_ID;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.fullSupply;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-requisition.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RnrLineItemMapperIT {
  public static final Long MODIFIED_BY = 1L;

  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private ProgramProductMapper programProductMapper;
  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private RnrLineItemMapper rnrLineItemMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProductCategoryMapper categoryMapper;

  private FacilityTypeApprovedProduct facilityTypeApprovedProduct;
  private Facility facility;
  private Rnr rnr;
  Program program;

  @Before
  public void setUp() {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
    programProductMapper.insert(programProduct);

    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);

    rnr = new Rnr(facility.getId(), PROGRAM_ID, processingPeriod.getId(), false, MODIFIED_BY, 1L);
    rnr.setStatus(INITIATED);
  }

  @Test
  public void shouldReturnRnrLineItemsByRnrId() {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    lineItem.setPacksToShip(20);
    lineItem.setPreviousStockInHandAvailable(true);
    lineItem.setBeginningBalance(5);
    lineItem.setFullSupply(true);
    rnrLineItemMapper.insert(lineItem);

    LossesAndAdjustments lossesAndAdjustmentsClinicReturn = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsTypeClinicReturn = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsTypeClinicReturn.setName("CLINIC_RETURN");
    lossesAndAdjustmentsClinicReturn.setType(lossesAndAdjustmentsTypeClinicReturn);
    lossesAndAdjustmentsClinicReturn.setQuantity(20);

    LossesAndAdjustments lossesAndAdjustmentsTransferIn = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsTypeTransferIn = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsTypeTransferIn.setName("TRANSFER_IN");
    lossesAndAdjustmentsTransferIn.setType(lossesAndAdjustmentsTypeTransferIn);
    lossesAndAdjustmentsTransferIn.setQuantity(45);

    lossesAndAdjustmentsMapper.insert(lineItem, lossesAndAdjustmentsClinicReturn);
    lossesAndAdjustmentsMapper.insert(lineItem, lossesAndAdjustmentsTransferIn);

    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

    assertThat(rnrLineItems.size(), is(1));
    RnrLineItem rnrLineItem = rnrLineItems.get(0);

    assertThat(rnrLineItem.getId(), is(lineItem.getId()));
    assertThat(rnrLineItem.getLossesAndAdjustments().size(), is(2));
    assertThat(rnrLineItem.getRnrId(), is(rnr.getId()));
    assertThat(rnrLineItem.getDosesPerMonth(), is(30));
    assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
    assertThat(rnrLineItem.getProduct(), is("Primary Name Tablet strength mg"));
    assertThat(rnrLineItem.getPacksToShip(), is(20));
    assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
    assertThat(rnrLineItem.getRoundToZero(), is(true));
    assertThat(rnrLineItem.getPackSize(), is(10));
    assertThat(rnrLineItem.getPrice().compareTo(new Money("12.5")), is(0));
    assertThat(rnrLineItem.getPreviousStockInHandAvailable(), is(true));
    assertThat(rnrLineItem.getBeginningBalance(), is(5));
    assertThat(rnrLineItem.getProductCategory(), is("Category 1"));
  }

  @Test
  public void shouldReturnNonFullSupplyLineItemsByRnrId() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem nonFullSupplyLineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    nonFullSupplyLineItem.setQuantityRequested(20);
    nonFullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    nonFullSupplyLineItem.setFullSupply(false);
    rnrLineItemMapper.insertNonFullSupply(nonFullSupplyLineItem);

    List<RnrLineItem> fetchedNonSupplyLineItems = rnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId(rnr.getId());

    assertThat(fetchedNonSupplyLineItems.size(), is(1));
    assertThat(fetchedNonSupplyLineItems.get(0).getQuantityRequested(), is(20));
    assertThat(fetchedNonSupplyLineItems.get(0).getReasonForRequestedQuantity(), is("More patients"));
    assertThat(fetchedNonSupplyLineItems.get(0).getProductCategory(), is("Category 1"));
  }

  @Test
  public void shouldReturnNonFullSupplyLineItemByRnrIdAndProductCode() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem nonFullSupplyLineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    nonFullSupplyLineItem.setQuantityRequested(20);
    nonFullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    nonFullSupplyLineItem.setFullSupply(false);
    rnrLineItemMapper.insertNonFullSupply(nonFullSupplyLineItem);

    RnrLineItem fetchedNonSupplyLineItem = rnrLineItemMapper.getExistingNonFullSupplyItemByRnrIdAndProductCode(rnr.getId(), nonFullSupplyLineItem.getProductCode());

    assertThat(fetchedNonSupplyLineItem.getQuantityRequested(), is(20));
    assertThat(fetchedNonSupplyLineItem.getReasonForRequestedQuantity(), is("More patients"));
    assertThat(fetchedNonSupplyLineItem.getProductCategory(), is("Category 1"));
  }

  @Test
  public void shouldUpdateRnrLineItem() {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem);
    Long anotherModifiedBy = 2L;
    lineItem.setModifiedBy(anotherModifiedBy);
    lineItem.setBeginningBalance(43);
    lineItem.setTotalLossesAndAdjustments(20);
    lineItem.setExpirationDate("12/2014");
    lineItem.setReasonForRequestedQuantity("Quantity Requested more in liu of coming rains");
    int updateCount = rnrLineItemMapper.update(lineItem);
    assertThat(updateCount, is(1));
    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

    assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
    assertThat(rnrLineItems.get(0).getTotalLossesAndAdjustments(), is(20));
    assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
    assertThat(rnrLineItems.get(0).getExpirationDate(), is("12/2014"));
    assertThat(rnrLineItems.get(0).getReasonForRequestedQuantity(),
      is("Quantity Requested more in liu of coming rains"));
  }


  @Test
  public void shouldInsertNonFullSupplyLineItem() {
    requisitionMapper.insert(rnr);
    RnrLineItem requisitionLineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    requisitionLineItem.setFullSupply(false);
    rnrLineItemMapper.insertNonFullSupply(requisitionLineItem);
    assertNotNull(requisitionLineItem.getId());
    List<RnrLineItem> nonFullSupplyLineItems = rnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId(rnr.getId());
    RnrLineItem nonFullSupply = nonFullSupplyLineItems.get(0);
    assertThat(nonFullSupply.getQuantityReceived(), is(0));
    assertThat(nonFullSupply.getQuantityDispensed(), is(0));
    assertThat(nonFullSupply.getBeginningBalance(), is(0));
    assertThat(nonFullSupply.getStockInHand(), is(0));
    assertThat(nonFullSupply.getTotalLossesAndAdjustments(), is(0));
    assertThat(nonFullSupply.getCalculatedOrderQuantity(), is(0));
    assertThat(nonFullSupply.getNewPatientCount(), is(0));
    assertThat(nonFullSupply.getStockOutDays(), is(0));
    assertThat(nonFullSupply.getNormalizedConsumption(), is(0));
    assertThat(nonFullSupply.getAmc(), is(0));
    assertThat(nonFullSupply.getMaxStockQuantity(), is(0));
    assertThat(nonFullSupply.getProductCategory(), is("Category 1"));

  }

  @Test
  public void shouldDeleteAllNonFullSupplyLineItemsForRnr() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = make(a(defaultRnrLineItem, with(fullSupply, false)));
    lineItem.setRnrId(rnr.getId());
    rnrLineItemMapper.insert(lineItem);

    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(fullSupply, true)));
    lineItem2.setRnrId(rnr.getId());
    rnrLineItemMapper.insert(lineItem2);

    rnrLineItemMapper.deleteAllNonFullSupplyForRequisition(rnr.getId());

    assertThat(rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId()).size(), is(1));
    assertThat(rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId()).get(0).getProductCode(),
      is(lineItem2.getProductCode()));
    assertThat(rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId()).get(0).getProductCategory(),
      is(lineItem2.getProductCategory()));
  }


  @Test
  public void shouldReturnCategoryCountForFullSupplyLineItems() {
    requisitionMapper.insert(rnr);
    boolean fullSupplyFlag = true;
    for (int index = 1; index <= 10; index++) {
      String productCode = "P" + index;
      ProductCategory category = new ProductCategory();
      category.setCode("C" + index);
      category.setName("Category " + index);
      category.setDisplayOrder(1);
      categoryMapper.insert(category);
      Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode),
        with(ProductBuilder.fullSupply, fullSupplyFlag)));
      product.setCategory(category);
      productMapper.insert(product);

      ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
      programProductMapper.insert(programProduct);

      FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
      facilityTypeApprovedProduct.setProgramProduct(programProduct);
      facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

      RnrLineItem item = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, 1L, 1L);
      rnrLineItemMapper.insert(item);
    }
    assertThat(rnrLineItemMapper.getCategoryCount(rnr, fullSupplyFlag), is(10));
  }

  @Test
  public void shouldUpdateApprovedQuantityAndRemarks() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem);
    rnr.setStatus(AUTHORIZED);

    lineItem.setQuantityApproved(23);
    lineItem.setRemarks("Updated Remarks");
    lineItem.setPacksToShip(2);
    rnrLineItemMapper.updateOnApproval(lineItem);

    RnrLineItem returnedRnrLineItem = rnrLineItemMapper.getRnrLineItemsByRnrId(lineItem.getRnrId()).get(0);

    assertThat(returnedRnrLineItem.getQuantityApproved(), is(23));
    assertThat(returnedRnrLineItem.getPacksToShip(), is(2));
    assertThat(returnedRnrLineItem.getRemarks(), is("Updated Remarks"));
  }
}
