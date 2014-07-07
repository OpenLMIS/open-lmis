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
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RightService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.utils.RightUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.any;

/**
 * This class is responsible for checking if the user has the given rights.
 */

@Component
@NoArgsConstructor
public class PermissionEvaluator {

  @Autowired
  private RoleRightsService roleRightService;

  @Autowired
  private RightService rightService;

  public Boolean hasPermission(Long userId, String commaSeparatedRights) {
    List<Right> userRights = roleRightService.getRights(userId);
    return any(userRights, RightUtil.contains(getRightNamesList(commaSeparatedRights)));
  }

  public Boolean hasReportingPermission(Long userId) {
    return rightService.hasReportingRight(userId);
  }

  private List<String> getRightNamesList(String commaSeparatedRights) {
    List<String> rights = new ArrayList<>();
    String[] permissions = commaSeparatedRights.split(",");
    for (String permission : permissions) {
      rights.add(permission.trim());
    }
    return rights;
  }
}
