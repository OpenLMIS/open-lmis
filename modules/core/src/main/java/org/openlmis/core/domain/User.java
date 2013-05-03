/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.List;
import java.util.regex.Pattern;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class User extends BaseModel implements Importable {
  @ImportField(mandatory = true, name = "User Name")
  private String userName;

  @ImportField(mandatory = true, name = "Password")
  @JsonIgnore
  private String password;

  @JsonIgnore
  private Boolean active;

  @ImportField(mandatory = true, name = "First Name")
  private String firstName;

  @ImportField(mandatory = true, name = "Last Name")
  private String lastName;

  @ImportField(name = "Employee Id")
  private String employeeId;
  @ImportField(name = "Job Title")
  private String jobTitle;
  @ImportField(name = "Primary Notification Method")
  private String primaryNotificationMethod;
  @ImportField(name = "Office Phone")
  private String officePhone;
  @ImportField(name = "Cell Phone")
  private String cellPhone;
  @ImportField(mandatory = true, name = "Email")
  private String email;
  @ImportField(name = "Supervisor User Name", nested = "userName")
  private User supervisor;

  private Long facilityId;

  private List<RoleAssignment> supervisorRoles;
  private List<RoleAssignment> homeFacilityRoles;
  private RoleAssignment adminRole;

  private static final String INVALID_EMAIL_ERROR_CODE = "user.email.invalid";

  private static final String INVALID_USER_NAME_ERROR_CODE = "user.userName.invalid";
  private Long vendorId;

  public User(Long id, String userName) {
    this.id = id;
    this.userName = userName;
  }


  public void validate() {
    validateEmail();
    validateUserName();
  }

  public void setPassword(String password) {
    this.password = Encoder.hash(password);
  }

  private void validateEmail() {
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    if (email != null && !pattern.matcher(email).matches())
      throw new DataException(INVALID_EMAIL_ERROR_CODE);
  }

  private void validateUserName() {
    if (userName != null && userName.trim().contains(" "))
      throw new DataException(INVALID_USER_NAME_ERROR_CODE);
  }

  public User basicInformation() {
    return new User(id, userName);
  }
}
