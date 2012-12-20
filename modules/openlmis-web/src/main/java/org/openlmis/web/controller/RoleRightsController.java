package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@NoArgsConstructor
public class RoleRightsController {


    private RoleRightsService roleRightsService;

    @Autowired
    public RoleRightsController(RoleRightsService roleRightsService) {
        this.roleRightsService = roleRightsService;
    }

    @RequestMapping(value = "/rights", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Right> getAllRights() {
        return roleRightsService.getAllRights();
    }
}
