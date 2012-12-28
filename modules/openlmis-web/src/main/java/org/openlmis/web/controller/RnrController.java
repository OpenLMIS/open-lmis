package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

@Controller
@NoArgsConstructor
public class RnrController {

    private RnrService rnrService;


    @Autowired
    public RnrController(RnrService rnrService) {
        this.rnrService = rnrService;
    }

    @RequestMapping(value = "/logistics/rnr/facility/{facilityId}/program/{programId}", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initRnr(@PathVariable("facilityId") Integer facilityId, @PathVariable("programId") Integer programId, HttpServletRequest request) {
        String modifiedBy = (String) request.getSession().getAttribute(USER);
        try {
            return OpenLmisResponse.response("rnr", rnrService.initRnr(facilityId, programId, modifiedBy));
        } catch (DataException e) {
            return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logistics/rnr/facility/{facilityId}/program/{programId}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable("facilityId") Integer facilityId, @PathVariable("programId") Integer programId) {
        try {
            return OpenLmisResponse.response("rnr", rnrService.get(facilityId, programId));
        } catch (DataException e) {
            return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/logistics/rnr/facility/{facilityId}/program/{programId}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
    public void saveRnr(@RequestBody Rnr rnr, HttpServletRequest request) {
        rnr.setModifiedBy((String) request.getSession().getAttribute(USER));
        rnrService.save(rnr);
    }

    @RequestMapping(value = "/logistics/rnr/lossAndAdjustment/{lossAndAdjustmentId}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
    public void removeLossAndAdjustment(@PathVariable("lossAndAdjustmentId") Integer lossAndAdjustmentId) {
        rnrService.removeLossAndAdjustment(lossAndAdjustmentId);
    }
}
