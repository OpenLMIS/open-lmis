/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.web;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for checking if the user has the given rights.
 */

@Component
@NoArgsConstructor
public class PermissionEvaluator {

  @Autowired
  private RoleRightsService roleRightService;

  public Boolean hasPermission(Long userId, String commaSeparatedRights) {
    return CollectionUtils.containsAny(roleRightService.getRights(userId), getRightList(commaSeparatedRights));
  }

  private List<Right> getRightList(String commaSeparatedRights) {
    List<Right> rights = new ArrayList<>();
    String[] permissions = commaSeparatedRights.split(",");
    for (String permission : permissions) {
      rights.add(Right.valueOf(permission.trim()));
    }

    return rights;
  }
}
