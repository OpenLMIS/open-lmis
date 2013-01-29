package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.lossesAndAdjustments;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;

public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private RnrLineItem lineItem;
  private List<RnrColumn> templateColumns;

  @Before
  public void setUp() throws Exception {
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
    assertThat(rnrLineItem.getMaxMonthsOfStock().intValue(), is(3));
    assertThat(rnrLineItem.getRnrId().intValue(), is(1));
    assertThat(rnrLineItem.getDispensingUnit(), is("Strip"));
    assertThat(rnrLineItem.getProductCode(), is("ASPIRIN"));
    assertThat(rnrLineItem.getDosesPerMonth().intValue(), is(30));
    assertThat(rnrLineItem.getModifiedBy().intValue(), is(1));
    assertThat(rnrLineItem.getDosesPerDispensingUnit().intValue(), is(10));
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


    lineItem.calculate();

    assertThat(lineItem.getTotalLossesAndAdjustments(), is(25));
  }


  @Test
  public void shouldRecalculateNormalizedConsumption() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);

    lineItem.calculate();

    assertThat(lineItem.getNormalizedConsumption(), is(37));
  }

  @Test
  public void shouldSetAMCSameAsCalculatedNormalizedConsumption() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);

    lineItem.calculate();

    assertThat(lineItem.getAmc(), is(37));
  }

  @Test
  public void shouldRecalculateMaxStockQuantityBasedOnCalculatedAMC() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37345);
    lineItem.setMaxMonthsOfStock(10);

    lineItem.calculate();

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

    lineItem.calculate();

    assertThat(lineItem.getCalculatedOrderQuantity(), is(70));
  }


  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    assertTrue(lineItem.validate(templateColumns));
  }

  @Test @Ignore
  public void shouldGetCalculatedOrderQuantityIfApprovedQuantityIsNullForFullSupplyItems() throws Exception {
    lineItem.setQuantityApproved(null);
    final int expected = 1;
    lineItem.setCalculatedOrderQuantity(expected);
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }

  @Test @Ignore
  public void shouldGetRequestedQuantityIfApprovedQuantityIsNullForNonFullSupplyItems() throws Exception {
    lineItem.setQuantityApproved(null);
    lineItem.setFullSupply(false);
    final int expected = 1;
    lineItem.setQuantityRequested(expected);
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }
}
