package org.openlmis.restapi.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramSupportedService;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Long facilityId;

    @Deprecated
    private List<String> facilitySupportedPrograms;

    public static LoginInformation prepareForREST(final User user, final Facility facility, List<String> programs) {
        LoginInformation loginInformation = new LoginInformation();
        loginInformation.setUserFirstName(user.getFirstName());
        loginInformation.setUserLastName(user.getLastName());
        loginInformation.setUserName(user.getUserName());

        if (facility != null) {
            loginInformation.setFacilityId(facility.getId());
            loginInformation.setFacilityCode(facility.getCode());
            loginInformation.setFacilityName(facility.getName());
            loginInformation.setFacilitySupportedPrograms(programs);
        }

        return loginInformation;
    }
}