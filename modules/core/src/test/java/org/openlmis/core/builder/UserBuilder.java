/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.User;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class UserBuilder {

  public static final Property<User, String> userName = newProperty();
  public static final Property<User, Integer> facilityId = newProperty();
  public static final Property<User, String> firstName = newProperty();
  public static final Property<User, String> lastName = newProperty();
  public static final Property<User, String> employeeId = newProperty();
  public static final Property<User, String> jobTitle = newProperty();
  public static final Property<User, String> primaryNotificationMethod = newProperty();
  public static final Property<User, String> officePhone = newProperty();
  public static final Property<User, String> cellPhone = newProperty();
  public static final Property<User, String> email = newProperty();
  public static final Property<User, Integer> supervisorId = newProperty();
  public static final Property<User, String> supervisorUserName = newProperty();

  public static final String defaultUserName = "User123";
  public static final String defaultPassword = "not-in-use";
  public static final String defaultFirstName = "Mizengo";
  public static final String defaultLastName = "Pinda";
  public static final String defaultEmployeeId = "E001_001";
  public static final String defaultJobTitle = "Facility Head";
  public static final String defaultPrimaryNotificationMethod = "Email";
  public static final String defaultOfficePhone = "0041-298-28904863";
  public static final String defaultCellPhone = "0041-9876389574";
  public static final String defaultEmail = "mizengo_pinda@openlmis.com";
  public static final Integer defaultSupervisorId = 1;
  public static final String defaultSupervisorUserName = "supervisorUserName";

  public static final Instantiator<User> defaultUser = new Instantiator<User>() {

    @Override
    public User instantiate(PropertyLookup<User> lookup) {
      User user = new User();
      user.setUserName(lookup.valueOf(userName, defaultUserName));
      user.setFacilityId(lookup.valueOf(facilityId, 9999));
      user.setFirstName(lookup.valueOf(firstName, defaultFirstName));
      user.setLastName(lookup.valueOf(lastName, defaultLastName));
      user.setEmployeeId(lookup.valueOf(employeeId, defaultEmployeeId));
      user.setJobTitle(lookup.valueOf(jobTitle, defaultJobTitle));
      user.setPrimaryNotificationMethod(lookup.valueOf(primaryNotificationMethod, defaultPrimaryNotificationMethod));
      user.setOfficePhone(lookup.valueOf(officePhone, defaultOfficePhone));
      user.setCellPhone(lookup.valueOf(cellPhone, defaultCellPhone));
      user.setEmail(lookup.valueOf(email, defaultEmail));
      User supervisor = new User();
      supervisor.setId(lookup.valueOf(supervisorId, defaultSupervisorId));
      supervisor.setUserName(lookup.valueOf(supervisorUserName, defaultSupervisorUserName));

      if (null != supervisor.getUserName())
        supervisor.setModifiedDate(new Date());

      user.setSupervisor(supervisor);
      user.setModifiedDate(new Date());

      return user;
    }
  };
}
