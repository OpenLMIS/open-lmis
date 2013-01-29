package org.openlmis.rnr.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
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

}
