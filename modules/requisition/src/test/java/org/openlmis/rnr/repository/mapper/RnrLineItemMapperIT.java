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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.defaultFacilityApprovedProduct;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.name;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_ID;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;

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
  @Autowired
  private RequisitionStatusChangeMapper requisitionStatusChangeMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;
  @Autowired
  QueryExecutor queryExecutor;

  private FacilityTypeApprovedProduct facilityTypeApprovedProduct;
  private Facility facility;
  private Rnr rnr;
  Program program;

  ProcessingPeriod processingPeriod;
  ProcessingPeriod processingPeriod2;
  ProcessingPeriod processingPeriod3;

  @Before
  public void setUp() {
    Product product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);


    ProductCategory category = new ProductCategory("C1", "Category 1", 1);
    productCategoryMapper.insert(category);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
    programProduct.setProductCategory(category);
    programProduct.setFullSupply(product.getFullSupply());
    programProductMapper.insert(programProduct);

    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId()), with(name, "month1")));
    processingPeriodMapper.insert(processingPeriod);

    processingPeriod2 = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId()), with(name, "month2")));
    processingPeriodMapper.insert(processingPeriod2);

    processingPeriod3 = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId()), with(name, "month3")));
    processingPeriodMapper.insert(processingPeriod3);

    rnr = new Rnr(facility, new Program(PROGRAM_ID), processingPeriod, false, MODIFIED_BY, 1L);
    rnr.setStatus(INITIATED);
  }

  @Test
  public void shouldReturnRnrLineItemsByRnrId() {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    lineItem.setPacksToShip(20);
    lineItem.setBeginningBalance(5);
    lineItem.setFullSupply(true);
    lineItem.setReportingDays(10);
    lineItem.setPreviousStockInHand(5);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

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
    assertThat(rnrLineItem.getBeginningBalance(), is(5));
    assertThat(rnrLineItem.getPreviousStockInHand(), is(5));
    assertThat(rnrLineItem.getProductCategory(), is("Category 1"));
    assertThat(rnrLineItem.getReportingDays(), is(10));
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
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    Long anotherModifiedBy = 2L;
    lineItem.setModifiedBy(anotherModifiedBy);
    lineItem.setBeginningBalance(43);
    lineItem.setTotalLossesAndAdjustments(20);
    lineItem.setNormalizedConsumption(12);
    lineItem.setPeriodNormalizedConsumption(12);
    lineItem.setExpirationDate("12/2014");
    lineItem.setReasonForRequestedQuantity("Quantity Requested more in liu of coming rains");
    lineItem.setReportingDays(5);

    int updateCount = rnrLineItemMapper.update(lineItem);

    assertThat(updateCount, is(1));
    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

    assertThat(rnrLineItems.get(0).getBeginningBalance(), is(43));
    assertThat(rnrLineItems.get(0).getTotalLossesAndAdjustments(), is(20));
    assertThat(rnrLineItems.get(0).getProduct(), is("Primary Name Tablet strength mg"));
    assertThat(rnrLineItems.get(0).getNormalizedConsumption(), is(12));
    assertThat(rnrLineItems.get(0).getPeriodNormalizedConsumption(), is(12));
    assertThat(rnrLineItems.get(0).getExpirationDate(), is("12/2014"));
    assertThat(rnrLineItems.get(0).getReportingDays(), is(5));
    assertThat(rnrLineItems.get(0).getReasonForRequestedQuantity(),
      is("Quantity Requested more in liu of coming rains"));
  }

  @Test
  public void shouldUpdateSkipFlag() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    lineItem.setSkipped(true);

    rnrLineItemMapper.update(lineItem);
    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());
    assertTrue(rnrLineItems.get(0).getSkipped());

    lineItem.setSkipped(false);

    rnrLineItemMapper.update(lineItem);
    rnrLineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());
    assertFalse(rnrLineItems.get(0).getSkipped());
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
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(fullSupply, true)));
    lineItem2.setRnrId(rnr.getId());
    rnrLineItemMapper.insert(lineItem2, lineItem.getPreviousNormalizedConsumptions().toString());

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
      category.setCode("C_" + index);
      category.setName("Category " + index);
      category.setDisplayOrder(1);
      categoryMapper.insert(category);
      Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, productCode),
        with(ProductBuilder.fullSupply, fullSupplyFlag)));
      productMapper.insert(product);

      ProgramProduct programProduct = new ProgramProduct(program, product, 30, true, new Money("12.5000"));
      programProduct.setFullSupply(product.getFullSupply());
      programProduct.setProductCategory(category);
      programProductMapper.insert(programProduct);

      FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
      facilityTypeApprovedProduct.setProgramProduct(programProduct);
      facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

      RnrLineItem item = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, 1L, 1L);
      rnrLineItemMapper.insert(item, null);
    }
    assertThat(rnrLineItemMapper.getCategoryCount(rnr, fullSupplyFlag), is(10));
  }

  @Test
  public void shouldUpdateApprovedQuantityAndRemarks() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
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

  @Test
  public void shouldGetCreatedDateForMostRecentNonSkippedAuthorizedLineItem() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));
    queryExecutor.executeUpdate("UPDATE requisition_status_changes SET createdDate = ? WHERE rnrId = ?", getDateByDays(-3), rnr.getId());

    Rnr rnr1 = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2)));
    requisitionMapper.insert(rnr1);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(rnr1.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr1.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr1));

    List<RequisitionStatusChange> changeList = requisitionStatusChangeMapper.getByRnrId(rnr1.getId());

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    Date createdDateForPreviousLineItem = rnrLineItemMapper.getAuthorizedDateForPreviousLineItem(currentRnr, lineItem.getProductCode(), getDateByDays(-4));

    assertThat(createdDateForPreviousLineItem, is(changeList.get(0).getCreatedDate()));
  }

  @Test
  public void shouldNotGetCreatedDateIfNoAuthorizedLineItemExistAfterGivenDateForAProduct() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));
    queryExecutor.executeUpdate("UPDATE requisition_status_changes SET createdDate = ? WHERE rnrId = ?", getDateByDays(-3), rnr.getId());

    Rnr rnr1 = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2)));
    requisitionMapper.insert(rnr1);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(rnr1.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr1.setStatus(SUBMITTED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr1));

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    Date createdDateForPreviousLineItem = rnrLineItemMapper.getAuthorizedDateForPreviousLineItem(currentRnr, lineItem.getProductCode(), getDateByDays(-2));

    assertNull(createdDateForPreviousLineItem);
  }

  @Test
  public void shouldGetPreviousNRnrLineItemsForGivenProductCode() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    lineItem.setNormalizedConsumption(3);
    lineItem.setStockInHand(0);
    rnrLineItemMapper.update(lineItem);

    rnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    Rnr rnr1 = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2)));
    requisitionMapper.insert(rnr1);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(rnr1.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr1.setStatus(SUBMITTED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr1));

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getAuthorizedRegularUnSkippedLineItems(lineItem.getProductCode(), currentRnr, 2, getDateByDays(-3));

    assertThat(rnrLineItems.size(), is(1));
    assertThat(rnrLineItems.get(0).getNormalizedConsumption(), is(3));
    assertThat(rnrLineItems.get(0).getStockInHand(), is(0));
  }

  @Test
  public void shouldNotGetPreviousNNormalizedConsumptionsGivenProductCodeIfNoAuthorizedRnrExist() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    lineItem.setNormalizedConsumption(3);
    rnrLineItemMapper.update(lineItem);

    rnr.setStatus(SUBMITTED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    Rnr rnr1 = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2)));
    requisitionMapper.insert(rnr1);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(rnr1.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    rnr1.setStatus(SUBMITTED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr1));

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getAuthorizedRegularUnSkippedLineItems(lineItem.getProductCode(), currentRnr, 2, getDateByDays(-3));

    assertThat(rnrLineItems.size(), is(0));
  }

  @Test
  public void shouldGetPreviousNNormalizedConsumptionsGivenProductCodeIfTwoRequisitionsAuthorized() {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    lineItem.setNormalizedConsumption(3);
    rnrLineItemMapper.update(lineItem);

    rnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    Rnr rnr1 = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2)));
    requisitionMapper.insert(rnr1);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(rnr1.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItem.setNormalizedConsumption(9);
    rnrLineItemMapper.update(rnrLineItem);

    rnr1.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr1));

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    List<RnrLineItem> rnrLineItems = rnrLineItemMapper.getAuthorizedRegularUnSkippedLineItems(lineItem.getProductCode(), currentRnr, 2, getDateByDays(-3));

    assertThat(rnrLineItems.size(), is(2));

    assertContainsLineItemWithNC(rnrLineItems, 3);
    assertContainsLineItemWithNC(rnrLineItems, 9);
  }

  private void assertContainsLineItemWithNC(List<RnrLineItem> rnrLineItems, final Integer expectedNormalizedConsumption) {
    assertTrue(CollectionUtils.exists(rnrLineItems, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RnrLineItem lineItem = (RnrLineItem) o;
        return lineItem.getNormalizedConsumption().equals(expectedNormalizedConsumption);
      }
    }));
  }

  @Test
  public void shouldInsertPreviousNormalizedConsumptions() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    lineItem.setPreviousNormalizedConsumptions(asList(4, 6));
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    List<RnrLineItem> lineItems = rnrLineItemMapper.getRnrLineItemsByRnrId(rnr.getId());

    assertThat(lineItems.get(0).getPreviousNormalizedConsumptions(), is(asList(4, 6)));
  }

  @Test
  public void shouldGetLineItemWithRnrIdAndProductCode() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    lineItem.setPacksToShip(20);
    lineItem.setBeginningBalance(5);
    lineItem.setFullSupply(true);
    lineItem.setReportingDays(10);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    RnrLineItem actualLineItem = rnrLineItemMapper.getNonSkippedLineItem(rnr.getId(), facilityTypeApprovedProduct.getProgramProduct().getProduct().getCode());

    assertThat(actualLineItem, is(lineItem));
  }

  @Test
  public void shouldNotGetSkippedLineItemsWithRnrIdAndProductCode() throws Exception {
    requisitionMapper.insert(rnr);
    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    lineItem.setPacksToShip(20);
    lineItem.setBeginningBalance(5);
    lineItem.setFullSupply(true);
    lineItem.setReportingDays(10);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    lineItem.setSkipped(true);
    rnrLineItemMapper.update(lineItem);

    RnrLineItem actualLineItem = rnrLineItemMapper.getNonSkippedLineItem(rnr.getId(), facilityTypeApprovedProduct.getProgramProduct().getProduct().getCode());

    assertThat(actualLineItem, is(nullValue()));
  }

  @Test
  public void shouldNotIncludeEmergencyRnrWhileFetchingPreviousNC() throws Exception {
    requisitionMapper.insert(rnr);

    RnrLineItem lineItem = new RnrLineItem(rnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    rnrLineItemMapper.insert(lineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    lineItem.setNormalizedConsumption(3);
    rnrLineItemMapper.update(lineItem);

    rnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(rnr));

    Rnr emergencyRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod2),
      with(RequisitionBuilder.emergency, true)));
    requisitionMapper.insert(emergencyRnr);

    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    rnrLineItem.setRnrId(emergencyRnr.getId());
    rnrLineItemMapper.insert(rnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItem.setNormalizedConsumption(9);
    rnrLineItemMapper.update(rnrLineItem);

    emergencyRnr.setStatus(AUTHORIZED);
    requisitionStatusChangeMapper.insert(new RequisitionStatusChange(emergencyRnr));

    Rnr currentRnr = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.program, new Program(PROGRAM_ID)), with(RequisitionBuilder.period, processingPeriod3)));
    RnrLineItem currentRnrLineItem = make(a(defaultRnrLineItem, with(productCode, lineItem.getProductCode())));
    requisitionMapper.insert(currentRnr);

    currentRnrLineItem.setRnrId(currentRnr.getId());
    rnrLineItemMapper.insert(currentRnrLineItem, lineItem.getPreviousNormalizedConsumptions().toString());

    List<RnrLineItem> lineItems = rnrLineItemMapper.getAuthorizedRegularUnSkippedLineItems(lineItem.getProductCode(), currentRnr, 2, getDateByDays(-3));

    assertThat(lineItems.size(), is(1));
    assertThat(lineItems.get(0).getNormalizedConsumption(), is(3));
  }

  @Test
  public void shouldReturnAllNonSkippedNonFullSupplyRnrLineItems() {
    Rnr newRnr = new Rnr(facility, new Program(PROGRAM_ID), processingPeriod, false, MODIFIED_BY, 1L);
    newRnr.setStatus(INITIATED);

    requisitionMapper.insert(newRnr);
    RnrLineItem fullSupplyLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    fullSupplyLineItem.setQuantityRequested(20);
    fullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    fullSupplyLineItem.setFullSupply(false);
    fullSupplyLineItem.setSkipped(false);
    rnrLineItemMapper.insert(fullSupplyLineItem, fullSupplyLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(fullSupplyLineItem);


    RnrLineItem skippedLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    skippedLineItem.setFullSupply(false);
    skippedLineItem.setSkipped(true);
    rnrLineItemMapper.insert(skippedLineItem, skippedLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(skippedLineItem);

    RnrLineItem nonFullSupplyLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    nonFullSupplyLineItem.setQuantityRequested(20);
    nonFullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    nonFullSupplyLineItem.setFullSupply(true);
    nonFullSupplyLineItem.setSkipped(false);
    rnrLineItemMapper.insert(nonFullSupplyLineItem, nonFullSupplyLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(nonFullSupplyLineItem);

    List<RnrLineItem> fetchedNonSkippedNonSupplyLineItems = rnrLineItemMapper.getNonSkippedNonFullSupplyRnrLineItemsByRnrId(newRnr.getId());

    assertThat(fetchedNonSkippedNonSupplyLineItems.size(), is(1));
    assertThat(fetchedNonSkippedNonSupplyLineItems.get(0).getQuantityRequested(), is(20));
  }

  @Test
  public void shouldReturnAllNonSkippedFullSupplyRnrLineItems() {
    Rnr newRnr = new Rnr(facility, new Program(PROGRAM_ID), processingPeriod, false, MODIFIED_BY, 1L);
    newRnr.setStatus(INITIATED);

    requisitionMapper.insert(newRnr);
    RnrLineItem nonFullSupplyLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    nonFullSupplyLineItem.setQuantityRequested(20);
    nonFullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    nonFullSupplyLineItem.setFullSupply(true);
    nonFullSupplyLineItem.setSkipped(false);
    rnrLineItemMapper.insert(nonFullSupplyLineItem, nonFullSupplyLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(nonFullSupplyLineItem);

    RnrLineItem fullSupplyLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    fullSupplyLineItem.setQuantityRequested(20);
    fullSupplyLineItem.setReasonForRequestedQuantity("More patients");
    fullSupplyLineItem.setFullSupply(false);
    fullSupplyLineItem.setSkipped(false);
    rnrLineItemMapper.insert(fullSupplyLineItem, fullSupplyLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(fullSupplyLineItem);


    RnrLineItem skippedLineItem = new RnrLineItem(newRnr.getId(), facilityTypeApprovedProduct, MODIFIED_BY, 1L);
    skippedLineItem.setFullSupply(true);
    skippedLineItem.setSkipped(true);
    rnrLineItemMapper.insert(skippedLineItem, skippedLineItem.getPreviousNormalizedConsumptions().toString());
    rnrLineItemMapper.update(skippedLineItem);

    List<RnrLineItem> fetchedNonSkippedNonSupplyLineItems = rnrLineItemMapper.getNonSkippedNonFullSupplyRnrLineItemsByRnrId(newRnr.getId());

    assertThat(fetchedNonSkippedNonSupplyLineItems.size(), is(1));
    assertThat(fetchedNonSkippedNonSupplyLineItems.get(0).getQuantityRequested(), is(20));
  }

  private java.sql.Date getDateByDays(int days) {
    Calendar currentDate = Calendar.getInstance();
    currentDate.add(Calendar.DATE, days);
    return new java.sql.Date(currentDate.getTimeInMillis());
  }
}

