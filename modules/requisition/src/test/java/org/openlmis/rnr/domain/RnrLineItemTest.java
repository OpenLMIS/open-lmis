package org.openlmis.rnr.domain;

import org.junit.Before;
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

public class RnrLineItemTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private RnrLineItem lineItem;
  private boolean formulaValidated = false;

  @Before
  public void setUp() throws Exception {
    lineItem = make(a(defaultRnrLineItem));
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
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowErrorIfQuantityReceivedNotPresent() throws Exception {
    lineItem.setQuantityReceived(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowErrorIfQuantityConsumedNotPresent() throws Exception {
    lineItem.setQuantityDispensed(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowErrorIfNewPatientsNotPresent() throws Exception {
    lineItem.setNewPatientCount(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowErrorIfStockOutDaysNotPresent() throws Exception {
    lineItem.setStockOutDays(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowErrorIfExplanationForRequestedQuantityNotPresent() throws Exception {
    lineItem.setQuantityRequested(70);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldNotThrowErrorForExplanationNotPresentIfRequestedQuantityNotSet() throws Exception {
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedTrue() throws Exception {
    formulaValidated = true;
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedTrue() throws Exception {
    formulaValidated = true;
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandNotValidAndFormulaValidatedFalse() throws Exception {
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(9);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);
  }


  @Test
  public void shouldNotThrowExceptionIfCalculationForQuantityDispensedAndStockInHandValidAndFormulaValidatedFalse() throws Exception {
    formulaValidated = true;
    lineItem.setBeginningBalance(10);
    lineItem.setQuantityReceived(3);
    lineItem.setTotalLossesAndAdjustments(1);
    lineItem.setStockInHand(4);
    lineItem.setQuantityDispensed(10);
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldThrowExceptionIfCalculationForTotalLossesAndAdjustmentsNotValid() throws Exception {
    LossesAndAdjustmentsType additive = new LossesAndAdjustmentsType();
    LossesAndAdjustmentsType subtractive = new LossesAndAdjustmentsType();
    additive.setAdditive(true);
    subtractive.setAdditive(false);
    LossesAndAdjustments add10 = new LossesAndAdjustments(1, additive, 10);
    LossesAndAdjustments sub5 = new LossesAndAdjustments(1, subtractive, 5);
    LossesAndAdjustments add20 = new LossesAndAdjustments(1, additive, 20);
    lineItem.addLossesAndAdjustments(add10);
    lineItem.addLossesAndAdjustments(sub5);
    lineItem.addLossesAndAdjustments(add20);
    lineItem.setTotalLossesAndAdjustments(20);
    lineItem.setQuantityDispensed(29);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);

    lineItem.validate(formulaValidated);
  }


  @Test
  public void shouldNotThrowExceptionIfCalculationForTotalLossesAndAdjustmentsValid() throws Exception {
    LossesAndAdjustmentsType additive = new LossesAndAdjustmentsType();
    LossesAndAdjustmentsType subtractive = new LossesAndAdjustmentsType();
    additive.setAdditive(true);
    subtractive.setAdditive(false);
    LossesAndAdjustments add10 = new LossesAndAdjustments(1, additive, 10);
    LossesAndAdjustments sub10 = new LossesAndAdjustments(1, subtractive, 10);
    LossesAndAdjustments add1 = new LossesAndAdjustments(1, additive, 1);
    List<LossesAndAdjustments> lossesAndAdjustmentsList = new ArrayList<>();
    lossesAndAdjustmentsList.add(add10);
    lossesAndAdjustmentsList.add(add1);
    lossesAndAdjustmentsList.add(sub10);
    lineItem.setLossesAndAdjustments(lossesAndAdjustmentsList);
    lineItem.setTotalLossesAndAdjustments(1);

    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldThrowExceptionIfCalculationForNormalizedConsumptionNotValid() throws Exception {
    lineItem.setStockOutDays(2);
    lineItem.setNewPatientCount(5);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(5F);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldNotThrowExceptionIfCalculationForNormalizedConsumptionValid() throws Exception {
    lineItem.setStockOutDays(3);
    lineItem.setNewPatientCount(3);
    lineItem.setDosesPerMonth(30);
    lineItem.setDosesPerDispensingUnit(10);
    lineItem.setNormalizedConsumption(37F);
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldThrowExceptionIfAMCNotEqualToNormalizedConsumption() throws Exception {
    lineItem.setNormalizedConsumption(22f);
    lineItem.setAmc(10f);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);
    lineItem.validate(formulaValidated);

  }

  @Test
  public void shouldThrowExceptionIfCalculationForMaxStockQuantityNotValid() throws Exception {
    lineItem.setAmc(37F);
    lineItem.setMaxMonthsOfStock(2);
    lineItem.setMaxStockQuantity(56);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);

    lineItem.validate(formulaValidated);
  }

  @Test
  public void shouldThrowExceptionIfCalculatedOrderQuantityNotValid() throws Exception {
    lineItem.setMaxStockQuantity(74);
    lineItem.setStockInHand(4);
    lineItem.setCalculatedOrderQuantity(65);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(Rnr.RNR_VALIDATION_ERROR);

    lineItem.validate(formulaValidated);
  }


  @Test
  public void shouldNotThrowErrorIfAllMandatoryFieldsPresent() throws Exception {
    assertTrue(lineItem.validate(formulaValidated));
  }

  @Test
  public void shouldGetCalculatedOrderQuantityIfApprovedQuantityIsNullForFullSupplyItems() throws Exception {
    lineItem.setQuantityApproved(null);
    final int expected = 1;
    lineItem.setCalculatedOrderQuantity(expected);
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetRequestedQuantityIfApprovedQuantityIsNullForNonFullSupplyItems() throws Exception {
    lineItem.setQuantityApproved(null);
    lineItem.setFullSupply(false);
    final int expected = 1;
    lineItem.setQuantityRequested(expected);
    final Integer actual = lineItem.getQuantityApproved();
    assertThat(actual, is(expected));
  }
}
