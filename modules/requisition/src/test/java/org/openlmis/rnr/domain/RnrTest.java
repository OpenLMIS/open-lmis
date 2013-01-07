package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.builder.RnrBuilder;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.cost;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;

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
    exception.expectMessage("R&R has errors, please correct them before submission");

    rnr.validate();
  }

  @Test
  public void shouldThrowExceptionIfFullSupplyAndNonFullSupplyItemsCostNotEqualToTotalCost() throws Exception {
    rnr.setFullSupplyItemsSubmittedCost(48F);
    rnr.setNonFullSupplyItemsSubmittedCost(40f);
    rnr.setTotalSubmittedCost(90f);

    exception.expect(DataException.class);
    exception.expectMessage("R&R has errors, please correct them before submission");

    rnr.validate();
  }

  @Test
  public void shouldNotThrowExceptionIfTotalSubmittedCostIsValid() throws Exception {
    rnr.setFullSupplyItemsSubmittedCost(48F);
    rnr.setNonFullSupplyItemsSubmittedCost(40f);
    rnr.setTotalSubmittedCost(88f);

    assertTrue(rnr.validate());
  }
}
