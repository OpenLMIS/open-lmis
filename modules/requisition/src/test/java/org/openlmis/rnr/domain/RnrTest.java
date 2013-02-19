package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrColumnBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.status;
import static org.openlmis.rnr.builder.RnrColumnBuilder.columnName;
import static org.openlmis.rnr.builder.RnrColumnBuilder.visible;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.*;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

public class RnrTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    rnr = make(a(defaultRnr));
  }

  @Test
  public void shouldCallValidateOnEachLineItem() throws Exception {
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);


    when(rnrLineItem1.calculateCost()).thenReturn(new Money("10"));
    when(rnrLineItem2.calculateCost()).thenReturn(new Money("10"));
    rnr.setLineItems(asList(rnrLineItem1));
    rnr.setNonFullSupplyLineItems(asList(rnrLineItem2));

    List<RnrColumn> programRnrColumns = new ArrayList<>();
    rnr.calculate(programRnrColumns);

    verify(rnrLineItem1).validateMandatoryFields(programRnrColumns);
    verify(rnrLineItem1).validateCalculatedFields(programRnrColumns);

    verify(rnrLineItem2).validateNonFullSupply();
  }

  @Test
  public void shouldFillNormalizedConsumptionsFromPreviousTwoPeriodsRnr() throws Exception {

    final Rnr lastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    lastPeriodsRnr.getLineItems().get(0).setNormalizedConsumption(1);

    final Rnr secondLastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    secondLastPeriodsRnr.getLineItems().get(0).setNormalizedConsumption(2);

    rnr.fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);

    List<Integer> previousNormalizedConsumptions = rnr.getLineItems().get(0).getPreviousNormalizedConsumptions();
    assertThat(previousNormalizedConsumptions.size(), is(2));
    assertThat(previousNormalizedConsumptions.get(0), is(1));
    assertThat(previousNormalizedConsumptions.get(1), is(2));
  }

  @Test
  public void shouldFillNormalizedConsumptionsFromOnlyNonNullPreviousTwoPeriodsRnr() throws Exception {

    final Rnr lastPeriodsRnr = null;

    final Rnr secondLastPeriodsRnr = make(a(RequisitionBuilder.defaultRnr));
    secondLastPeriodsRnr.getLineItems().get(0).setNormalizedConsumption(2);

    rnr.fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);

    List<Integer> previousNormalizedConsumptions = rnr.getLineItems().get(0).getPreviousNormalizedConsumptions();
    assertThat(previousNormalizedConsumptions.size(), is(1));
    assertThat(previousNormalizedConsumptions.get(0), is(2));
  }

  @Test
  public void shouldCopyApproverEditableFields() throws Exception {
    rnr.setModifiedBy(1);
    Rnr savedRnr = make(a(defaultRnr));
    RnrLineItem savedLineItem = savedRnr.getLineItems().get(0);
    RnrLineItem savedLineItemSpy = spy(savedLineItem);
    savedRnr.getLineItems().set(0, savedLineItemSpy);
    savedRnr.copyApproverEditableFields(rnr);
    verify(savedLineItemSpy).copyApproverEditableFields(rnr.getLineItems().get(0));
    assertThat(savedRnr.getModifiedBy(), is(1));
  }

  @Test
  public void shouldCopyUserEditableFields() throws Exception {
    List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
    nonFullSupplyLineItems.add(new RnrLineItem());

    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    rnr.setModifiedBy(1);
    Rnr savedRnr = make(a(defaultRnr));
    savedRnr.setModifiedBy(1);
    RnrLineItem savedLineItem = savedRnr.getLineItems().get(0);
    RnrLineItem savedLineItemSpy = spy(savedLineItem);
    savedRnr.getLineItems().set(0, savedLineItemSpy);
    ArrayList<RnrColumn> programRnrColumns = setupProgramTemplate();

    savedRnr.copyUserEditableFields(rnr, programRnrColumns);

    verify(savedLineItemSpy).copyUserEditableFields(rnr.getLineItems().get(0), programRnrColumns);
    assertThat(savedRnr.getModifiedBy(), is(1));
    assertThat(savedRnr.getNonFullSupplyLineItems(), is(nonFullSupplyLineItems));
    assertThat(savedRnr.getNonFullSupplyLineItems().get(0).getModifiedBy(), is(rnr.getModifiedBy()));
  }

  @Test
  public void shouldFindLineItemInPreviousRequisitionAndSetBeginningBalance() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    Rnr previousRequisition = new Rnr();
    RnrLineItem correspondingLineItemInPreviousRequisition = make(a(defaultRnrLineItem, with(stockInHand, 76)));
    previousRequisition.setLineItems(asList(correspondingLineItemInPreviousRequisition));

    rnr.setBeginningBalances(previousRequisition, true);

    assertThat(rnr.getLineItems().get(0).getBeginningBalance(), is(correspondingLineItemInPreviousRequisition.getStockInHand()));
    assertThat(rnr.getLineItems().get(0).getPreviousStockInHandAvailable(), is(Boolean.TRUE));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfLineItemDoesNotExistInPreviousRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    rnr.setBeginningBalances(new Rnr(), true);
    assertThat(rnr.getLineItems().get(0).getBeginningBalance(), is(0));
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


    rnr.setLineItems(lineItems);
    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);
    Money fullSupplyItemSubmittedCost = new Money("10");
    Money nonFullSupplyItemSubmittedCost = new Money("20");
    when(firstLineItem.calculateCost()).thenReturn(fullSupplyItemSubmittedCost);
    when(secondLineItem.calculateCost()).thenReturn(nonFullSupplyItemSubmittedCost);
    rnr.calculate(programRequisitionColumns);

    verify(firstLineItem).calculate(period, programRequisitionColumns);
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(fullSupplyItemSubmittedCost));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(nonFullSupplyItemSubmittedCost));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionDoesNotExist() throws Exception {
    rnr.setBeginningBalances(null, false);
    assertThat(rnr.getLineItems().get(0).getBeginningBalance(), is(0));
  }

  @Test
  public void shouldNotCopyInvisibleTemplateFieldsFromRequisition() throws Exception {
    ArrayList<RnrColumn> programRnrColumns = setupProgramTemplate();

    RnrLineItem newLineItem = make(a(defaultRnrLineItem, with(stockInHand, 2), with(beginningBalance, 7)));

    Rnr requisitionForSaving = make(a(defaultRnr, with(status, SUBMITTED)));
    requisitionForSaving.setLineItems(asList(newLineItem));
    rnr.copyUserEditableFields(requisitionForSaving, programRnrColumns);

    assertThat(rnr.getLineItems().get(0).getStockInHand(), is(2));
    assertThat(rnr.getLineItems().get(0).getBeginningBalance(), is(BEGINNING_BALANCE));
  }

  @Test
  public void shouldPrepareRequisitionAndCalculate() throws Exception {
    rnr.setStatus(INITIATED);
    Rnr rnrSpy = spy(rnr);
    ArrayList<RnrColumn> programRnrColumns = new ArrayList<>();
    doNothing().when(rnrSpy).calculate(programRnrColumns);

    rnrSpy.prepareFor(SUBMITTED, programRnrColumns);

    verify(rnrSpy).calculate(programRnrColumns);
    assertThat(rnrSpy.getStatus(), is(SUBMITTED));
  }

  @Test
  public void testCalculatePacksToShip() throws Exception {
    RnrLineItem lineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem,
      with(roundToZero, true),
      with(packRoundingThreshold, 6),
      with(quantityApproved, 66),
      with(packSize, 10),
      with(roundToZero, false)));
    rnr.setLineItems(asList(lineItem));

    rnr.calculateForApproval();

    assertThat(rnr.getLineItems().get(0).getPacksToShip(), is(7));
    assertThat(rnr.getFullSupplyItemsSubmittedCost(), is(new Money("28")));
    assertThat(rnr.getNonFullSupplyItemsSubmittedCost(), is(new Money("0")));
  }

  private ArrayList<RnrColumn> setupProgramTemplate() {
    ArrayList<RnrColumn> programRnrColumns = new ArrayList<>();
    programRnrColumns.add(make(a(RnrColumnBuilder.defaultRnrColumn, with(columnName, "stockInHand"), with(visible, true))));
    programRnrColumns.add(make(a(RnrColumnBuilder.defaultRnrColumn, with(columnName, "beginningBalance"), with(visible, false))));
    return programRnrColumns;
  }
}
