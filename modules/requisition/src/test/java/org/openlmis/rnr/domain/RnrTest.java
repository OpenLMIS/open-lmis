/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.defaultFacilityApprovedProduct;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.modifiedBy;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.RELEASED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
public class RnrTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  private Rnr rnr;
  List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    rnr = make(a(defaultRnr));
    lossesAndAdjustmentsTypes = mock(ArrayList.class);
  }

  @Test
  public void shouldCallValidateOnEachLineItem() throws Exception {
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);


    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    when(rnrLineItem2.calculateCost()).thenReturn(new Money("10"));
    rnr.setFullSupplyLineItems(asList(rnrLineItem1));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem2));

    List<RnrColumn> programRnrColumns = new ArrayList<>();
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRnrColumns);
    rnr.calculate(template, lossesAndAdjustmentsTypes);

    verify(rnrLineItem1).validateMandatoryFields(template);
    verify(rnrLineItem1).validateCalculatedFields(template);

    verify(rnrLineItem2).validateNonFullSupply();
  }

  @Test
  public void shouldFillNormalizedConsumptionsFromPreviousTwoPeriodsRnr() throws Exception {

    final Rnr lastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    lastPeriodsRnr.getFullSupplyLineItems().get(0).setNormalizedConsumption(1);

    final Rnr secondLastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    secondLastPeriodsRnr.getFullSupplyLineItems().get(0).setNormalizedConsumption(2);

    rnr.fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);

    List<Integer> previousNormalizedConsumptions = rnr.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions();
    assertThat(previousNormalizedConsumptions.size(), is(2));
    assertThat(previousNormalizedConsumptions.get(0), is(1));
    assertThat(previousNormalizedConsumptions.get(1), is(2));
  }

  @Test
  public void shouldFillNormalizedConsumptionsFromOnlyNonNullPreviousTwoPeriodsRnr() throws Exception {

    final Rnr lastPeriodsRnr = null;

    final Rnr secondLastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    secondLastPeriodsRnr.getFullSupplyLineItems().get(0).setNormalizedConsumption(2);

    rnr.fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);

    List<Integer> previousNormalizedConsumptions = rnr.getFullSupplyLineItems().get(0).getPreviousNormalizedConsumptions();
    assertThat(previousNormalizedConsumptions.size(), is(1));
    assertThat(previousNormalizedConsumptions.get(0), is(2));
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
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = new ArrayList<>();
    facilityTypeApprovedProducts.add(facilityTypeApprovedProduct);

    Rnr requisition = new Rnr(1L, 2L, 3L, facilityTypeApprovedProducts, regimens, 4L);

    assertThat(requisition.getRegimenLineItems().size(), is(1));
    assertThat(requisition.getFullSupplyLineItems().size(), is(1));
  }

  @Test
  public void shouldFindLineItemInPreviousRequisitionAndSetBeginningBalance() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    Rnr previousRequisition = new Rnr();
    RnrLineItem correspondingLineItemInPreviousRequisition = make(a(defaultRnrLineItem, with(stockInHand, 76)));
    previousRequisition.setFullSupplyLineItems(asList(correspondingLineItemInPreviousRequisition));

    rnr.setBeginningBalances(previousRequisition, true);

    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(correspondingLineItemInPreviousRequisition.getStockInHand()));
    assertThat(rnr.getFullSupplyLineItems().get(0).getPreviousStockInHandAvailable(), is(Boolean.TRUE));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfLineItemDoesNotExistInPreviousRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    rnr.setBeginningBalances(new Rnr(), true);
    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldCalculateCalculatedFieldsAccordingToProgramTemplate() throws Exception {
    ArrayList<RnrColumn> programRequisitionColumns = new ArrayList<>();
    ProcessingPeriod period = new ProcessingPeriod();
    RnrLineItem firstLineItem = mock(RnrLineItem.class);
    RnrLineItem secondLineItem = mock(RnrLineItem.class);

    rnr.setFullSupplyLineItems(asList(firstLineItem));
    rnr.setNonFullSupplyLineItems(asList(secondLineItem));
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);

    when(firstLineItem.calculateCost()).thenReturn(new Money("10"));
    when(secondLineItem.calculateCost()).thenReturn(new Money("20"));
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRequisitionColumns);

    rnr.calculate(template, lossesAndAdjustmentsTypes);

    verify(firstLineItem).calculateForFullSupply(period, template, SUBMITTED, lossesAndAdjustmentsTypes);
    verify(firstLineItem).calculateCost();
    verify(secondLineItem).calculateCost();
    verify(secondLineItem).calculatePacksToShip();
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("10")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("20")));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionDoesNotExist() throws Exception {
    rnr.setBeginningBalances(null, false);
    assertThat(rnr.getFullSupplyLineItems().get(0).getBeginningBalance(), is(0));
  }


  @Test
  public void testCalculatePacksToShip() throws Exception {
    RnrLineItem lineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem,
      with(roundToZero, true),
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10),
      with(roundToZero, false)));
    rnr.setFullSupplyLineItems(asList(lineItem));
    rnr.setNonFullSupplyLineItems(asList(lineItem));

    rnr.calculateForApproval();

    assertThat(rnr.getFullSupplyLineItems().get(0).getPacksToShip(), is(7));
    assertThat(rnr.getNonFullSupplyLineItems().get(0).getPacksToShip(), is(7));
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
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));
    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(beginningBalance, 27), with(productCode, "P3")));
    RnrLineItem lineItem4 = make(a(defaultRnrLineItem, with(productCode, "P4")));

    RnrLineItem spyLineItem1 = spy(lineItem1);
    RnrLineItem spyLineItem2 = spy(lineItem2);
    RnrLineItem spyLineItem3 = spy(lineItem3);
    RnrLineItem spyLineItem4 = spy(lineItem4);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));
    newRnr.setNonFullSupplyLineItems(asList(lineItem3, lineItem4));

    rnr.setFullSupplyLineItems(asList(spyLineItem1, spyLineItem2));
    rnr.setNonFullSupplyLineItems(asList(spyLineItem3, spyLineItem4));

    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate);

    verify(spyLineItem1).copyCreatorEditableFieldsForFullSupply(lineItem1, template);
    verify(spyLineItem2).copyCreatorEditableFieldsForFullSupply(lineItem2, template);
    verify(spyLineItem3).copyCreatorEditableFieldsForNonFullSupply(lineItem3, template);
    verify(spyLineItem4).copyCreatorEditableFieldsForNonFullSupply(lineItem4, template);
    assertThat(rnr.getModifiedBy(), is(newRnr.getModifiedBy()));
    assertModifiedBy(userId);
  }

  @Test
  public void shouldCopyRegimenLineItems() throws Exception {

    Rnr newRnr = make(a(defaultRnr));
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

    rnr.copyCreatorEditableFields(newRnr, new ProgramRnrTemplate(rnrColumns), regimenTemplate);

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
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));
    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    exception.expect(DataException.class);
    exception.expectMessage("product.code.invalid");

    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate);
  }

  @Test
  public void shouldNotCopyExtraLineItemForApprovalRnr() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    exception.expect(DataException.class);
    exception.expectMessage("product.code.invalid");

    rnr.copyApproverEditableFields(newRnr, template);
  }

  @Test
  public void shouldAddNewNonFullSupplyLineItems() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());
    RegimenTemplate regimenTemplate = new RegimenTemplate(rnr.getProgram().getId(), new ArrayList<RegimenColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setFullSupplyLineItems(new ArrayList<RnrLineItem>());
    newRnr.setNonFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(new ArrayList<RnrLineItem>());
    rnr.setNonFullSupplyLineItems(new ArrayList<RnrLineItem>());
    rnr.getNonFullSupplyLineItems().add(spyLineItem1);

    rnr.copyCreatorEditableFields(newRnr, template, regimenTemplate);

    assertThat(rnr.getNonFullSupplyLineItems(), hasItem(lineItem2));
  }

  @Test
  public void shouldCopyApproverEditableFields() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));
    RnrLineItem lineItem3 = make(a(defaultRnrLineItem, with(beginningBalance, 27), with(productCode, "P3")));
    RnrLineItem lineItem4 = make(a(defaultRnrLineItem, with(productCode, "P4")));

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
}
