package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;

import java.util.Date;

@Data
@NoArgsConstructor
public class User implements Importable {
  private Integer id;
  private String userName;
  private String password;
  private String firstName;
  private String lastName;
  private String employeeId;
  private String jobTitle;
  private String primaryNotificationMethod;
  private String officePhone;
  private String cellPhone;
  private String email;

  private Integer facilityId;
  private String modifiedBy;
  private Date modifiedDate;
  private User supervisor;
}
