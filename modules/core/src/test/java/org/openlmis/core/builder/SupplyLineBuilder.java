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
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;

public class SupplyLineBuilder {

  public static final Property<SupplyLine, SupervisoryNode> supervisoryNode = newProperty();
  public static final Property<SupplyLine, Facility> facility = newProperty();
  public static final Property<SupplyLine, Program> program = newProperty();

  private static SupervisoryNode defaultSupervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));

  public static final Instantiator<SupplyLine> defaultSupplyLine = new Instantiator<SupplyLine>() {
    @Override
    public SupplyLine instantiate(PropertyLookup<SupplyLine> lookup) {
      Facility defaultFacility = make(a(FacilityBuilder.defaultFacility));
      Program program = make(a(ProgramBuilder.defaultProgram));

      SupplyLine supplyLine = new SupplyLine();
      supplyLine.setSupervisoryNode(lookup.valueOf(supervisoryNode, defaultSupervisoryNode));
      supplyLine.setSupplyingFacility(lookup.valueOf(facility, defaultFacility));
      supplyLine.setProgram(lookup.valueOf(SupplyLineBuilder.program, program));
      supplyLine.setExportOrders(true);

      return supplyLine;
    }
  };
}
