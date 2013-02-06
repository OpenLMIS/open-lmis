package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.STOCK_IN_HAND;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.BEGINNING_BALANCE;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RnrLineItem.class)
public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private RnrLineItem lineItem;
  private List<RnrColumn> templateColumns;
  private ProcessingPeriod period;

  @Before
  public void setUp() throws Exception {
    period = new ProcessingPeriod() {{
      setNumberOfMonths(1);
    }};
    templateColumns = new ArrayList<>();
    addVisibleColumns(templateColumns);
    lineItem = make(a(defaultRnrLineItem));
  }

  private void addVisibleColumns(List<RnrColumn> templateColumns) {
    RnrColumn beginningBalanceColumn = new RnrColumn();
    beginningBalanceColumn.setName(BEGINNING_BALANCE);
    beginningBalanceColumn.setVisible(true);
    beginningBalanceColumn.setFormulaValidationRequired(true);
    templateColumns.add(beginningBalanceColumn);

    RnrColumn quantityReceivedColumn = new RnrColumn();
    quantityReceivedColumn.setName(QUANTITY_RECEIVED);
    quantityReceivedColumn.setVisible(true);
    templateColumns.add(quantityReceivedColumn);

    RnrColumn quantityDispensedColumn = new RnrColumn();
    quantityDispensedColumn.setName(QUANTITY_DISPENSED);
    quantityDispensedColumn.setVisible(true);
    templateColumns.add(quantityDispensedColumn);

    RnrColumn newPatientCountColumn = new RnrColumn();
    newPatientCountColumn.setName(NEW_PATIENT_COUNT);
    newPatientCountColumn.setVisible(true);
    templateColumns.add(newPatientCountColumn);

    RnrColumn stockOutOfDaysColumn = new RnrColumn();
    stockOutOfDaysColumn.setName(STOCK_OUT_DAYS);
    stockOutOfDaysColumn.setVisible(true);
    templateColumns.add(stockOutOfDaysColumn);

    RnrColumn quantityRequestedColumn = new RnrColumn();
    quantityRequestedColumn.setName(QUANTITY_REQUESTED);
    quantityRequestedColumn.setVisible(true);
    templateColumns.add(quantityRequestedColumn);

    RnrColumn reasonForRequestedQuantityColumn = new RnrColumn();
    reasonForRequestedQuantityColumn.setName(REASON_FOR_REQUESTED_QUANTITY);
    reasonForRequestedQuantityColumn.setVisible(true);
    templateColumns.add(reasonForRequestedQuantityColumn);
  }

  @Test
  public void shouldConstructRnrLineItem() {

    Program program = make(a(ProgramBuilder.defaultProgram));
    Product product = make(a(ProductBuilder.defaultProduct, with(code, "ASPIRIN")));
    product.setDispensingUnit("Strip");

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    RnrLineItem rnrLineItem = new RnrLineItem(1, new FacilityApprovedProduct("warehouse", programProduct, 3), 1);

    assertThat(rnrLineItem.getFullSupply(), is(product.getFullSupply()));
    assertThat(rnrLineItem.getMaxMonthsOfStock(), is(3));
    assertThat(rnrLineItem.getRnrId(), is(1));
    assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
    assertThat(rnrLineItem.getProductCode(), is("ASPIRIN"));
    assertThat(rnrLineItem.getDosesPerMonth(), is(30));
    assertThat(rnrLineItem.getModifiedBy(), is(1));
    assertThat(rnrLineItem.getDosesPerDispensingUnit(), is(10));
  }

  @Test
  public void shouldThrowErrorIfBeginningBalanceNotPresent() throws Exception {
    lineItem.setBeginningBalance(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldThrowErrorIfQuantityReceivedNotPresent() throws Exception {
    lineItem.setQuantityReceived(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldThrowErrorIfQuantityConsumedNotPresent() throws Exception {
    lineItem.setQuantityDispensed(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldThrowErrorIfNewPatientsNotPresent() throws Exception {
    lineItem.setNewPatientCount(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldThrowErrorIfStockOutDaysNotPresent() throws Exception {
    lineItem.setStockOutDays(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldThrowErrorIfExplanationForRequestedQuantityNotPresent() throws Exception {
    lineItem.setQuantityRequested(70);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldNotThrowErrorForExplanationNotPresentIfRequestedQuantityNotSet() throws Exception {
    assertTrue(lineItem.validate(templateColumns));
  }

  @Test
  public void shouldThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(templateColumns);
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedTrue() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    assertTrue(lineItem.validate(templateColumns));
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    templateColumns.get(0).setFormulaValidationRequired(false);

    lineItem.validate(templateColumns);
  }


  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    assertTrue(lineItem.validate(templateColumns));
  }

  @Test
  public void shouldRecalculateTotalLossesAndAdjustments() throws Exception {
    LossesAndAdjustmentsType additive = new LossesAndAdjustmentsType();
    LossesAndAdjustmentsType subtractive = new LossesAndAdjustmentsType();
    additive.setAdditive(true);
    subtractive.setAdditive(false);
    LossesAndAdjustments add10 = new LossesAndAdjustments(1, additive, 10);
    LossesAndAdjustments sub5 = new LossesAndAdjustments(1, subtractive, 5);
    LossesAndAdjustments add20 = new LossesAndAdjustments(1, additive, 20);
    RnrLineItem lineItem = make(a(defaultRnrLineItem, with(lossesAndAdjustments, add10)));
    lineItem.addLossesAndAdjustments(sub5);
    lineItem.addLossesAndAdjustments(add20);
    lineItem.setTotalLossesAndAdjustments(20);
    lineItem.setQuantityDispensed(29);


    lineItem.calculate(period, INITIATED);

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(25));
  }

  @Test
  public void shouldRecalculateNormalizedConsumption() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);

    lineItem.calculate(period, INITIATED);

    assertThat(lineItem.getNormalizedConsumption(), is(37));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsThree() throws Exception {
    lineItem.setNormalizedConsumption(45);

    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(3);

    spyLineItem.calculate(period, INITIATED);

    assertThat(spyLineItem.getAmc(), is(15));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwo() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(2);

    spyLineItem.calculate(period, INITIATED);

    assertThat(spyLineItem.getAmc(), is(14));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOne() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12, 13));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculate(period, INITIATED);

    assertThat(spyLineItem.getAmc(), is(23));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyOnePreviousConsumptionIsAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);
    lineItem.setPreviousNormalizedConsumptions(asList(12));
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculate(period, INITIATED);

    assertThat(spyLineItem.getAmc(), is(29));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsOneAndOnlyNoPreviousConsumptionsAreAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);
    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(1);

    spyLineItem.calculate(period, INITIATED);

    assertThat(spyLineItem.getAmc(), is(45));
  }

  @Test
  public void shouldCalculateAmcWhenNumberOfMonthsInPeriodIsTwoAndPreviousConsumptionIsNotAvailable() throws Exception {
    lineItem.setNormalizedConsumption(45);

    RnrLineItem spyLineItem = spy(lineItem);
    doNothing().when(spyLineItem, "calculateNormalizedConsumption");
    period.setNumberOfMonths(2);

    spyLineItem.calculate(period, INITIATED);

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

    lineItem.calculate(period, INITIATED);

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

    lineItem.calculate(period, INITIATED);

    assertThat(lineItem.getCalculatedOrderQuantity(), is(70));
  }


  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    assertTrue(lineItem.validate(templateColumns));
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

    lineItem.copyApproverEditableFields(editedLineItem);

    assertThat(lineItem.getQuantityApproved(), is(1872));
    assertThat(lineItem.getRemarks(), is("Approved"));
    assertThat(lineItem.getStockInHand(), is(STOCK_IN_HAND));

  }

  @Test
  public void shouldCopyUserEditableFields() throws Exception {
    RnrLineItem editedLineItem = make(a(defaultRnrLineItem));
    editedLineItem.setRemarks("Submitted");
    editedLineItem.setBeginningBalance(12);
    editedLineItem.setQuantityReceived(23);
    editedLineItem.setQuantityDispensed(32);
    editedLineItem.setStockInHand(1946);
    editedLineItem.setNewPatientCount(1);
    editedLineItem.setStockOutDays(3);
    editedLineItem.setQuantityRequested(43);
    editedLineItem.setReasonForRequestedQuantity("Reason");
    List<LossesAndAdjustments> lossesAndAdjustments = new ArrayList<>();
    editedLineItem.setLossesAndAdjustments(lossesAndAdjustments);

    lineItem.copyUserEditableFieldsForSubmitOrAuthorize(editedLineItem);

    assertThat(lineItem.getBeginningBalance(), is(12));
    assertThat(lineItem.getStockInHand(), is(1946));
    assertThat(lineItem.getLossesAndAdjustments(), is(lossesAndAdjustments));
    assertThat(lineItem.getRemarks(), is("Submitted"));
    assertThat(lineItem.getQuantityReceived(), is(23));
    assertThat(lineItem.getQuantityDispensed(), is(32));
    assertThat(lineItem.getNewPatientCount(), is(1));
    assertThat(lineItem.getStockOutDays(), is(3));
    assertThat(lineItem.getQuantityRequested(), is(43));
    assertThat(lineItem.getReasonForRequestedQuantity(), is("Reason"));
  }


}
