/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RegimenLineItemBuilder;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.defaultFacilityApprovedProduct;
import static org.openlmis.core.builder.ProgramProductBuilder.defaultProgramProduct;
import static org.openlmis.core.builder.ProgramProductBuilder.productCode;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@Category(UnitTests.class)
public class RnrTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  ProgramRnrTemplate rnrTemplate;
  RegimenTemplate regimenTemplate;
  List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    rnr = make(a(defaultRequisition));
    rnrTemplate = mock(ProgramRnrTemplate.class);
    regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    lossesAndAdjustmentsTypes = mock(ArrayList.class);
  }

  @Test
  public void shouldPopulateRnrLineItemsAndRegimenLineItems() throws Exception {

    List<Regimen> regimens = new ArrayList<>();
    Regimen regimen1 = new Regimen();
    regimen1.setActive(true);
    Regimen regimen2 = new Regimen();
    regimen2.setActive(false);

    regimens.add(regimen1);
    regimens.add(regimen2);

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = make(a(defaultFacilityApprovedProduct));
    Product product = make(a(ProductBuilder.defaultProduct));
    facilityTypeApprovedProduct.getProgramProduct().setProduct(product);
    ProductCategory productCategory = new ProductCategory();
    facilityTypeApprovedProduct.getProgramProduct().setProductCategory(productCategory);
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    facilityTypeApprovedProducts.add(facilityTypeApprovedProduct);

    Rnr requisition = new Rnr(new Facility(1L), new Program(2L), new ProcessingPeriod(3L), false, facilityTypeApprovedProducts, regimens, 4L);

    assertThat(requisition.getRegimenLineItems().size(), is(1));
    assertThat(requisition.getFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldFindLineItemInPreviousRequisitionAndSetBeginningBalance() throws Exception {

    Rnr rnr = make(a(defaultRequisition));
    Rnr previousRequisition = new Rnr();
    previousRequisition.setStatus(AUTHORIZED);

    RnrLineItem correspondingLineItemInPreviousRequisition = make(a(defaultRnrLineItem, with(stockInHand, 76)));
    previousRequisition.setFullSupplyLineItems(asList(correspondingLineItemInPreviousRequisition));

    rnr.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(76));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfLineItemDoesNotExistInPreviousRequisition() throws Exception {
    Rnr rnr = make(a(defaultRequisition));

    Rnr previousRequisition = new Rnr();

    previousRequisition.setStatus(AUTHORIZED);

    rnr.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionDoesNotExist() throws Exception {
    Rnr previousRequisition = null;

    rnr.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionIsInInitiatedState() throws Exception {
    Rnr previousRequisition = make(a(defaultRequisition, with(status, INITIATED)));

    rnr.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionIsInSubmittedState() throws Exception {
    Rnr previousRequisition = make(a(defaultRequisition, with(status, SUBMITTED)));

    rnr.setFieldsAccordingToTemplateFrom(previousRequisition, rnrTemplate, regimenTemplate);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldCalculatePacksToShip() throws Exception {
    RnrLineItem fullSupply = spy(make(a(defaultRnrLineItem,
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10))));

    RnrLineItem nonFullSupply = spy(make(a(defaultRnrLineItem,
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10))));

    rnr.setFullSupplyLineItems(asList(fullSupply));
    rnr.setNonFullSupplyLineItems(asList(nonFullSupply));

    rnr.calculateForApproval();

    verify(fullSupply).calculatePacksToShip();
    verify(nonFullSupply).calculatePacksToShip();

    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("28")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("28")));
  }

  @Test
  public void shouldCalculatePacksToShipInCaseOfEmergencyRequisition() throws Exception {
    RnrLineItem fullSupply = spy(make(a(defaultRnrLineItem,
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10))));

    RnrLineItem nonFullSupply = spy(make(a(defaultRnrLineItem,
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10))));

    rnr.setFullSupplyLineItems(asList(fullSupply));
    rnr.setNonFullSupplyLineItems(asList(nonFullSupply));
    rnr.setEmergency(true);

    rnr.calculateForApproval();

    verify(fullSupply).calculatePacksToShip();
    verify(nonFullSupply).calculatePacksToShip();

    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("28")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("28")));
  }

  @Test
  public void shouldReleaseARequisitionAsAnOrder() throws Exception {
    Long userId = 1L;
    rnr.convertToOrder(userId);
    assertThat(rnr.getStatus(), is(RELEASED));
    assertThat(rnr.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldValidateRnrForApproval() throws Exception {
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem3 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem4 = mock(RnrLineItem.class);

    rnr.setFullSupplyLineItems(asList(rnrLineItem1, rnrLineItem2));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem3, rnrLineItem4));

    rnr.validateForApproval();

    verify(rnrLineItem1).validateForApproval();
    verify(rnrLineItem2).validateForApproval();
    verify(rnrLineItem3).validateForApproval();
    verify(rnrLineItem4).validateForApproval();
  }

  @Test
  public void shouldCopyCreatorEditableFields() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRequisition, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(RnrLineItemBuilder.productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(RnrLineItemBuilder.productCode, "P2")));
    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(beginningBalance, 27), with(RnrLineItemBuilder.productCode, "P3")));
    RnrLineItem lineItem4 = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P4")));

    RnrLineItem spyLineItem1 = spy(lineItem1);
    RnrLineItem spyLineItem2 = spy(lineItem2);
    RnrLineItem spyLineItem3 = spy(lineItem3);
    RnrLineItem spyLineItem4 = spy(lineItem4);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));
    newRnr.setNonFullSupplyLineItems(asList(lineItem3, lineItem4));

    rnr.setFullSupplyLineItems(asList(spyLineItem1, spyLineItem2));
    rnr.setNonFullSupplyLineItems(asList(spyLineItem3, spyLineItem4));

    List<ProgramProduct> programProducts = new ArrayList<>();
    ProgramProduct programProduct1 = make(a(defaultProgramProduct, with(productCode, "P3")));
    ProgramProduct programProduct2 = make(a(defaultProgramProduct, with(productCode, "P4")));
    programProducts.add(programProduct1);
    programProducts.add(programProduct2);

    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate, programProducts);

    verify(spyLineItem1).copyCreatorEditableFieldsForFullSupply(lineItem1, template);
    verify(spyLineItem2).copyCreatorEditableFieldsForFullSupply(lineItem2, template);
    verify(spyLineItem3).copyCreatorEditableFieldsForNonFullSupply(lineItem3, template);
    verify(spyLineItem4).copyCreatorEditableFieldsForNonFullSupply(lineItem4, template);
    assertThat(rnr.getModifiedBy(), is(newRnr.getModifiedBy()));
    assertModifiedBy(userId);
  }

  @Test
  public void shouldAddNewNonFullSupplyLineItemsOnlyIfProductBelongsToTheProgram() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRequisition, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(beginningBalance, 27), with(RnrLineItemBuilder.productCode, "P3")));
    RnrLineItem lineItem4 = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P4")));

    RnrLineItem spyLineItem3 = spy(lineItem3);

    newRnr.setNonFullSupplyLineItems(asList(lineItem3, lineItem4));

    rnr.setNonFullSupplyLineItems(asList(spyLineItem3));

    List<ProgramProduct> programProducts = new ArrayList<>();
    ProgramProduct programProduct1 = make(a(defaultProgramProduct, with(productCode, "P3")));
    programProducts.add(programProduct1);

    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate, programProducts);

    assertThat(rnr.getNonFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldCopyRegimenLineItems() throws Exception {
    Rnr newRnr = make(a(defaultRequisition));
    List<RegimenColumn> regimenColumns = new ArrayList<>();
    RegimenLineItem regimenLineItem = make(a(RegimenLineItemBuilder.defaultRegimenLineItem));
    regimenLineItem.setCode("R02");
    RegimenLineItem regimenLineItem1 = make(a(RegimenLineItemBuilder.defaultRegimenLineItem));
    RegimenLineItem spyRegimenLineItem = spy(regimenLineItem);
    RegimenLineItem spyRegimenLineItem1 = spy(regimenLineItem1);
    newRnr.setModifiedBy(1L);
    newRnr.setRegimenLineItems(asList(regimenLineItem, regimenLineItem1));
    rnr.setRegimenLineItems(asList(spyRegimenLineItem, spyRegimenLineItem1));
    RegimenTemplate regimenTemplate = new RegimenTemplate(1l, regimenColumns);
    List<RnrColumn> rnrColumns = new ArrayList<>();
    List<ProgramProduct> programProducts = new ArrayList<>();
    rnr.copyCreatorEditableFields(newRnr, new ProgramRnrTemplate(rnrColumns), regimenTemplate, programProducts);

    verify(spyRegimenLineItem).copyCreatorEditableFieldsForRegimen(regimenLineItem, regimenTemplate);
    verify(spyRegimenLineItem1).copyCreatorEditableFieldsForRegimen(regimenLineItem1, regimenTemplate);
    assertThat(spyRegimenLineItem.getModifiedBy(), is(1L));
    assertThat(spyRegimenLineItem1.getModifiedBy(), is(1L));
  }

  private void assertModifiedBy(long userId) {
    List<RnrLineItem> finalLineItems = new ArrayList<RnrLineItem>() {{
      addAll(rnr.getFullSupplyLineItems());
      addAll(rnr.getNonFullSupplyLineItems());
    }};
    for (RnrLineItem lineItem : finalLineItems) {
      assertThat(lineItem.getModifiedBy(), is(userId));
    }
  }

  @Test
  public void shouldNotCopyFieldsForExtraFullSupplyLineItemsAndThrowError() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRequisition, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(RnrLineItemBuilder.productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(RnrLineItemBuilder.productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));
    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    exception.expect(DataException.class);
    exception.expectMessage("product.code.invalid");
    List<ProgramProduct> programProducts = new ArrayList<>();
    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate, programProducts);
  }

  @Test
  public void shouldNotCopyExtraLineItemForApprovalRnr() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRequisition, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());


    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(RnrLineItemBuilder.productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(RnrLineItemBuilder.productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    exception.expect(DataException.class);
    exception.expectMessage("product.code.invalid");

    rnr.copyApproverEditableFields(newRnr, template);
  }

  @Test
  public void shouldCopyApproverEditableFields() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRequisition, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(RnrLineItemBuilder.productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(RnrLineItemBuilder.productCode, "P2")));
    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(beginningBalance, 27), with(RnrLineItemBuilder.productCode, "P3")));
    RnrLineItem lineItem4 = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P4")));

    RnrLineItem spyLineItem1 = spy(lineItem1);
    RnrLineItem spyLineItem2 = spy(lineItem2);
    RnrLineItem spyLineItem3 = spy(lineItem3);
    RnrLineItem spyLineItem4 = spy(lineItem4);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));
    newRnr.setNonFullSupplyLineItems(asList(lineItem3, lineItem4));

    rnr.setFullSupplyLineItems(asList(spyLineItem1, spyLineItem2));
    rnr.setNonFullSupplyLineItems(asList(spyLineItem3, spyLineItem4));

    rnr.copyApproverEditableFields(newRnr, template);

    verify(spyLineItem1).copyApproverEditableFields(lineItem1, template);
    verify(spyLineItem2).copyApproverEditableFields(lineItem2, template);
    verify(spyLineItem3).copyApproverEditableFields(lineItem3, template);
    verify(spyLineItem4).copyApproverEditableFields(lineItem4, template);
    assertThat(rnr.getModifiedBy(), is(newRnr.getModifiedBy()));
    assertModifiedBy(userId);
  }

  @Test
  public void shouldSetModifiedByAndStatus() throws Exception {
    rnr.setAuditFieldsForRequisition(1l, SUBMITTED);

    assertThat(rnr.getModifiedBy(), is(1l));
    assertThat(rnr.getStatus(), is(SUBMITTED));
  }

  @Test
  public void shouldCalculateDefaultApprovedQuantityForRegularRequisitionUsingRegularCalcStrategy() {
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);

    rnr.setFullSupplyLineItems(asList(rnrLineItem1));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem2));

    rnr.setFieldsForApproval();

    verify(rnrLineItem1).setFieldsForApproval();
    verify(rnrLineItem2).setFieldsForApproval();
  }

  @Test
  public void shouldGetProductCodeDifferenceGivenARnr() throws Exception {
    Rnr savedRnr = make(a(RequisitionBuilder.defaultRequisition));
    savedRnr.setFullSupplyLineItems(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P11")))));

    Rnr rnrForApproval = make(a(RequisitionBuilder.defaultRequisition));
    rnrForApproval.setFullSupplyLineItems(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P10")))));

    List<String> invalidProductCodes = new ArrayList<>();
    for (final RnrLineItem lineItem : rnrForApproval.getFullSupplyLineItems()) {
      if (savedRnr.findCorrespondingLineItem(lineItem) == null) {
        invalidProductCodes.add(lineItem.getProductCode());
      }
    }
    assertThat(invalidProductCodes, is(asList("P10")));
  }

  @Test
  public void shouldFindCorrespondingLineItemInRnrAndReturnIfFound() {
    Rnr savedRnr = make(a(RequisitionBuilder.defaultRequisition));
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P11")));
    savedRnr.setFullSupplyLineItems(asList(rnrLineItem));

    RnrLineItem foundLineItem = savedRnr.findCorrespondingLineItem(rnrLineItem);

    assertThat(foundLineItem, is(rnrLineItem));
  }

  @Test
  public void shouldFindCorrespondingLineItemInRnrAndReturnNullIfNotFound() {
    Rnr savedRnr = make(a(RequisitionBuilder.defaultRequisition));
    RnrLineItem rnrLineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P11")));

    assertThat(savedRnr.findCorrespondingLineItem(rnrLineItem), is(nullValue()));
  }

  @Test
  public void shouldReturnNonSkippedLineItemsForRnrWithoutAffectingFullSupplyLineItems() throws Exception {
    Rnr savedRnr = make(a(defaultRequisition));
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P11"), with(skipped, true)));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P12"), with(skipped, false)));
    savedRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));

    assertThat(savedRnr.getNonSkippedLineItems(), is(asList(lineItem2)));
    assertThat(savedRnr.getFullSupplyLineItems(), is(asList(lineItem1, lineItem2)));
  }

  @Test
  public void shouldValidateRegimenLineItems() throws NoSuchFieldException, IllegalAccessException {
    Rnr rnr = new Rnr();
    RegimenLineItem regimenLineItem = mock(RegimenLineItem.class);
    List<RegimenLineItem> regimenLineItems = asList(regimenLineItem);
    rnr.setRegimenLineItems(regimenLineItems);
    rnr.validateRegimenLineItems(regimenTemplate);
    verify(regimenLineItem).validate(regimenTemplate);
  }

  @Test
  public void shouldReturnBudgetingAppliesAsFalseIfEmergencyRnr() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setEmergency(true);

    assertFalse(rnr.isBudgetingApplicable());
  }

  @Test
  public void shouldReturnBudgetingAppliesAsFalseIfNotApplicableToProgram() throws Exception {
    Program program = mock(Program.class);
    when(program.getBudgetingApplies()).thenReturn(false);
    Rnr rnr = new Rnr();
    rnr.setEmergency(false);
    rnr.setProgram(program);

    assertFalse(rnr.isBudgetingApplicable());
  }

  @Test
  public void shouldReturnBudgetingAppliesAsTrueIBudgetingApplicableToProgram() throws Exception {
    Program program = mock(Program.class);
    when(program.getBudgetingApplies()).thenReturn(true);
    Rnr rnr = new Rnr();
    rnr.setEmergency(false);
    rnr.setProgram(program);

    assertTrue(rnr.isBudgetingApplicable());
  }

  @Test
  public void shouldReturnBudgetingAppliesAsFalseIBudgetingApplicableToProgramAndRnrIsEmergency() throws Exception {
    Program program = mock(Program.class);
    when(program.getBudgetingApplies()).thenReturn(true);
    Rnr rnr = new Rnr();
    rnr.setEmergency(true);
    rnr.setProgram(program);

    assertFalse(rnr.isBudgetingApplicable());
  }

  @Test
  public void shouldGetAllLineItems() throws Exception {
    Rnr rnr = make(a(defaultRequisition));
    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(RnrLineItemBuilder.productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(RnrLineItemBuilder.productCode, "P2")));
    rnr.setFullSupplyLineItems(asList(lineItem1));
    rnr.setNonFullSupplyLineItems(asList(lineItem2));

    assertThat(rnr.getAllLineItems().size(), is(2));
    assertThat(rnr.getAllLineItems().get(0), is(lineItem1));
    assertThat(rnr.getAllLineItems().get(1), is(lineItem2));
  }
}
