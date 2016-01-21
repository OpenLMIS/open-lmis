/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.domain;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RnrColumnBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.NEW_PATIENT_COUNT;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;
import static org.openlmis.rnr.domain.Rnr.RNR_VALIDATION_ERROR;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RnrLineItem.class)
public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  Integer numberOfMonths;
  @Mock
  ProgramRnrTemplate template;
  @Mock
  RnrColumn column;
  private RnrLineItem lineItem;
  private List<RnrColumn> templateColumns;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;

  @Before
  public void setUp() throws Exception {
    templateColumns = new ArrayList<>();
    addVisibleColumns(templateColumns);
    lineItem = make(a(defaultRnrLineItem));
    template = new ProgramRnrTemplate(getRnrColumns());
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    LossesAndAdjustmentsType additive2 = new LossesAndAdjustmentsType("additive2", "Additive 2", true, 2);
    LossesAndAdjustmentsType subtractive1 = new LossesAndAdjustmentsType("subtractive1", "Subtractive 1", false, 3);
    LossesAndAdjustmentsType subtractive2 = new LossesAndAdjustmentsType("subtractive2", "Subtractive 2", false, 4);
    lossesAndAdjustmentsList = asList(additive1, additive2, subtractive1, subtractive2);
    numberOfMonths = 3;
  }

  @Test
  public void shouldSetFieldValuesToNullIfSkipped() {
    lineItem.setCalculatedOrderQuantity(null);
    lineItem.setSkipped(true);
    lineItem.setExpirationDate("some date");

    lineItem.setFieldsForApproval();

    assertThat(lineItem.getLossesAndAdjustments().size(), is(0));
    assertThat(lineItem.getTotalLossesAndAdjustments(), is(0));
    assertThat(lineItem.getQuantityDispensed(), is(nullValue()));
    assertThat(lineItem.getBeginningBalance(), is(nullValue()));
    assertThat(lineItem.getReasonForRequestedQuantity(), is(nullValue()));
    assertThat(lineItem.getStockInHand(), is(nullValue()));
    assertThat(lineItem.getStockOutDays(), is(nullValue()));
    assertThat(lineItem.getNewPatientCount(), is(nullValue()));
    assertThat(lineItem.getQuantityRequested(), is(nullValue()));
    assertThat(lineItem.getQuantityApproved(), is(nullValue()));
    assertThat(lineItem.getNormalizedConsumption(), is(nullValue()));
    assertThat(lineItem.getPeriodNormalizedConsumption(), is(nullValue()));
    assertThat(lineItem.getPacksToShip(), is(nullValue()));
    assertThat(lineItem.getRemarks(), is(nullValue()));
    assertThat(lineItem.getExpirationDate(), is(nullValue()));
    assertThat(lineItem.getQuantityApproved(), is(nullValue()));
  }

  @Test
  public void shouldConstructRnrLineItem() {

    Program program = make(a(defaultProgram));
    Product product = make(
      a(defaultProduct, with(code, "ASPIRIN")));
    product.setDispensingUnit("Strip");

    ProductCategory category = new ProductCategory("C1", "Category 1", 3);
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    programProduct.setDisplayOrder(9);
    programProduct.setProductCategory(category);
    programProduct.setFullSupply(product.getFullSupply());

    RnrLineItem rnrLineItem = new RnrLineItem(1L, new FacilityTypeApprovedProduct("warehouse", programProduct, 3.2), 1L,
      1L);

    assertThat(rnrLineItem.getFullSupply(), is(programProduct.isFullSupply()));
    assertThat(rnrLineItem.getMaxMonthsOfStock(), is(3.2));
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
  public void shouldThrowErrorIfBeginningBalanceIsNegative() throws Exception {
    lineItem.setBeginningBalance(-678);
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);

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
  public void shouldThrowErrorIfStockInHandNotPresentAndIsUserInput() throws Exception {
    lineItem.setStockInHand(null);
    addColumnToTemplate(templateColumns, STOCK_IN_HAND, true, false);
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
  public void shouldCalculateAMCAndMaxStockQuantityAndOrderedQuantityOnlyWhenAuthorized() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem).calculateNormalizedConsumption(template);

    spyLineItem.calculateForFullSupply(template, AUTHORIZED, lossesAndAdjustmentsList, numberOfMonths);

    verify(spyLineItem).calculateAmc(numberOfMonths);
    verify(spyLineItem).calculateMaxStockQuantity(template);
    verify(spyLineItem).calculateOrderQuantity();
  }

  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    lineItem.validateMandatoryFields(template);
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
    assertThat(lineItem.getStockInHand(), is(RnrLineItemBuilder.DEFAULT_STOCK_IN_HAND));
  }

  @Test
  public void shouldCopyTotalLossesAndAdjustments() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setTotalLossesAndAdjustments(10);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(10));
  }


  @Test
  public void shouldCopyPreviousStockInHand() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setPreviousStockInHand(10);

    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getPreviousStockInHand(), is(10));
  }

  @Test
  public void shouldCopyEditableFieldsForNonFullSupplyBasedOnTemplate() throws Exception {
    lineItem.copyCreatorEditableFieldsForNonFullSupply(make(a(defaultRnrLineItem, with(quantityRequested, 9),
      with(reasonForRequestedQuantity, "no reason"), with(remarks, "no remarks"))),
      new ProgramRnrTemplate(getRnrColumnsForNonFullSupply()));

    assertThat(lineItem.getReasonForRequestedQuantity(), is("no reason"));
    assertThat(lineItem.getRemarks(), is("no remarks"));
    assertThat(lineItem.getQuantityRequested(), is(9));

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

    assertThat(lineItem.getBeginningBalance(), is(RnrLineItemBuilder.DEFAULT_BEGINNING_BALANCE));
    assertThat(lineItem.getStockOutDays(), is(RnrLineItemBuilder.STOCK_OUT_DAYS));
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
  public void shouldCalculateStockInHandIfInputTypeIsCalculated() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, QUANTITY_DISPENSED))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, NEW_PATIENT_COUNT),
        with(option, new RnrColumnOption("newPatientCount", "NPC")))));
    }};

    ProgramRnrTemplate rnrTemplate = new ProgramRnrTemplate(columns);
    spyLineItem.calculateForFullSupply(rnrTemplate, SUBMITTED, lossesAndAdjustmentsList, numberOfMonths);

    verify(spyLineItem).calculateStockInHand();
    verify(spyLineItem).calculateNormalizedConsumption(rnrTemplate);
    verify(spyLineItem).calculatePeriodNormalizedConsumption(eq(numberOfMonths));
    verify(spyLineItem).calculatePacksToShip();
  }

  @Test
  public void shouldNotCalculateStockInHandIfUserInput() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, QUANTITY_DISPENSED))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, NEW_PATIENT_COUNT),
        with(option, new RnrColumnOption("newPatientCount", "NPC")))));
    }};

    spyLineItem.calculateForFullSupply(new ProgramRnrTemplate(columns), SUBMITTED, lossesAndAdjustmentsList,
      numberOfMonths);

    verify(spyLineItem, never()).calculateStockInHand();
  }

  @Test
  public void shouldCalculateQuantityDispensedIfCalculated() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, CALCULATED), with(columnName, QUANTITY_DISPENSED))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, NEW_PATIENT_COUNT),
        with(option, new RnrColumnOption("newPatientCount", "NPC")))));
    }};

    spyLineItem.calculateForFullSupply(new ProgramRnrTemplate(columns), SUBMITTED, lossesAndAdjustmentsList,
      numberOfMonths);

    verify(spyLineItem).calculateQuantityDispensed();
  }

  @Test
  public void shouldNotCalculateQuantityDispensedIfUserInput() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, QUANTITY_DISPENSED))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, NEW_PATIENT_COUNT),
        with(option, new RnrColumnOption("newPatientCount", "NPC")))));
    }};

    spyLineItem.calculateForFullSupply(new ProgramRnrTemplate(columns), SUBMITTED, lossesAndAdjustmentsList,
      numberOfMonths);

    verify(spyLineItem, never()).calculateQuantityDispensed();
  }

  @Test
  public void shouldNotCalculateOrderQuantityIfUserInput() throws Exception {
    RnrLineItem spyLineItem = spy(lineItem);
    ArrayList<RnrColumn> columns = new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, STOCK_IN_HAND))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, QUANTITY_DISPENSED))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, CALCULATED_ORDER_QUANTITY))));
      add(make(a(defaultRnrColumn, with(source, USER_INPUT), with(columnName, NEW_PATIENT_COUNT),
              with(option, new RnrColumnOption("newPatientCount", "NPC")))));
    }};

    spyLineItem.calculateForFullSupply(new ProgramRnrTemplate(columns), AUTHORIZED, lossesAndAdjustmentsList,
            numberOfMonths);

    verify(spyLineItem, never()).calculateOrderQuantity();
  }


  @Test
  public void shouldCopyBeginningBalanceIfItIsVisible() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setBeginningBalance(44);
    template.getRnrColumnsMap().get(BEGINNING_BALANCE).setVisible(true);
    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, template);

    assertThat(lineItem.getBeginningBalance(), is(editedLineItem.getBeginningBalance()));
  }

  @Test
  public void shouldNotCopyQuantityApprovedWhileCopyingNonApproverEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem, with(quantityApproved, 89)));
    lineItem.copyCreatorEditableFieldsForFullSupply(editedLineItem, new ProgramRnrTemplate(getRnrColumns()));

    assertThat(lineItem.getQuantityApproved(), is(RnrLineItemBuilder.QUANTITY_APPROVED));
  }

  @Test
  public void shouldValidateLineItemForApproval() throws Exception {
    lineItem.setQuantityApproved(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_VALIDATION_ERROR);

    lineItem.validateForApproval();
  }

  @Test
  public void shouldCalculatePacksToShipWhenPackRoundingThresholdIsSmallerThanRemainder() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(26);
    lineItem.setPackSize(10);
    lineItem.setPackRoundingThreshold(4);
    lineItem.setRoundToZero(false);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(3));
  }

  @Test
  public void shouldCalculatePacksToShipWhenPackRoundingThresholdIsGreaterThanRemainder() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(26);
    lineItem.setPackSize(10);
    lineItem.setPackRoundingThreshold(7);
    lineItem.setRoundToZero(false);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(2));
  }

  @Test
  public void shouldCalculatePacksToShipWhenCanRoundToZero() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(6);
    lineItem.setPackSize(10);
    lineItem.setPackRoundingThreshold(7);
    lineItem.setRoundToZero(true);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(0));
  }

  @Test
  public void shouldCalculatePacksToShipWhenCanNotRoundToZero() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(6);
    lineItem.setPackSize(10);
    lineItem.setPackRoundingThreshold(7);
    lineItem.setRoundToZero(false);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(1));
  }

  @Test
  public void shouldReturnNullPacksToShipIfPackSizeIsNull() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(6);
    lineItem.setPackRoundingThreshold(7);
    lineItem.setRoundToZero(true);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(nullValue()));
  }

  @Test
  public void shouldReturnOnePackToShipIfOrderQuantityIsZeroAndRoundToZeroFalse() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(0);
    lineItem.setPackSize(10);
    lineItem.setRoundToZero(false);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(1));
  }

  @Test
  public void shouldReturnZeroPackToShipIfOrderQuantityIsZeroAndRoundToZeroTrue() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(0);
    lineItem.setPackSize(10);
    lineItem.setRoundToZero(true);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(0));
  }

  @Test
  public void shouldReturnZeroPackToShipIfOrderQuantityIsOneAndRoundToZeroTrueWithPackSizeTen() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setCalculatedOrderQuantity(1);
    lineItem.setPackSize(10);
    lineItem.setPackRoundingThreshold(7);
    lineItem.setRoundToZero(true);

    lineItem.calculatePacksToShip();

    assertThat(lineItem.getPacksToShip(), is(0));
  }

  @Test
  public void shouldCalculateMaxStockQuantity() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setMaxMonthsOfStock(2.4);
    lineItem.setAmc(5);

    lineItem.calculateMaxStockQuantity(template);

    assertThat(lineItem.getMaxStockQuantity(), is(12));
  }

  @Test
  public void shouldReturnOrderedQuantityZeroIfStockInHandExceedsMaxStockQuantity() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setMaxStockQuantity(10);
    lineItem.setStockInHand(11);

    lineItem.calculateOrderQuantity();

    assertThat(lineItem.getCalculatedOrderQuantity(), is(0));
  }

  @Test
  public void shouldReturnOrderedQuantityIfStockInHandIsLessThanMaxStockQuantity() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setMaxStockQuantity(11);
    lineItem.setStockInHand(10);

    lineItem.calculateOrderQuantity();

    assertThat(lineItem.getCalculatedOrderQuantity(), is(1));
  }

  @Test
  public void shouldReturnNullOrderedQuantityIfStockInHandNull() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setMaxStockQuantity(11);

    lineItem.calculateOrderQuantity();

    assertThat(lineItem.getCalculatedOrderQuantity(), is(nullValue()));
  }

  @Test
  public void shouldSetQuantityApprovedEqualToOrderedQuantityIfFullSupply() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setFullSupply(true);
    lineItem.setSkipped(false);
    lineItem.setCalculatedOrderQuantity(10);
    lineItem.setQuantityRequested(20);

    lineItem.setFieldsForApproval();

    assertThat(lineItem.getQuantityApproved(), is(20));
  }

  @Test
  public void shouldSetQuantityApprovedEqualToRequestedQuantityIfNonFullSupply() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setFullSupply(false);
    lineItem.setSkipped(false);
    lineItem.setCalculatedOrderQuantity(10);
    lineItem.setQuantityRequested(20);

    lineItem.setFieldsForApproval();

    assertThat(lineItem.getQuantityApproved(), is(20));
  }

  @Test
  public void shouldCalculateQuantityDispensedIfAllInputPresent() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setBeginningBalance(1);
    lineItem.setQuantityReceived(2);
    lineItem.setTotalLossesAndAdjustments(3);
    lineItem.setStockInHand(4);

    lineItem.calculateQuantityDispensed();

    assertThat(lineItem.getQuantityDispensed(), is(2));
  }

  @Test
  public void shouldReturnNullQuantityDispensedIfAnyInputIsNull() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setBeginningBalance(1);
    lineItem.setQuantityReceived(2);
    lineItem.setTotalLossesAndAdjustments(3);
    lineItem.setStockInHand(null);

    lineItem.calculateQuantityDispensed();

    assertThat(lineItem.getQuantityDispensed(), is(nullValue()));
  }

  @Test
  public void shouldCalculateStockInHand() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setBeginningBalance(1);
    lineItem.setQuantityReceived(2);
    lineItem.setTotalLossesAndAdjustments(3);
    lineItem.setQuantityDispensed(4);

    lineItem.calculateStockInHand();

    assertThat(lineItem.getStockInHand(), is(2));
  }

  @Test
  public void shouldCalculateAmc() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setNormalizedConsumption(10);
    lineItem.setPreviousNormalizedConsumptions(asList(10, 20));

    lineItem.calculateAmc(1);
    assertThat(lineItem.getAmc(), is(13));
  }

  @Test
  public void shouldCalculatePeriodNC() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setNormalizedConsumption(10);

    lineItem.calculatePeriodNormalizedConsumption(3);
    assertThat(lineItem.getPeriodNormalizedConsumption(), is(30));
  }

  @Test
  public void shouldCalculateNormalizedConsumptionForNewPatientCount() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setStockOutDays(1);
    lineItem.setQuantityDispensed(2);
    lineItem.setNewPatientCount(1);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setReportingDays(30);

    lineItem.calculateNormalizedConsumption(template);

    assertThat(lineItem.getNormalizedConsumption(), is(5));
  }

  @Test
  public void shouldCalculateNormalizedConsumptionForDispensingUnitsNewPatients() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setStockOutDays(1);
    lineItem.setQuantityDispensed(2);
    lineItem.setNewPatientCount(1);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setReportingDays(30);

    template.getRnrColumnsMap().get("newPatientCount").getConfiguredOption().setName("dispensingUnit");
    lineItem.calculateNormalizedConsumption(template);

    assertThat(lineItem.getNormalizedConsumption(), is(3));
  }

  @Test
  public void shouldCalculateNCIfReportingDaysAreLessThanZero() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setStockOutDays(30);
    lineItem.setQuantityDispensed(1);
    lineItem.setNewPatientCount(1);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setReportingDays(30);

    lineItem.calculateNormalizedConsumption(template);

    assertThat(lineItem.getNormalizedConsumption(), is(4));
  }

  @Test
  public void shouldCalculateNCIfGIsZero() throws Exception {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setStockOutDays(0);
    lineItem.setQuantityDispensed(1);
    lineItem.setNewPatientCount(1);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(1);
    lineItem.setReportingDays(30);

    lineItem.calculateNormalizedConsumption(template);

    assertThat(lineItem.getNormalizedConsumption(), is(31));
  }

  @Test
  public void shouldSetBeginningBalanceWhenPreviousStockInHandAvailableAndColumnVisible() {
    RnrLineItem lineItem = new RnrLineItem();
    RnrLineItem previousRnrLineItem = new RnrLineItem();
    previousRnrLineItem.setStockInHand(100);

    lineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousRnrLineItem, true);

    assertThat(lineItem.getBeginningBalance(), is(100));
    assertThat(lineItem.getPreviousStockInHand(), is(100));
  }

  @Test
  public void shouldSetBeginningBalanceAsZeroWhenPreviousRnrLineItemSkippedAndColumnNotVisible() {
    RnrLineItem lineItem = new RnrLineItem();
    RnrLineItem previousRnrLineItem = new RnrLineItem();
    previousRnrLineItem.setSkipped(true);

    lineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(previousRnrLineItem, false);

    assertThat(lineItem.getBeginningBalance(), is(0));
    assertThat(lineItem.getPreviousStockInHand(), is(nullValue()));
  }

  @Test
  public void shouldSetBeginningBalanceAsZeroWhenPreviousRnrLineItemNotAvailableAndColumnVisible() {
    RnrLineItem lineItem = new RnrLineItem();

    lineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(null, true);

    assertThat(lineItem.getBeginningBalance(), is(0));
    assertThat(lineItem.getPreviousStockInHand(), is(nullValue()));
  }

  @Test
  public void shouldSetBeginningBalanceAsZeroWhenPreviousRnrLineItemNotAvailableAndColumnNotVisible() {
    RnrLineItem lineItem = new RnrLineItem();

    lineItem.setBeginningBalanceWhenPreviousStockInHandAvailable(null, false);

    assertThat(lineItem.getBeginningBalance(), is(0));
    assertThat(lineItem.getPreviousStockInHand(), is(nullValue()));
  }

  @Test
  public void shouldNotUpdateTotalLossesAndAdjustmentsFieldIfLossesAndAdjustmentsAreNullAndTotalHaveValue() {
    RnrLineItem lineItem = new RnrLineItem();
    lineItem.setTotalLossesAndAdjustments(10);
    lineItem.calculateTotalLossesAndAdjustments(new ArrayList<LossesAndAdjustmentsType>());
    assertEquals(10, lineItem.getTotalLossesAndAdjustments().intValue());
  }


  private ArrayList<RnrColumn> getRnrColumns() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_RECEIVED), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, QUANTITY_DISPENSED), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, LOSSES_AND_ADJUSTMENTS), with(visible, true))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.NEW_PATIENT_COUNT), with(visible, false),
        with(option, new RnrColumnOption("newPatientCount", "NPC")))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.NEW_PATIENT_COUNT), with(visible, false), with(option, new RnrColumnOption(ProgramRnrTemplate.NEW_PATIENT_COUNT, ProgramRnrTemplate.NEW_PATIENT_COUNT) ) )));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.STOCK_OUT_DAYS), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, STOCK_IN_HAND), with(visible, false),
        with(source, CALCULATED))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.BEGINNING_BALANCE), with(visible, false))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true),
        with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_APPROVED), with(visible, true),
        with(source, USER_INPUT))));

    }};
  }

  private ArrayList<RnrColumn> getRnrColumnsForNonFullSupply() {
    return new ArrayList<RnrColumn>() {{
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.QUANTITY_REQUESTED), with(visible, true),
        with(source, USER_INPUT))));
      add(make(a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REMARKS), with(visible, true),
        with(source, USER_INPUT))));
      add(make(
        a(defaultRnrColumn, with(columnName, ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY), with(visible, true),
          with(source, USER_INPUT))));
    }};
  }

  private void addVisibleColumns(List<RnrColumn> templateColumns) {
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.BEGINNING_BALANCE, true, true);
    addColumnToTemplate(templateColumns, QUANTITY_DISPENSED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.QUANTITY_RECEIVED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.NEW_PATIENT_COUNT, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.STOCK_OUT_DAYS, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.QUANTITY_REQUESTED, true, null);
    addColumnToTemplate(templateColumns, ProgramRnrTemplate.REASON_FOR_REQUESTED_QUANTITY, true, null);
  }

  private void addColumnToTemplate(List<RnrColumn> templateColumns,
                                   String columnName,
                                   Boolean visible,
                                   Boolean formulaValidation) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setName(columnName);
    rnrColumn.setVisible(visible);
    if (formulaValidation != null) rnrColumn.setFormulaValidationRequired(formulaValidation);
    templateColumns.add(rnrColumn);
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
}
