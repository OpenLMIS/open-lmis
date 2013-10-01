/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.joda.time.DateTime;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;

import java.util.Arrays;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.Property.newProperty;
import static java.lang.Boolean.FALSE;

public class RequisitionBuilder {

  public static final Property<Rnr, RnrStatus> status = newProperty();
  public static final Property<Rnr, Date> submittedDate = newProperty();
  public static final Property<Rnr, Long> periodId = newProperty();
  public static final Property<Rnr, Long> modifiedBy = newProperty();
  public static final Property<Rnr, Facility> facility = newProperty();
  public static final Property<Rnr, Program> program = newProperty();
  public static final Property<Rnr, Long> id = newProperty();
  public static final Property<Rnr, Boolean> emergency = newProperty();

  public static final Date SUBMITTED_DATE = new DateTime().withDate(2013, 3, 19).toDate();
  public static final Program PROGRAM = make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programId, 3L)));
  public static final Long ID = 1L;
  public static final Instantiator<Rnr> defaultRnr = new Instantiator<Rnr>() {
    @Override
    public Rnr instantiate(PropertyLookup<Rnr> lookup) {
      Rnr rnr = getDefaultRnr(lookup);
      return rnr;
    }
  };

  private static Rnr getDefaultRnr(PropertyLookup<Rnr> lookup) {
    Rnr rnr = new Rnr();
    rnr.setId(lookup.valueOf(id, ID));
    Facility defaultFacility = make(a(FacilityBuilder.defaultFacility));
    defaultFacility.setId(3L);
    rnr.setFacility(lookup.valueOf(facility, defaultFacility));
    rnr.setProgram(lookup.valueOf(program, PROGRAM));
    rnr.setPeriod(make(a(ProcessingPeriodBuilder.defaultProcessingPeriod)));
    rnr.getPeriod().setId(lookup.valueOf(periodId, 3L));
    rnr.setStatus(lookup.valueOf(status, RnrStatus.INITIATED));
    rnr.setSubmittedDate(lookup.valueOf(submittedDate, SUBMITTED_DATE));
    rnr.setEmergency(lookup.valueOf(emergency, FALSE));
    RnrLineItem rnrLineItemCost48 = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    rnr.add(rnrLineItemCost48, true);
    rnr.setModifiedBy(lookup.valueOf(modifiedBy, 1L));
    rnr.setCreatedBy(lookup.valueOf(modifiedBy, 1L));
    return rnr;
  }

  public static final Instantiator<Rnr> rnrWithRegimens = new Instantiator<Rnr>() {
    @Override
    public Rnr instantiate(PropertyLookup<Rnr> lookup) {
      Rnr rnr = getDefaultRnr(lookup);
      rnr.setRegimenLineItems(Arrays.asList(make(a(RegimenLineItemBuilder.defaultRegimenLineItem))));
      return rnr;
    }
  };

}
