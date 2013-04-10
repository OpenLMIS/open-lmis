/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.service;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class UserAuthenticationService {

  private static final boolean AUTHORIZATION_SUCCESSFUL = true;
  private static final boolean AUTHORIZATION_FAILED = false;

  private UserService userService;

  @Autowired
  public UserAuthenticationService(UserService userService) {
    this.userService = userService;
  }

  public UserToken authorizeUser(User user) {
    User fetchedUser = userService.selectUserByUserNameAndPassword(user.getUserName(),
      user.getPassword());
    if (fetchedUser == null) return new UserToken(user.getUserName(), null, AUTHORIZATION_FAILED);

    return new UserToken(fetchedUser.getUserName(), fetchedUser.getId(), AUTHORIZATION_SUCCESSFUL);
  }
}
