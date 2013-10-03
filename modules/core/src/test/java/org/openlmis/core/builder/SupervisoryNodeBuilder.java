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
import org.openlmis.core.domain.SupervisoryNode;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class SupervisoryNodeBuilder {
  public static final Property<SupervisoryNode, String> code = newProperty();
  public static final Property<SupervisoryNode, Long> id = newProperty();
  public static final Property<SupervisoryNode, String> name = newProperty();
  public static final Property<SupervisoryNode, Date> modifiedDate = newProperty();
  public static final Property<SupervisoryNode, Facility> facility = newProperty();

  public static final String SUPERVISORY_NODE_CODE = "N1";
  public static final String SUPERVISORY_NODE_NAME = "Approval Point 1";
  public static final Date SUPERVISORY_NODE_DATE = new Date(0);

  public static Long nullLong = null;

  public static final Instantiator<SupervisoryNode> defaultSupervisoryNode = new Instantiator<SupervisoryNode>() {
    @Override
    public SupervisoryNode instantiate(PropertyLookup<SupervisoryNode> lookup) {
      SupervisoryNode supervisoryNode = new SupervisoryNode();
      supervisoryNode.setId(lookup.valueOf(id, nullLong));
      supervisoryNode.setCode(lookup.valueOf(code, SUPERVISORY_NODE_CODE));
      supervisoryNode.setName(lookup.valueOf(name, SUPERVISORY_NODE_NAME));
      supervisoryNode.setFacility(lookup.valueOf(facility, make(a(defaultFacility))));
      supervisoryNode.setModifiedBy(1L);
      supervisoryNode.setModifiedDate(lookup.valueOf(modifiedDate, SUPERVISORY_NODE_DATE));
      return supervisoryNode;
    }
  };
}
