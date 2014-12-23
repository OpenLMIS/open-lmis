package org.openlmis.web.controller;

/**
 * Created by Teklu  on 10/19/2014.
 */

import org.apache.log4j.Logger;
import org.openlmis.core.exception.DataException;
import org.openlmis.help.domain.HelpDocument;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Controller
public class HelpCateoryController extends BaseController {
    public static final String HELPTOPICLIST = "helpTopicList";
    public static final String HELPDOCUMENTLIST = "helpDocumentList";
    public static final String HELPTOPIC = "helpTopic";
    public static final String HELPTOPICDETAIL = "helpTopic";
    public static final String UPLOAD_FILE_SUCCESS = "upload.file.successful";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final Logger LOGGER = Logger.getLogger(HelpCateoryController.class);
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
            response.getBody().addData(HELPTOPICLIST, this.helpTopicService.buildHelpTopicTree(null, true));
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
        return OpenLmisResponse.response(HELPTOPICLIST, this.helpTopicService.buildRoleHelpTopicTree(userId, null, true));
    }

    ///////////////////////////////////////////////////////////////
//   video image and file uploads
    @RequestMapping(value = "/uploadDocument", method = RequestMethod.POST)
    public ResponseEntity<OpenLmisResponse> uploadHelpDocuments(MultipartFile helpDocuments, String documentType, HttpServletRequest request) {
        try {
            String fileName = null;
            String fileType = null;
            Long userId = loggedInUserId(request);
            FileOutputStream outputStream = null;
            String filePath = null;
            String uriPath = null;
            byte[] byteFile = null;
            InputStream inputStream = null;
            uriPath = request.getRequestURL().toString();
            int index = uriPath.indexOf("/uploadDocument");
            uriPath = uriPath.substring(0, index);

            System.out.println(" and server name is " + uriPath);
            HelpDocument helpDocument = new HelpDocument();
            inputStream = helpDocuments.getInputStream();
            int val = inputStream.available();
            byteFile = new byte[val];
            inputStream.read(byteFile);
            fileName = helpDocuments.getOriginalFilename();
            fileType = helpDocuments.getContentType();

            System.out.println(" file name is " + fileName+ " file type is "+fileType);
            filePath = request.getSession().getServletContext().getRealPath("public\\images\\help\\" + fileName);
            System.out.println(" here  uploading calling " + documentType + " path is " + filePath);
            System.out.println(" here calling and size is " + val + documentType);
            helpDocument.setDocumentType(documentType);
            helpDocument.setFileUrl(fileName);
            helpDocument.setCreatedDate(new Date());
            helpDocument.setCreatedBy(userId);
            outputStream = new FileOutputStream(new File(filePath));
            outputStream.write(byteFile);
            outputStream.flush();
            this.helpTopicService.uploadHelpDocument(helpDocument);
            return this.successPage(1);
        } catch (Exception ex) {
System.out.println(ex.getMessage());
        }


        //System.out.println(" uz" + userId);
//        LOGGER.log(null, " uploading ");
        return null;
    }
    private ResponseEntity<OpenLmisResponse> successPage(int recordsProcessed) {
        Map<String, String> responseMessages = new HashMap<>();
        String message = messageService.message(UPLOAD_FILE_SUCCESS, recordsProcessed);
        responseMessages.put(SUCCESS, message);
        return response(responseMessages, OK, TEXT_HTML_VALUE);
    }
    ///////////////////////////////////////
    @RequestMapping(value = "/loadDocumentList", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> loadHelpDocumentList(HttpServletRequest request){
        List<HelpDocument> helpDocumentList=null;
        String uriPath=null;

        System.out.println("i am here "+uriPath);
        helpDocumentList=this.helpTopicService.loadHelpDocumentList();
        System.out.println("i am here ");
        uriPath = request.getRequestURL().toString();
        int firstIndex=uriPath.indexOf("://");
        int index = uriPath.indexOf("/",firstIndex+3);
        uriPath = uriPath.substring(0, index);
        System.out.println("i am here "+uriPath);
        for(HelpDocument helpDocument: helpDocumentList){
            String imageUrl= uriPath+"/public/images/help/"+helpDocument.getFileUrl();
            helpDocument.setFileUrl(imageUrl);
        }
        return OpenLmisResponse.response(  HELPDOCUMENTLIST, helpDocumentList);
    }
}
