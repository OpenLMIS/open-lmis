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
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

public class RoleAssignmentBuilder {

  public static Property<RoleAssignment, Long> userId = newProperty();
  public static Property<RoleAssignment, Long> programId = newProperty();
  public static Property<RoleAssignment, SupervisoryNode> supervisoryNode = newProperty();
  public static Property<RoleAssignment, DeliveryZone> deliveryZone = newProperty();
  public static Property<RoleAssignment, List<Long>> roleIds = newProperty();

  public static final long PROGRAM_ID = 1l;
  public static final long USER_ID = 1l;

  public static final Instantiator<RoleAssignment> defaultRoleAssignment = new Instantiator<RoleAssignment>() {

    @Override
    public RoleAssignment instantiate(PropertyLookup<RoleAssignment> lookup) {
      RoleAssignment roleAssignment = new RoleAssignment();
      roleAssignment.setProgramId(lookup.valueOf(programId, PROGRAM_ID));
      roleAssignment.setUserId(lookup.valueOf(userId, USER_ID));
      roleAssignment.setRoleIds(lookup.valueOf(roleIds, new ArrayList<Long>()));
      roleAssignment.setSupervisoryNode(lookup.valueOf(supervisoryNode, make(a(defaultSupervisoryNode))));
      roleAssignment.setDeliveryZone(lookup.valueOf(deliveryZone, make(a(defaultDeliveryZone))));

      return roleAssignment;
    }
  };

}
