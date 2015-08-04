package org.openlmis.restapi.service;

import org.apache.commons.collections.Closure;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.openlmis.restapi.domain.Program;
import org.openlmis.restapi.domain.LoginInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;

@Service
public class RestLoginService {

  @Autowired
  private UserAuthenticationService userAuthenticationService;

  @Autowired
  private UserService userService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramMapper programMapper;

  MessageService messageService = MessageService.getRequestInstance();

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
      return LoginInformation.prepareForREST(user, facility, getProgramsByFacility(facilityId));
    } else {
      return LoginInformation.prepareForREST(user, null, null);
    }
  }

  private List<Program> getProgramsByFacility(Long facilityId) {

    List<Program> programs = new ArrayList();

    for (org.openlmis.core.domain.Program activeProgram: programMapper.getActiveByFacility(facilityId)) {
      programs.add(new Program(activeProgram.getCode(), activeProgram.getName()));
    }
    return programs;
  }
}
