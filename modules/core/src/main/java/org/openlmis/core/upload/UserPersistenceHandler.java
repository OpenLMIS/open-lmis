/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.openlmis.core.repository.UserRepository.DUPLICATE_USER_NAME_FOUND;

@Component
@NoArgsConstructor
public class UserPersistenceHandler extends AbstractModelPersistenceHandler {

  private UserService userService;
  private String baseUrl;
  public static final String RESET_PASSWORD_PATH = "public/pages/reset-password.html#/token/";

  @Autowired
  public UserPersistenceHandler(UserService userService, @Value("${mail.base.url}") String baseUrl) {
    this.userService = userService;
    this.baseUrl = baseUrl;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return userService.getByUsernameAndVendorId((User) record);
  }

  @Override
  protected void save(BaseModel record) {
    User user = (User) record;
    userService.createUser(user, baseUrl + RESET_PASSWORD_PATH);
  }

  @Override
  public String getMessageKey() {
    return DUPLICATE_USER_NAME_FOUND;
  }

}