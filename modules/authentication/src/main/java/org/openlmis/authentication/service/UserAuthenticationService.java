/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.service;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for authenticating the given username, credential combination
 */

@Service
@NoArgsConstructor
public class UserAuthenticationService {

  private static final boolean AUTHENTICATION_SUCCESSFUL = true;
  private static final boolean AUTHENTICATION_FAILED = false;

  private UserService userService;

  @Autowired
  public UserAuthenticationService(UserService userService) {
    this.userService = userService;
  }

  public UserToken authenticateUser(User user) {
    User fetchedUser = userService.selectUserByUserNameAndPassword(user.getUserName(),
      user.getPassword());
    if (fetchedUser == null || fetchedUser.getRestrictLogin())
      return new UserToken(user.getUserName(), null, AUTHENTICATION_FAILED);

    return new UserToken(fetchedUser.getUserName(), fetchedUser.getId(), AUTHENTICATION_SUCCESSFUL);
  }
}
