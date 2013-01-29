package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;

public class RequisitionBuilder {

  public static final Property<Rnr, RnrStatus> status = newProperty();
  public static final Property<Rnr, Date> submittedDate = newProperty();
  public static final Property<Rnr, Integer> periodId = newProperty();

  public static final Instantiator<Rnr> defaultRnr = new Instantiator<Rnr>() {

    @Override
    public Rnr instantiate(PropertyLookup<Rnr> lookup) {
      Rnr rnr = new Rnr();
      rnr.setId(1);
      rnr.setFacility(make(a(FacilityBuilder.defaultFacility)));
      rnr.setFacilityId(1);
      rnr.setProgram(make(a(ProgramBuilder.defaultProgram)));
      rnr.setProgramId(1);
      rnr.setPeriodId(lookup.valueOf(periodId, 1));
      rnr.setPeriod(make(a(defaultProcessingPeriod)));
      rnr.getProgram().setId(3);
      rnr.getFacility().setId(3);
      rnr.getPeriod().setId(3);
      rnr.setStatus(lookup.valueOf(status, RnrStatus.INITIATED));
      rnr.setSubmittedDate(lookup.valueOf(submittedDate, new Date()));
      RnrLineItem rnrLineItemCost48 = make(a(defaultRnrLineItem));
      rnr.add(rnrLineItemCost48);

      return rnr;
    }
  };
}
