package org.openlmis.web.controller;

/**
 * User: mahmed
 * Date: 6/19/13
 */
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.model.dto.SupplyLineList;
import org.openlmis.report.service.SupplyLineListDataProvider;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.response.OpenLmisResponse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.core.exception.DataException;
import static org.openlmis.web.response.OpenLmisResponse.error;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.Right.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@NoArgsConstructor
public class SupplyLineController extends BaseController {

    public static final String SUPPLYLINES= "supplylines";
    public static final String SUPPLYLINE= "supplyline";
    public static final String SUPPLYLINELIST= "supplyLineList";
    @Autowired
    private SupplyLineService supplyLineService;

    @Autowired
    private SupplyLineListDataProvider supplyLineListService;



    @Autowired
    public SupplyLineController(SupplyLineService supplyLineService) {
        this.supplyLineService = supplyLineService;
    }

    // TODO: implement this function in the repository
    // and remove this reference to the mapper.
    @Autowired
    private SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private FacilityService facilityService;

    // supply line list for view
    @RequestMapping(value = "/supplylineslist", method = RequestMethod.GET, headers = "Accept=application/json")
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> getAll() {
        return OpenLmisResponse.response(SUPPLYLINELIST, supplyLineListService.getAll());
    }

    // supply line for add/update
    @RequestMapping(value = "/supplylines", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> getAllSupplyLine() {
        return OpenLmisResponse.response(SUPPLYLINES, supplyLineService.getAllSupplyLine());
    }

    @RequestMapping(value = "/supplylines/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
        try{
            SupplyLine supplyLine = supplyLineService.get(id);
            return OpenLmisResponse.response(SUPPLYLINE, supplyLine);
        } catch (DataException e){
            return error(e, HttpStatus.NOT_FOUND);
        }
    }

    // create
    @RequestMapping(value = "/supplylines", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> create(@RequestBody SupplyLine supplyLine, HttpServletRequest request) {
        supplyLine.setModifiedBy(loggedInUserId(request));
        supplyLine.setCreatedBy(loggedInUserId(request));
        supplyLine.setCreatedDate(new Date());
        // load the supervisory node ... and attach it to the supply line object
        // this is requred by the valiation and the save.
        SupervisoryNode sn = supervisoryNodeMapper.getSupervisoryNode(Long.parseLong(supplyLine.getSupervisorynodeid().toString()));
        supplyLine.setSupervisoryNode(sn);
        // load the programs
        Program program = programService.getById(Long.parseLong(supplyLine.getProgramid().toString()));
        supplyLine.setProgram(program);

        Facility facility = facilityService.getById(Long.parseLong(supplyLine.getSupplyingfacilityid().toString()));
        supplyLine.setSupplyingFacility(facility);

        // there has to be a better way to do the code above
        supplyLine.setModifiedDate(new Date());
        return saveSupplyline(supplyLine, true);
    }

    // update
    @RequestMapping(value = "/supplylines/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
    public ResponseEntity<OpenLmisResponse> update(@RequestBody SupplyLine supplyLine, @PathVariable("id") Long id, HttpServletRequest request) {
        supplyLine.setId(id);
        // load the supervisory node ... and attach it to the supply line object
        // this is requred by the valiation and the save.
        SupervisoryNode sn = supervisoryNodeMapper.getSupervisoryNode(Long.parseLong(supplyLine.getSupervisorynodeid().toString()));
        supplyLine.setSupervisoryNode(sn);
        // load the programs
        Program program = programService.getById(Long.parseLong(supplyLine.getProgramid().toString()));
        supplyLine.setProgram(program);
        Facility facility = facilityService.getById(Long.parseLong(supplyLine.getSupplyingfacilityid().toString()));
        supplyLine.setSupplyingFacility(facility);
        // there has to be a better way to do the code above
        return saveSupplyline(supplyLine, false);
    }

    // save/update
    private ResponseEntity<OpenLmisResponse> saveSupplyline(SupplyLine supplyLine, boolean createOperation) {
        try {
            supplyLineService.save(supplyLine);
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("'" + supplyLine.getDescription() + "' "+ (createOperation?"created":"updated") +" successfully");
            response.getBody().addData(SUPPLYLINE, supplyLineService.get(supplyLine.getId()));
            response.getBody().addData(SUPPLYLINELIST, supplyLineListService.getAll());
            return response;
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
    }

       // mahmed - 07.11.2013  delete
       @RequestMapping(value = "/supplylineDelete/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
       @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLYLINE')")
       public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Long id, HttpServletRequest request) {
           try{
               supplyLineService.deleteById(id);
               ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("Supply line deleted successfully");
               response.getBody().addData(SUPPLYLINELIST, supplyLineListService.getAll());
               return response;
           }
           catch (DataException e) {
               return error(e, HttpStatus.BAD_REQUEST);
           }
       }




}
