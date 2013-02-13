package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.builder.RnrColumnBuilder;

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
    List<RnrColumn> templateColumns = new ArrayList<>();
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    ArrayList<RnrLineItem> lineItems = new ArrayList<RnrLineItem>() {{
      add(rnrLineItem1);
      add(rnrLineItem2);
    }};
    rnr.setLineItems(lineItems);
    when(rnrLineItem1.validate(templateColumns)).thenReturn(true);
    when(rnrLineItem2.validate(templateColumns)).thenReturn(true);

    rnr.validate(templateColumns);

    verify(rnrLineItem1).validate(templateColumns);
    verify(rnrLineItem2).validate(templateColumns);
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

    rnr.setBeginningBalanceForEachLineItem(previousRequisition, true);

    assertThat(rnr.getLineItems().get(0).getBeginningBalance(), is(correspondingLineItemInPreviousRequisition.getStockInHand()));
    assertThat(rnr.getLineItems().get(0).getPreviousStockInHandAvailable(), is(Boolean.TRUE));
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfLineItemDoesNotExistInPreviousRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    rnr.setBeginningBalanceForEachLineItem(new Rnr(), true);
    assertThat(rnr.getLineItems().get(0).getBeginningBalance(),is(0));
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
    when(firstLineItem.getPrice()).thenReturn(new Money("1"));
    when(secondLineItem.getPrice()).thenReturn(new Money("1"));

    rnr.setLineItems(lineItems);
    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    rnr.setPeriod(period);
    rnr.setStatus(SUBMITTED);

    rnr.calculate();

    verify(firstLineItem).calculate(period, SUBMITTED);
    verify(secondLineItem).calculate(period, SUBMITTED);
  }

  @Test
  public void shouldSetBeginningBalanceToZeroIfPreviousRequisitionDoesNotExist() throws Exception {
    rnr.setBeginningBalanceForEachLineItem(null, false);
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

  private ArrayList<RnrColumn> setupProgramTemplate() {
    ArrayList<RnrColumn> programRnrColumns = new ArrayList<>();
    programRnrColumns.add(make(a(RnrColumnBuilder.defaultRnrColumn, with(columnName, "stockInHand"), with(visible, true))));
    programRnrColumns.add(make(a(RnrColumnBuilder.defaultRnrColumn, with(columnName, "beginningBalance"), with(visible, false))));
    return programRnrColumns;
  }
}
