package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class RnrBuilder {

  public static final Property<Rnr, RnrStatus> status = newProperty();
  public static final Instantiator<Rnr> defaultRnr = new Instantiator<Rnr>() {

    @Override
    public Rnr instantiate(PropertyLookup<Rnr> lookup) {
      Rnr rnr = new Rnr();
      rnr.setId(1);
      rnr.setFacilityId(1);
      rnr.setProgramId(1);
      rnr.setStatus(lookup.valueOf(status, RnrStatus.INITIATED));
      RnrLineItem rnrLineItemCost48 = make(a(RnrLineItemBuilder.defaultRnrLineItem));
      rnr.add(rnrLineItemCost48);

      return rnr;
    }
  };
}
