/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.productCategoryDisplayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.LOSSES_AND_ADJUSTMENTS;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;
import static org.openlmis.rnr.domain.RnrLineItem.RNR_VALIDATION_ERROR;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrLineItem.class)
public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private RnrLineItem lineItem;
  private List<RnrColumn> templateColumns;
  private ProcessingPeriod period;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;
  ProgramRnrTemplate template;

  @Before
  public void setUp() throws Exception {
    period = new ProcessingPeriod() {{
      setNumberOfMonths(1);
    }};
    templateColumns = new ArrayList<>();
    addVisibleColumns(templateColumns);
    lineItem = make(a(defaultRnrLineItem));
    template = new ProgramRnrTemplate(getRnrColumns());
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    LossesAndAdjustmentsType additive2 = new LossesAndAdjustmentsType("additive2", "Additive 2", true, 2);
    LossesAndAdjustmentsType subtractive1 = new LossesAndAdjustmentsType("subtractive1", "Subtractive 1", false, 3);
    LossesAndAdjustmentsType subtractive2 = new LossesAndAdjustmentsType("subtractive2", "Subtractive 2", false, 4);
    lossesAndAdjustmentsList = asList(
      new LossesAndAdjustmentsType[]{additive1, additive2, subtractive1, subtractive2});
  }

  private void addVisibleColumns(List<RnrColumn> templateColumns) {
    RnrColumn beginningBalanceColumn = new RnrColumn();
    beginningBalanceColumn.setName(ProgramRnrTemplate.BEGINNING_BALANCE);
    beginningBalanceColumn.setVisible(true);
    beginningBalanceColumn.setFormulaValidationRequired(true);
    templateColumns.add(beginningBalanceColumn);

    RnrColumn quantityReceivedColumn = new RnrColumn();
    quantityReceivedColumn.setName(ProgramRnrTemplate.QUANTITY_RECEIVED);
    quantityReceivedColumn.setVisible(true);
    templateColumns.add(quantityReceivedColumn);

    RnrColumn quantityDispensedColumn = new RnrColumn();
    quantityDispensedColumn.setName(ProgramRnrTemplate.QUANTITY_DISPENSED);
    quantityDispensedColumn.setVisible(true);
    templateColumns.add(quantityDispensedColumn);

    RnrColumn newPatientCountColumn = new RnrColumn();
    newPatientCountColumn.setName(ProgramRnrTemplate.NEW_PATIENT_COUNT);
    newPatientCountColumn.setVisible(true);
    templateColumns.add(newPatientCountColumn);

    RnrColumn stockOutOfDaysColumn = new RnrColumn();
    stockOutOfDaysColumn.setName(ProgramRnrTemplate.STOCK_OUT_DAYS);
    stockOutOfDaysColumn.setVisible(true);
    templateColumns.add(stockOutOfDaysColumn);

    RnrColumn quantityRequestedColumn = new RnrColumn();
    quantityRequestedColumn.setName(ProgramRnrTemplate.QUANTITY_REQUESTED);
    quantityRequestedColumn.setVisible(true);
    templateColumns.add(quantityRequestedColumn);

    RnrColumn reasonForRequestedQuantityColumn = new RnrColumn();
    reasonForRequestedQuantityColumn.setName(ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY);
    reasonForRequestedQuantityColumn.setVisible(true);
    templateColumns.add(reasonForRequestedQuantityColumn);
  }

  @Test
  public void shouldConstructRnrLineItem() {

    Program program = make(a(defaultProgram));
    Product product = make(
      a(defaultProduct, with(code, "ASPIRIN"), with(productCategoryDisplayOrder, 3), with(displayOrder, 9)));
    product.setDispensingUnit("Strip");

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    RnrLineItem rnrLineItem = new RnrLineItem(1L, new FacilityTypeApprovedProduct("warehouse", programProduct, 3), 1L);

    assertThat(rnrLineItem.getFullSupply(), is(product.getFullSupply()));
    assertThat(rnrLineItem.getMaxMonthsOfStock(), is(3));
    assertThat(rnrLineItem.getRnrId(), is(1L));
    assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
    assertThat(rnrLineItem.getProductCode(), is("ASPIRIN"));
    assertThat(rnrLineItem.getDosesPerMonth(), is(30));
    assertThat(rnrLineItem.getModifiedBy(), is(1L));
    assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
    assertThat(rnrLineItem.getProductCategoryDisplayOrder(), is(3));
    assertThat(rnrLineItem.getProductDisplayOrder(), is(9));
  }

  @Test
  public void shouldThrowErrorIfBeginningBalanceNotPresent() throws Exception {
    lineItem.setBeginningBalance(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfQuantityReceivedNotPresent() throws Exception {
    lineItem.setQuantityReceived(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfQuantityConsumedNotPresent() throws Exception {
    lineItem.setQuantityDispensed(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfNewPatientsNotPresent() throws Exception {
    lineItem.setNewPatientCount(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfStockOutDaysNotPresent() throws Exception {
    lineItem.setStockOutDays(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowErrorIfRequestedQuantityAndItsExplanationAreNull() throws Exception {
    lineItem.setQuantityRequested(null);
    lineItem.setReasonForRequestedQuantity(null);

    lineItem.validateMandatoryFields(template);
  }


  @Test
  public void shouldNotThrowErrorIfRequestedQuantityAndItsExplanationArePresent() {
    lineItem.setQuantityRequested(123);
    lineItem.setReasonForRequestedQuantity("something");

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowErrorIfRequestedQuantityIsPresentAndExplanationIsNotPresent() throws Exception {
    lineItem.setQuantityRequested(70);
    lineItem.setReasonForRequestedQuantity(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowErrorIfRequestedQuantityIsNullAndExplanationIsPresent() {
    lineItem.setQuantityRequested(null);
    lineItem.setReasonForRequestedQuantity("something");

    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);

    List<LossesAndAdjustments> list = Arrays.asList(createLossAndAdjustment("CLINIC_RETURN", true, 1));

    lineItem.setLossesAndAdjustments(list);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    lineItem.validateCalculatedFields(template);
  }

  private LossesAndAdjustments createLossAndAdjustment(String typeName, boolean additive, int quantity) {
    LossesAndAdjustments lossAndAdjustment = new LossesAndAdjustments();
    LossesAndAdjustmentsType lossesAndAdjustmentsType = new LossesAndAdjustmentsType();
    lossesAndAdjustmentsType.setName(typeName);
    lossesAndAdjustmentsType.setAdditive(additive);
    lossAndAdjustment.setType(lossesAndAdjustmentsType);
    lossAndAdjustment.setQuantity(quantity);
    return lossAndAdjustment;
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setLossesAndAdjustments(asList(createLossAndAdjustment("", true, 1)));
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    templateColumns.get(0).setFormulaValidationRequired(false);

    lineItem.validateMandatoryFields(template);
  }


  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldRecalculateTotalLossesAndAdjustments() throws Exception {
    LossesAndAdjustmentsType additive = new LossesAndAdjustmentsType();
    additive.setName("TRANSFER_IN");
    LossesAndAdjustmentsType subtractive = new LossesAndAdjustmentsType();
    subtractive.setName("subtractive1");
    LossesAndAdjustments add10 = new LossesAndAdjustments(1L, additive, 10);
    LossesAndAdjustments sub5 = new LossesAndAdjustments(1L, subtractive, 5);
    LossesAndAdjustments add20 = new LossesAndAdjustments(1L, additive, 20);
    RnrLineItem lineItem = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.lossesAndAdjustments, add10)));
    lineItem.addLossesAndAdjustments(sub5);
    lineItem.addLossesAndAdjustments(add20);
    lineItem.setTotalLossesAndAdjustments(20);
    lineItem.setQuantityDispensed(29);


    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(25));
  }

  @Test
  public void shouldRecalculateNormalizedConsumption() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);

    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getNormalizedConsumption(), is(37));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsThree() throws Exception {
    lineItem.setNormalizedConsumption(45);

    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(3);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(15));
  }

  @Test
  public void shouldCalculateAMCAndMaxStockQuantityAndOrderedQuantityOnlyWhenAuthorized() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    verify(spyLineItem).calculateAmc(period);
    verify(spyLineItem).calculateMaxStockQuantity();
    verify(spyLineItem).calculateOrderQuantity();
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwo() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(2);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(14));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOne() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12, 13));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(23));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyOnePreviousConsumptionIsAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(29));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyNoPreviousConsumptionsAreAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(45));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwoAndPreviousConsumptionIsNotAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);

    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(2);

    spyLineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(spyLineItem.getAmc(), is(23));
  }

  @Test
  public void shouldRecalculateMaxStockQuantityBasedOnCalculatedAMC() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);
    lineItem.setMaxMonthsOfStock(10);

    lineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(lineItem.getMaxStockQuantity(), is(370));
  }

  @Test
  public void shouldRecalculatedOrderQuantityBasedOnCalculatedMaxStockQuantityAndStockInHand() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);
    lineItem.setMaxMonthsOfStock(10);
    lineItem.setMaxStockQuantity(370);
    lineItem.setStockInHand(300);

    lineItem.calculateForFullSupply(period, template, AUTHORIZED, lossesAndAdjustmentsList);

    assertThat(lineItem.getCalculatedOrderQuantity(), is(366));
  }


  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    lineItem.validateMandatoryFields(template);
  }

  @Test
  public void shouldSetCalculatedOrderQuantityAsDefaultApprovedQuantityForFullSupplyItems() throws Exception {
    final int expected = 1;
    lineItem.setCalculatedOrderQuantity(expected);
    lineItem.setDefaultApprovedQuantity();
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldSetRequestedQuantityAsApprovedQuantityForNonFullSupplyItems() throws Exception {
    lineItem.setFullSupply(false);
    final int expected = 1;
    lineItem.setQuantityRequested(expected);
    lineItem.setDefaultApprovedQuantity();
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldCopyApproverEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setQuantityApproved(1872);
    editedLineItem.setRemarks("Approved");
    editedLineItem.setStockInHand(1946);
    ProgramRnrTemplate template = new ProgramRnrTemplate(getRnrColumns());
    lineItem.copyApproverEditableFields(editedLineItem, template);

    assertThat(lineItem.getQuantityApproved(), is(1872));
    assertThat(lineItem.getRemarks(), is("Approved"));
    assertThat(lineItem.getStockInHand(), is(RnrLineItemBuilder.STOCK_IN_HAND));
  }

  private ArrayList<RnrColumn> getRnrColumns() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_RECEIVED), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, LOSSES_AND_ADJUSTMENTS), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.NEW_PATIENT_COUNT), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.STOCK_OUT_DAYS), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.STOCK_IN_HAND), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.BEGINNING_BALANCE), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_APPROVED), with(visible, true),
        with(source, USER_INPUT))));
    }};
  }

  private ArrayList<RnrColumn> getRnrColumnsForNonFullSupply() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_REQUESTED), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true), with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY), with(visible, true),
        with(source, USER_INPUT))));
    }};
  }


  @Test
  public void shouldCopyUserEditableFieldsOnlyIfVisible() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setRemarks("Submitted");
    editedLineItem.setBeginningBalance(12);
    editedLineItem.setQuantityReceived(23);
    editedLineItem.setQuantityDispensed(32);
    editedLineItem.setStockInHand(1946);
    editedLineItem.setNewPatientCount(1);
    editedLineItem.setStockOutDays(7);
    editedLineItem.setQuantityRequested(43);
    editedLineItem.setReasonForRequestedQuantity("Reason");
    List<LossesAndAdjustments> lossesAndAdjustments = new ArrayList<>();
    editedLineItem.setLossesAndAdjustments(lossesAndAdjustments);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getBeginningBalance(), is(RnrLineItemBuilder.BEGINNING_BALANCE));
    assertThat(lineItem.getStockOutDays(), is(RnrLineItemBuilder.STOCK_OUT_DAYS));
  }

  @Test
  public void shouldCalculateStockInHandIfCalculated() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    lineItem.setStockInHand(99);
    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getStockInHand(), is(4));
  }

  @Test
  public void shouldNotCalculateStockInHandIfUserInput() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    ProgramRnrTemplate template = new ProgramRnrTemplate(columns);
    lineItem.setStockInHand(66);
    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getStockInHand(), is(66));
  }

  @Test
  public void shouldCalculateQuantityDispensedIfCalculated() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};
    ProgramRnrTemplate template = new ProgramRnrTemplate(columns);
    lineItem.setQuantityDispensed(4);
    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getQuantityDispensed(), is(10));
  }

  @Test
  public void shouldNotCalculateQuantityDispensedIfUserInput() throws Exception {
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, ProgramRnrTemplate.QUANTITY_DISPENSED))));
    }};

    lineItem.setQuantityDispensed(0);
    lineItem.calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsList);

    assertThat(lineItem.getQuantityDispensed(), is(0));
  }

  @Test
  public void shouldNotCopyBeginningBalanceWhenPreviousStockInHandIsAvailable() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setBeginningBalance(44);
    lineItem.setPreviousStockInHandAvailable(true);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(new ArrayList<RnrColumn>()));

    assertThat(lineItem.getBeginningBalance(), is(RnrLineItemBuilder.BEGINNING_BALANCE));
  }

  @Test
  public void shouldNotCopyQuantityApprovedWhileCopyingNonApproverEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem, with(quantityApproved, 89)));
    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getQuantityApproved(), is(RnrLineItemBuilder.QUANTITY_APPROVED));
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasRequestedQuantityAsNull() {
    Integer nullInteger = null;
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(quantityRequested, nullInteger)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasRequestedQuantityIsNegative() {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(quantityRequested, -10)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldThrowExceptionIfNonFullSupplyLineItemHasReasonForRequestedQuantityNull() {
    String nullString = null;
    RnrLineItem rnrLineItem = make(
      a(defaultRnrLineItem, with(RnrLineItemBuilder.reasonForRequestedQuantity, nullString)));
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    rnrLineItem.validateNonFullSupply();
  }

  @Test
  public void shouldCalculateCostAsZeroIfPacksToShipIsNull() {
    Integer nullInteger = null;
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(packsToShip, nullInteger)));
    Money money = rnrLineItem.calculateCost();
    assertThat(money.getValue().intValue(), Is.is(0));
  }

  @Test
  public void shouldCalculateCostIfPacksToShipIsNotNull() {
    RnrLineItem rnrLineItem = make(a(defaultRnrLineItem, with(packsToShip, 5)));
    Money money = rnrLineItem.calculateCost();
    assertThat(money.getValue().intValue(), Is.is(20));
  }

  @Test
  public void shouldValidateLineItemForApproval() throws Exception {
    lineItem.setQuantityApproved(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);
    lineItem.validateForApproval();

  }

  @Test
  public void shouldCopyEditableFieldsForNonFullSupplyBasedOnTemplate() throws Exception {
    lineItem.copyCreatorEditableFieldsForNonFullSupply(make(a(defaultRnrLineItem, with(quantityRequested, 9),
      with(reasonForRequestedQuantity, "no reason"), with(remarks, "no remarks"))), new ProgramRnrTemplate(getRnrColumnsForNonFullSupply()));

    assertThat(lineItem.getReasonForRequestedQuantity(), is("no reason"));
    assertThat(lineItem.getRemarks(), is("no remarks"));
    assertThat(lineItem.getQuantityRequested(), is(9));

  }

  @Test
  public void shouldCopyTotalLossesAndAdjustments() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setTotalLossesAndAdjustments(10);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(10));
  }

}
