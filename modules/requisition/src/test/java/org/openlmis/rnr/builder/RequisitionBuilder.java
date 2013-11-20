/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;

import java.util.Arrays;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.Property.newProperty;
import static java.lang.Boolean.FALSE;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;

public class RequisitionBuilder {

  public static final Property<Rnr, RnrStatus> status = newProperty();
  public static final Property<Rnr, Date> submittedDate = newProperty();
  public static final Property<Rnr, Long> periodId = newProperty();
  public static final Property<Rnr, Long> modifiedBy = newProperty();
  public static final Property<Rnr, Facility> facility = newProperty();
  public static final Property<Rnr, Program> program = newProperty();
  public static final Property<Rnr, Long> id = newProperty();
  public static final Property<Rnr, Boolean> emergency = newProperty();
  public static final Property<Rnr, ProcessingPeriod> period = newProperty();

  public static final Date SUBMITTED_DATE = new DateTime().withDate(2013, 3, 19).toDate();
  public static final Program PROGRAM = make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programId, 3L)));
  public static final Long ID = 1L;
  public static final Instantiator<Rnr> defaultRequisition = new Instantiator<Rnr>() {
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
    Long processingPeriodId = lookup.valueOf(periodId, 3L);
    ProcessingPeriod period =
        make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.id, processingPeriodId)));
    rnr.setPeriod(lookup.valueOf(RequisitionBuilder.period, period));
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
