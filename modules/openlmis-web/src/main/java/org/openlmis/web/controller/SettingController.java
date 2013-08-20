package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.web.model.ConfigurationDTO;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
  private ConfigurationSettingService configurationService;


  @RequestMapping(value = "/settings", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SETTING')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    ConfigurationDTO dto = new ConfigurationDTO();
    dto.setList( configurationService.getConfigurations() );
    return OpenLmisResponse.response("settings", dto );
  }

  @RequestMapping(value = "/saveSettings", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SETTING')")
  public ResponseEntity<OpenLmisResponse> updateSettings(@RequestBody ConfigurationDTO settings) {
    configurationService.update(settings.getList());
    return OpenLmisResponse.response("settings", "success");
  }

}
