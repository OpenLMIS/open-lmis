package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.UserService;
import org.openlmis.restapi.domain.LoginInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestLoginService {

    MessageService messageService = MessageService.getRequestInstance();
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ProgramSupportedService programSupportedService;

    public LoginInformation login(String username, String password) {
        authenticateUser(username, password);
        return getLoginInformation(username);
    }

    private UserToken authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        String userName = (String) authenticationToken.getPrincipal();
        String pass = (String) authenticationToken.getCredentials();

        User user = new User();
        user.setUserName(userName);
        user.setPassword(pass);

        UserToken userToken = userAuthenticationService.authenticateUser(user);

        if (userToken.isAuthenticated()) {
            return userToken;
        } else {
            throw new BadCredentialsException(messageService.message("error.authentication.failed"));
        }
    }

    private LoginInformation getLoginInformation(String username) {
        User user = userService.getByUserName(username);
        Long facilityId = user.getFacilityId();

        if (facilityId != null) {
            Facility facility = facilityService.getById(facilityId);
            List<String> programs = FluentIterable.from(programSupportedService.getActiveByFacilityId(facilityId)).transform(new Function<ProgramSupported, String>() {
                @Override
                public String apply(ProgramSupported input) {
                    return input.getProgram().getCode();
                }
            }).toList();
            return LoginInformation.prepareForREST(user, facility, programs);
        } else {
            return LoginInformation.prepareForREST(user, null, null);
        }
    }
}
