/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
/**
 * UserPersistenceHandler is used for uploads of users. It uploads each User record by record.
 */
@Component
@NoArgsConstructor
public class UserPersistenceHandler extends AbstractModelPersistenceHandler {

  private UserService userService;
  private String baseUrl;
  public static final String RESET_PASSWORD_PATH = "/public/pages/reset-password.html#/token/";

  @Autowired
  public UserPersistenceHandler(UserService userService, @Value("${mail.base.url}") String baseUrl) {
    this.userService = userService;
    this.baseUrl = baseUrl;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return userService.getByUserName(((User) record).getUserName());
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