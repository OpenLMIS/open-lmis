/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *   Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ELMISInterface;
import org.openlmis.core.domain.ELMISInterfaceFacilityMapping;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ELMISInterfaceService;
import org.openlmis.web.form.ProductDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class ELMISInterfacesController extends BaseController {

    @Autowired
    private ELMISInterfaceService elmisInterfaceService;

    @RequestMapping(value = "/ELMISInterface/{interfaceId}", method = GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getELMISInterfaceById(HttpServletRequest request,
                                                                  @PathVariable long interfaceId) {

        ELMISInterface elmisInterface = elmisInterfaceService.get(interfaceId);
        return OpenLmisResponse.response("interface", elmisInterface);
    }

    @RequestMapping(value = "/ELMISInterface", method = POST, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ELMIS_INTERFACE')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody ELMISInterface elmisInterface, HttpServletRequest request) {

        Long userId = loggedInUserId(request);
        elmisInterface.setCreatedBy(userId);
        elmisInterface.setModifiedBy(userId);

        try {
            elmisInterfaceService.save(elmisInterface);
        } catch (DataException e) {
            return OpenLmisResponse.error(e, BAD_REQUEST);
        }

        String successMessage = messageService.message("message.elmisInterface.save.sucess", elmisInterface.getName());
        OpenLmisResponse openLmisResponse = new OpenLmisResponse("interface", elmisInterface);
        return openLmisResponse.successEntity(successMessage);

    }

    @RequestMapping(value = "/ELMISAllInterfaces", method = GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getAllInterfaces(HttpServletRequest request) {

        List<ELMISInterface> interfaces = elmisInterfaceService.getAllInterfaces();
        return OpenLmisResponse.response("interfaces", interfaces);
    }

    @RequestMapping(value = "/ELMISAllActiveInterfaces", method = GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getAllActiveInterfaces(HttpServletRequest request) {

        List<ELMISInterface> interfaces = elmisInterfaceService.getAllActiveInterfaces();
        return OpenLmisResponse.response("activeInterfaces", interfaces);
    }

    @RequestMapping(value = "/ELMISInterfacesMappings", method = GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getInterfaceFacilityMappings(HttpServletRequest request) {

        List<ELMISInterfaceFacilityMapping> mappings = elmisInterfaceService.getInterfaceFacilityMappings();
        return OpenLmisResponse.response("interfacesMappings", mappings);
    }

    @RequestMapping(value = "/ELMISInterfacesMapping/{facilityId}", method = GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getFacilityInterfaceMappingById(@PathVariable Long facilityId, HttpServletRequest request) {

        List<ELMISInterfaceFacilityMapping> mapping = elmisInterfaceService.getFacilityInterfaceMappingById(facilityId);
        return OpenLmisResponse.response("interfacesMapping", mapping);
    }
}
