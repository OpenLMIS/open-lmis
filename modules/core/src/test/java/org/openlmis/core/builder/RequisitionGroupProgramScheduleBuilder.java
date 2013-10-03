/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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