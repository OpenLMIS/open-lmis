package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class LoginInformation {

  private String userName;

  private String userFirstName;

  private String userLastName;

  private String facilityCode;

  private String facilityName;

  private List<Program> programs;

  public static LoginInformation prepareForREST(final User user, final Facility facility, final List<Program> programs) {
    LoginInformation loginInformation = new LoginInformation();
    loginInformation.setUserFirstName(user.getFirstName());
    loginInformation.setUserLastName(user.getLastName());
    loginInformation.setUserName(user.getUserName());

    if (facility != null) {
      loginInformation.setFacilityCode(facility.getCode());
      loginInformation.setFacilityName(facility.getName());
    }

    if (programs != null && !programs.isEmpty()) {
      loginInformation.setPrograms(programs);
    }
    return loginInformation;
  }
}