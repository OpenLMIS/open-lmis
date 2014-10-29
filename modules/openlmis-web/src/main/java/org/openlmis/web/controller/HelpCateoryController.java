package org.openlmis.web.controller;

/**
 * Created by Teklu  on 10/19/2014.
 */

import org.openlmis.core.exception.DataException;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.service.HelpTopicService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.logging.Logger;

import static org.openlmis.web.response.OpenLmisResponse.error;

@Controller
public class HelpCateoryController extends BaseController {
    public static final String HELPTOPICLIST = "helpTopicList";
    public static final String HELPTOPIC = "helpTopic";
    public static final String HELPTOPICDETAIL = "helpTopic";
    @Autowired
    private HelpTopicService helpTopicService;

    // create product
    @RequestMapping(value = "/createHelpTopic", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody HelpTopic helpTopic, HttpServletRequest request) {
        //System.out.println(" here saving help topic");
        helpTopic.setCreatedBy(loggedInUserId(request));
        helpTopic.setModifiedBy(loggedInUserId(request));
        helpTopic.setModifiedDate(new Date());
        helpTopic.setCreatedDate(new Date());
        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveHelpTopic(helpTopic, true);
    }

    @RequestMapping(value = "/edit/:id", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> edit(@RequestBody HelpTopic helpTopic, HttpServletRequest request) {
        //System.out.println(" here updating help topic");
        helpTopic.setCreatedBy(loggedInUserId(request));
        helpTopic.setModifiedBy(loggedInUserId(request));
        helpTopic.setModifiedDate(new Date());
        helpTopic.setCreatedDate(new Date());
        //System.out.println(" help topic id is" + helpTopic.getName());
        return saveHelpTopic(helpTopic, false);
    }

    private ResponseEntity<OpenLmisResponse> saveHelpTopic(HelpTopic helpTopic, boolean createOperation) {
        try {
            this.helpTopicService.addHelpTopic(helpTopic);


            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + helpTopic.getName()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(HELPTOPIC, this.helpTopicService.get(helpTopic.getId()));
            response.getBody().addData(HELPTOPICLIST, this.helpTopicService.buildHelpTopicTree(null,true ));
            return response;
        } catch (DuplicateKeyException exp) {
           // //System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
           // //System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
           // //System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }

    // supply line list for view
    @RequestMapping(value = "/helpTopicList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getHelpToicsList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(HELPTOPICLIST, this.helpTopicService.buildHelpTopicTree(null, true));
    }

    // supply line list for view
    @RequestMapping(value = "/helpTopicDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getHelpTopicDetail(@PathVariable("id") Long id) {
        //System.out.println(" here calling");
        HelpTopic helpTopic = this.helpTopicService.get(id);
        return OpenLmisResponse.response(HELPTOPICDETAIL, helpTopic);
    }

    @RequestMapping(value = "/updateHelpTopic", method = RequestMethod.POST, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> update(@RequestBody HelpTopic helpTopic, HttpServletRequest request) {
        //System.out.println(" updating ");
        this.helpTopicService.updateHelpTopicRole(helpTopic);
        HelpTopic updatedHelpTopic = this.helpTopicService.get(helpTopic.getId());
        return OpenLmisResponse.response(HELPTOPICDETAIL, updatedHelpTopic);
    }

    // supply line list for view
    @RequestMapping(value = "/helpTopicForCreate", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> intializeHelptopic() {
        //System.out.println(" here calling");
        HelpTopic helpTopic = this.helpTopicService.intializeHelpTopicForCreate();
        return OpenLmisResponse.response(HELPTOPICDETAIL, helpTopic);
    }

    // supply line list for view
    @RequestMapping(value = "/userHelpTopicList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getUserHelpToicsList(HttpServletRequest request) {
        //System.out.println(" here calling");
        Long userId = loggedInUserId(request);
        //System.out.println(" uz" + userId);
        return OpenLmisResponse.response(HELPTOPICLIST, this.helpTopicService.buildRoleHelpTopicTree(userId,null, true));
    }
}
