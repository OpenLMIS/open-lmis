package org.openlmis.web.controller;

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

@Controller
public class RnrController {

    private RnrService rnrService;

    public RnrController() {
    }

    @Autowired
    public RnrController(RnrService rnrService) {
        this.rnrService = rnrService;
    }



    @RequestMapping(value = "/logistics/rnr/{facilityId}/{programCode}/init", method = RequestMethod.POST, headers = "Accept=application/json")
    public Rnr initRnr(@PathVariable("facilityId") int facilityId, @PathVariable("programCode") String programCode, HttpServletRequest request) {
        String modifiedBy = (String) request.getSession().getAttribute(USER);
        return rnrService.initRnr(facilityId, programCode, modifiedBy);
    }

    @RequestMapping(value = "/logistics/rnr/{rnrId}/save", method = RequestMethod.POST, headers = "Accept=application/json")
    public void saveRnr(@RequestBody Rnr rnr, HttpServletRequest request){
        rnr.setModifiedBy((String) request.getSession().getAttribute(USER));
        rnrService.save(rnr);
    }

}
