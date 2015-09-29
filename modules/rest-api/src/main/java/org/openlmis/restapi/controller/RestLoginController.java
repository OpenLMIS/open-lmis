package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.restapi.domain.LoginInformation;
import org.openlmis.restapi.domain.RestLoginRequest;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
public class RestLoginController extends BaseController {

    @Autowired
    private RestLoginService restLoginService;

    @RequestMapping(value = "/rest-api/login", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity<RestResponse> login(@RequestBody RestLoginRequest restLogin) {
        try {
            LoginInformation loginInformation = restLoginService.login(restLogin.getUsername(), restLogin.getPassword());
            return RestResponse.response("userInformation", loginInformation);
        } catch (BadCredentialsException e) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}