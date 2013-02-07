package org.openlmis.rnr.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.rnr.builder.RequisitionBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;

public class RnrTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldCallValidateOnEachLineItem() throws Exception {
    List<RnrColumn> templateColumns = new ArrayList<>();
    final RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    final RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    ArrayList<RnrLineItem> lineItems = new ArrayList<RnrLineItem>() {{
      add(rnrLineItem1);
      add(rnrLineItem2);
    }};
    Rnr rnr = make(a(defaultRnr));
    rnr.setLineItems(lineItems);
    when(rnrLineItem1.validate(templateColumns)).thenReturn(true);
    when(rnrLineItem2.validate(templateColumns)).thenReturn(true);

    rnr.validate(templateColumns);

    verify(rnrLineItem1).validate(templateColumns);
    verify(rnrLineItem2).validate(templateColumns);
  }

  @Test
  public void shouldFillNormalizedConsumptionsFromPreviousTwoPeriodsRnr() throws Exception {
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr));

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
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr));

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
    Rnr rnr = make(a(defaultRnr));
    Rnr savedRnr = make(a(defaultRnr));
    RnrLineItem savedLineItem = savedRnr.getLineItems().get(0);
    RnrLineItem savedLineItemSpy = spy(savedLineItem);
    savedRnr.getLineItems().set(0, savedLineItemSpy);
    savedRnr.copyApproverEditableFields(rnr);
    verify(savedLineItemSpy).copyApproverEditableFields(rnr.getLineItems().get(0));
  }

  @Test
  public void shouldCopyUserEditableFields() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    Rnr savedRnr = make(a(defaultRnr));
    RnrLineItem savedLineItem = savedRnr.getLineItems().get(0);
    RnrLineItem savedLineItemSpy = spy(savedLineItem);
    savedRnr.getLineItems().set(0, savedLineItemSpy);
    savedRnr.copyUserEditableFieldsForSaveSubmitOrAuthorize(rnr);
    verify(savedLineItemSpy).copyUserEditableFieldsForSaveSubmitOrAuthorize(rnr.getLineItems().get(0));
  }
}
