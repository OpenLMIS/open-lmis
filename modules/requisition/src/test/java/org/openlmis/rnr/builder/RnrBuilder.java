package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

public class RnrBuilder {


  public static final Instantiator<Rnr> defaultRnr = new Instantiator<Rnr>() {

    @Override
    public Rnr instantiate(PropertyLookup<Rnr> lookup) {
      Rnr rnr = new Rnr();
      rnr.setFacilityId(1);
      rnr.setProgramId(1);
      rnr.setStatus(SUBMITTED);
      rnr.setFullSupplyItemsSubmittedCost(0f);
      RnrLineItem rnrLineItemCost48 = make(a(RnrLineItemBuilder.defaultRnrLineItem));
      rnr.add(rnrLineItemCost48);

      return rnr;
    }
  };
}
