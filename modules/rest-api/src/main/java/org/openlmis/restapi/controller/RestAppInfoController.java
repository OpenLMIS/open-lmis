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

package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import org.openlmis.restapi.domain.RestAppInfoRequest;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestAppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Api(value = "App Info", description = "update app info", position = 0)
public class RestAppInfoController extends BaseController {

    @Autowired
    private RestAppInfoService restAppInfoService;

    @RequestMapping(value = "/rest-api/update-app-info", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity<RestResponse> updateAppInfo(@RequestBody RestAppInfoRequest restAppInfoRequest) {
        int result = restAppInfoService.createOrUpdateVersion(restAppInfoRequest);
        if (result > 0) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
