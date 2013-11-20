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
import org.openlmis.core.domain.User;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class UserBuilder {

  public static final Property<User, String> userName = newProperty();
  public static final Property<User, Long> facilityId = newProperty();
  public static final Property<User, String> firstName = newProperty();
  public static final Property<User, String> lastName = newProperty();
  public static final Property<User, String> employeeId = newProperty();
  public static final Property<User, String> jobTitle = newProperty();
  public static final Property<User, String> primaryNotificationMethod = newProperty();
  public static final Property<User, String> officePhone = newProperty();
  public static final Property<User, String> cellPhone = newProperty();
  public static final Property<User, String> email = newProperty();
  public static final Property<User, Long> supervisorId = newProperty();
  public static final Property<User, String> supervisorUserName = newProperty();
  public static final Property<User, Boolean> verified = newProperty();
  public static final Property<User, Boolean> active = newProperty();
  public static final Property<User, Boolean> restrictLogin = newProperty();

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
  public static final Long defaultSupervisorId = 1L;
  public static final String defaultSupervisorUserName = "supervisorUserName";
  public static final Boolean defaultVerified = false;
  public static final Boolean defaultActive = true;
  public static final Boolean defaultRestrictLogin = false;


  public static final Instantiator<User> defaultUser = new Instantiator<User>() {

    @Override
    public User instantiate(PropertyLookup<User> lookup) {
      User user = new User();
      user.setUserName(lookup.valueOf(userName, defaultUserName));
      user.setFacilityId(lookup.valueOf(facilityId, 9999L));
      user.setFirstName(lookup.valueOf(firstName, defaultFirstName));
      user.setLastName(lookup.valueOf(lastName, defaultLastName));
      user.setEmployeeId(lookup.valueOf(employeeId, defaultEmployeeId));
      user.setJobTitle(lookup.valueOf(jobTitle, defaultJobTitle));
      user.setPrimaryNotificationMethod(lookup.valueOf(primaryNotificationMethod, defaultPrimaryNotificationMethod));
      user.setOfficePhone(lookup.valueOf(officePhone, defaultOfficePhone));
      user.setCellPhone(lookup.valueOf(cellPhone, defaultCellPhone));
      user.setEmail(lookup.valueOf(email, defaultEmail));
      user.setVerified(lookup.valueOf(verified, defaultVerified));
      user.setActive(lookup.valueOf(active, defaultActive));
      user.setRestrictLogin(lookup.valueOf(restrictLogin, defaultRestrictLogin));
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
