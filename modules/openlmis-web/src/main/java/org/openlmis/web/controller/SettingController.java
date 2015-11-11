/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.web.model.ConfigurationDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
public class SettingController extends BaseController {

  public static final String SETTINGS = "settings";

  @Autowired
  private ConfigurationSettingService configurationService;


  @RequestMapping(value = "/settings", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SETTING')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    ConfigurationDTO dto = new ConfigurationDTO();
    dto.setList( configurationService.getConfigurations() );
    return OpenLmisResponse.response(SETTINGS, dto );
  }

  @RequestMapping(value = "/settings/{key}",  method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<OpenLmisResponse> getByKey(@PathVariable(value = "key") String key) {
    return OpenLmisResponse.response(SETTINGS, configurationService.getByKey(key) );
  }

  @RequestMapping(value = "/saveSettings", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SETTING')")
  public ResponseEntity<OpenLmisResponse> updateSettings(@RequestBody ConfigurationDTO settings) {
    configurationService.update(settings.getList());
    return OpenLmisResponse.response(SETTINGS, "success");
  }

}
