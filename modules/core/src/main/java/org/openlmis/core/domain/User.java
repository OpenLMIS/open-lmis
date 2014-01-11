/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

  @ImportField(name = "Restrict Login", type = "boolean")
  private Boolean restrictLogin;

  private Long facilityId;
  private List<RoleAssignment> supervisorRoles;
  private List<RoleAssignment> homeFacilityRoles;
  private List<RoleAssignment> allocationRoles;
  private List<FulfillmentRoleAssignment> fulfillmentRoles;

  private RoleAssignment reportRoles;

  private RoleAssignment adminRole;

  private Boolean verified;

  private Boolean active;

  public User(Long id, String userName) {
    this.id = id;
    this.userName = userName;
  }

  public User(String userName) {
    this(null, userName);
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
      throw new DataException("user.email.invalid");
  }

  private void validateUserName() {
    if (userName != null && userName.trim().contains(" "))
      throw new DataException("user.userName.invalid");
  }

  public User basicInformation() {
    return new User(id, userName);
  }
}
