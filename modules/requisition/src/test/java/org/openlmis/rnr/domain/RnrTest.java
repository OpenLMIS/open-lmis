package org.openlmis.rnr.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.rnr.builder.RequisitionBuilder;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RnrTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldValidateEachLineItem() throws Exception {
    RnrLineItem rnrLineItem1 = mock(RnrLineItem.class);
    RnrLineItem rnrLineItem2 = mock(RnrLineItem.class);
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr));
    rnr.add(rnrLineItem1);
    rnr.add(rnrLineItem2);

    boolean formulaValidated = true;
    rnr.validate(formulaValidated);

    verify(rnrLineItem1).validate(formulaValidated);
    verify(rnrLineItem2).validate(formulaValidated);
  }

}
