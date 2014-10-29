package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.service.HelpContentService;
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

import static org.openlmis.web.response.OpenLmisResponse.error;

/**
 * Created by seifu on 10/20/2014.
 */
@Controller
public class HelpContentController extends BaseController {
    @Autowired
    private HelpContentService helpContentService;
    private HelpTopicService helpTopicService;
    public static final String HELPCONTENT = "helpContent";
    public static final String HELPCONTENTLIST = "helpContentList";

    @RequestMapping(value = "/createHelpContent", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody HelpContent helpContent, HttpServletRequest request) {
        //System.out.println(" here saving help Content");
        helpContent.setCreatedBy(loggedInUserId(request));
        helpContent.setModifiedBy(loggedInUserId(request));
        helpContent.setModifiedDate(new Date());
        helpContent.setCreatedDate(new Date());
        //System.out.println(" help content id is " + helpContent.getName());
        return saveHelpContent(helpContent, true);
    }

    @RequestMapping(value = "/updateHelpContent", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> updateHelpContent(@RequestBody HelpContent helpContent, HttpServletRequest request) {
        //System.out.println(" here updating help Content");
        helpContent.setModifiedBy(loggedInUserId(request));
        helpContent.setModifiedDate(new Date());
        return saveHelpContent(helpContent, false);
    }

    @RequestMapping(value = "/edit1/:id", method = RequestMethod.POST, headers = ACCEPT_JSON)
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> edit(@RequestBody HelpContent helpContent, HttpServletRequest request) {
        //System.out.println(" here updating help topic");
        helpContent.setCreatedBy(loggedInUserId(request));
        helpContent.setModifiedBy(loggedInUserId(request));
        helpContent.setModifiedDate(new Date());
        helpContent.setCreatedDate(new Date());
        //System.out.println(" help topic id is" + helpContent.getName());
        return saveHelpContent(helpContent, false);
    }

    private ResponseEntity<OpenLmisResponse> saveHelpContent(HelpContent helpContent, boolean createOperation) {
        try {
            if (createOperation) {
                this.helpContentService.addNewHelpContent(helpContent);
            }else{
                this.helpContentService.updateHelpContent(helpContent);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + helpContent.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(HELPCONTENT, this.helpContentService.getHelpContentById(helpContent.getId()));
            response.getBody().addData(HELPCONTENTLIST, this.helpContentService.loadAllHelpContentList());
            return response;
        } catch (DuplicateKeyException exp) {
            //System.out.println(exp.getStackTrace());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            //System.out.println(e.getStackTrace());
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }

    // supply line list for view
    @RequestMapping(value = "/helpContentList", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getHelpToicsList() {
        //System.out.println(" here calling");
        return OpenLmisResponse.response(HELPCONTENTLIST, this.helpContentService.loadAllHelpContentList());
    }

    // supply line list for view
    @RequestMapping(value = "/helpContentDetail/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
//    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getHelpContentDetail(@PathVariable("id") Long id) {
        //System.out.println(" here calling for help content detail ");
        HelpContent helpContent = this.helpContentService.getHelpContentById(id);
        return OpenLmisResponse.response(HELPCONTENT, helpContent);
    }
}
