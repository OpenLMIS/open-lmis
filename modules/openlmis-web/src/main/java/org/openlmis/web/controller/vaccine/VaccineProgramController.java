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

package org.openlmis.web.controller.vaccine;

import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VaccineProgramController extends BaseController {

    public static final String PROGRAMS = "programs";
    public static final String FACILITIES = "facilities";
    @Autowired
    VaccineReportService service;

    @Autowired
    ProgramService programService;

    @Autowired
    UserService userService;

    @Autowired
    FacilityService facilityService;

    @RequestMapping(value = "/vaccine/report/programs.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return OpenLmisResponse.response(PROGRAMS, programService.getAllIvdPrograms());
    }

    @RequestMapping(value = "/vaccine/report/ivd-form/programs.json", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD,APPROVE_IVD, VIEW_IVD')")
    public ResponseEntity<OpenLmisResponse> getProgramForIvdFormHomeFacility(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        User user = userService.getById(userId);
        return OpenLmisResponse.response(PROGRAMS, programService.getIvdProgramsSupportedByUserHomeFacilityWithRights(user.getFacilityId(), userId, RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD));
    }

    @RequestMapping(value = "/vaccine/report/ivd-form/supervised-programs")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD, APPROVE_IVD, VIEW_IVD')")
    public ResponseEntity<OpenLmisResponse> getProgramForIvdFormSupervisedFacilities(HttpServletRequest request) {
        return OpenLmisResponse.response(PROGRAMS, programService.getIvdProgramForSupervisedFacilities(loggedInUserId(request), RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD));
    }

    @RequestMapping(value = "/vaccine/report/ivd-form/facilities/{programId}.json", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD, APPROVE_IVD, VIEW_IVD')")
    public ResponseEntity<OpenLmisResponse> getFacilities(@PathVariable Long programId, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        //TODO: make sure this method also supports home facility.
        return OpenLmisResponse.response(FACILITIES, facilityService.getUserSupervisedFacilities(userId, programId, RightName.CREATE_IVD, RightName.APPROVE_IVD, RightName.VIEW_IVD));
    }



}
