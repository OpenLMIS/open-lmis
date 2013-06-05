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
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.modifiedBy;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.RELEASED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

@Category(UnitTests.class)
public class RnrTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  private Rnr rnr;
  List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  @Before
  public void setUp() throws Exception {
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
    rnr.calculateAndValidate(programRnrColumns, lossesAndAdjustmentsTypes);

    verify(rnrLineItem1).validateMandatoryFields(programRnrColumns);
    verify(rnrLineItem1).validateCalculatedFields(programRnrColumns);

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
    ArrayList<RnrLineItem> lineItems = new ArrayList<>();
    ArrayList<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
    RnrLineItem firstLineItem = mock(RnrLineItem.class);
    RnrLineItem secondLineItem = mock(RnrLineItem.class);
    lineItems.add(firstLineItem);
    nonFullSupplyLineItems.add(secondLineItem);


    rnr.setFullSupplyLineItems(lineItems);
    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);
    Money fullSupplyItemSubmittedCost = new Money("10");
    Money nonFullSupplyItemSubmittedCost = new Money("20");
    when(firstLineItem.calculateCost()).thenReturn(fullSupplyItemSubmittedCost);
    when(secondLineItem.calculateCost()).thenReturn(nonFullSupplyItemSubmittedCost);
    rnr.calculateAndValidate(programRequisitionColumns, lossesAndAdjustmentsTypes);

    verify(firstLineItem).calculate(period, programRequisitionColumns, SUBMITTED, lossesAndAdjustmentsTypes);
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(fullSupplyItemSubmittedCost));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(nonFullSupplyItemSubmittedCost));
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

    rnr.calculateForApproval();

    assertThat(rnr.getFullSupplyLineItems().get(0).getPacksToShip(), is(7));
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("28")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("0")));
  }

//  @Test
//  public void shouldCopyUserEditableFieldsIdAccordingToStatus() throws Exception {
//    Rnr rnr = spy(new Rnr());
//    RnrLineItem lineItem = spy(make(a(RnrLineItemBuilder.defaultRnrLineItem,
//      with(roundToZero, true),
//      with(packRoundingThreshold, 6),
//      with(quantityApproved, 66),
//      with(packSize, 10),
//      with(roundToZero, false))));
//    rnr.setFullSupplyLineItems(asList(lineItem));
//    rnr.setStatus(INITIATED);
//    Rnr otherRnr = new Rnr();
//    otherRnr.setFullSupplyLineItems(asList(lineItem));
//    List<RnrColumn> programRnrColumns = setupProgramTemplate();
//    rnr.copyEditableFields(otherRnr, programRnrColumns);
//    verify(lineItem).copyUserEditableFields(lineItem, programRnrColumns);
//
//    rnr.setStatus(IN_APPROVAL);
//    rnr.copyEditableFields(otherRnr, programRnrColumns);
//    verify(lineItem).copyApproverEditableFields(lineItem, template);
//  }

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

    rnr.copyCreatorEditableFields(newRnr, template);

    verify(spyLineItem1).copyCreatorEditableFieldsForFullSupply(lineItem1, template);
    verify(spyLineItem2).copyCreatorEditableFieldsForFullSupply(lineItem2, template);
    verify(spyLineItem3).copyCreatorEditableFieldsForNonFullSupply(lineItem3, template);
    verify(spyLineItem4).copyCreatorEditableFieldsForNonFullSupply(lineItem4, template);
    assertThat(rnr.getModifiedBy(), is(newRnr.getModifiedBy()));
    assertModifiedBy(userId);
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
  public void shouldNotCopyFieldsForExtraFullSupplyLineItems() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);
    RnrLineItem spyLineItem2 = spy(lineItem2);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    rnr.copyCreatorEditableFields(newRnr, template);

    verify(spyLineItem2, never()).copyCreatorEditableFieldsForFullSupply(lineItem2, template);
  }

  @Test
  public void shouldNotCopyExtraLineItemForApprovalRnr() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);
    RnrLineItem spyLineItem2 = spy(lineItem2);

    newRnr.setFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(asList(spyLineItem1));

    rnr.copyApproverEditableFields(newRnr, template);

    verify(spyLineItem2, never()).copyCreatorEditableFieldsForFullSupply(lineItem2, template);
  }

  @Test
  public void shouldAddNewNonFullSupplyLineItems() throws Exception {
    long userId = 5L;
    Rnr newRnr = make(a(defaultRnr, with(modifiedBy, userId)));
    ProgramRnrTemplate template = new ProgramRnrTemplate(new ArrayList<RnrColumn>());

    RnrLineItem lineItem1 = make(a(defaultRnrLineItem, with(beginningBalance, 24), with(productCode, "P1")));
    RnrLineItem lineItem2 = make(a(defaultRnrLineItem, with(beginningBalance, 25), with(productCode, "P2")));

    RnrLineItem spyLineItem1 = spy(lineItem1);

    newRnr.setNonFullSupplyLineItems(asList(lineItem1, lineItem2));

    rnr.setFullSupplyLineItems(new ArrayList<RnrLineItem>());
    rnr.setNonFullSupplyLineItems(new ArrayList<RnrLineItem>());
    rnr.getNonFullSupplyLineItems().add(spyLineItem1);

    rnr.copyCreatorEditableFields(newRnr, template);

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

}
