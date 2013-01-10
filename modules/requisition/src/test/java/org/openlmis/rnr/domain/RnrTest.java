package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.builder.RnrBuilder;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.cost;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.domain.Rnr.RNR_VALIDATION_ERROR;

public class RnrTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    rnr = make(a(RnrBuilder.defaultRnr));
  }

  @Test
  public void shouldThrowExceptionIfCalculationForTotalCostNotValid() throws Exception {
    RnrLineItem rnrLineItemCost10 = make(a(defaultRnrLineItem, with(cost, 10f)));
    rnr.add(rnrLineItemCost10);
    rnr.setFullSupplyItemsSubmittedCost(60f);

    exception.expect(DataException.class);
    exception.expectMessage(RNR_VALIDATION_ERROR);

    rnr.validate(true);
  }

  @Test
  public void shouldThrowExceptionIfFullSupplyAndNonFullSupplyItemsCostNotEqualToTotalCost() throws Exception {
    rnr.setFullSupplyItemsSubmittedCost(48F);
    rnr.setNonFullSupplyItemsSubmittedCost(40f);
    rnr.setTotalSubmittedCost(90f);

    exception.expect(DataException.class);
    exception.expectMessage(RNR_VALIDATION_ERROR);

    rnr.validate(true);
  }

  @Test
  public void shouldValidateEachLineItem() throws Exception {
    RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    Rnr rnr = new Rnr();
    rnr.add(rnrLineItem1);
    rnr.add(rnrLineItem2);

    boolean formulaValidated = true;
    rnr.validate(formulaValidated);

    verify(rnrLineItem1).validate(formulaValidated);
    verify(rnrLineItem2).validate(formulaValidated);
  }

  @Test
  public void shouldNotThrowExceptionIfTotalSubmittedCostIsValid() throws Exception {
    rnr.setFullSupplyItemsSubmittedCost(48F);
    rnr.setNonFullSupplyItemsSubmittedCost(40f);
    rnr.setTotalSubmittedCost(88f);

    assertTrue(rnr.validate(true));
  }

}
