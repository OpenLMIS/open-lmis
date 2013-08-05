package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ConfigurationService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/5/13
 * Time: 1:57 PM
 */
@Controller
@NoArgsConstructor
public class SettingController extends BaseController {
  @Autowired
  private ConfigurationService configurationService;


  @RequestMapping(value = "/settings", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SETTING')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response("settings", configurationService.getConfigurations());
  }

}
