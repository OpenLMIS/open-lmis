/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RequisitionGroupProgramScheduleBuilder {

  public static final Property<RequisitionGroupProgramSchedule, String> code = newProperty();
  public static final Property<RequisitionGroupProgramSchedule, String> programCode = new Property<>();
  public static final Property<RequisitionGroupProgramSchedule, String> requisitionGroupCode = new Property<>();

  public static final String PROGRAM_CODE = "HIV";
  public static final String REQUISITION_GROUP_CODE = "REQ_GRP_CODE";

  public static final Instantiator<RequisitionGroupProgramSchedule> defaultRequisitionGroupProgramSchedule = new Instantiator<RequisitionGroupProgramSchedule>() {
    @Override
    public RequisitionGroupProgramSchedule instantiate(PropertyLookup<RequisitionGroupProgramSchedule> lookup) {
      RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
      Program program = new Program();
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));

      RequisitionGroup requisitionGroup = new RequisitionGroup();
      requisitionGroup.setCode(lookup.valueOf(requisitionGroupCode, REQUISITION_GROUP_CODE));
      requisitionGroupProgramSchedule.setRequisitionGroup(requisitionGroup);
      requisitionGroupProgramSchedule.setProgram(program);
      return requisitionGroupProgramSchedule;
    }
  };
}