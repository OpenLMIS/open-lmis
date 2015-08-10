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
package org.openlmis.web.controller;



import org.apache.log4j.Logger;
import org.openlmis.core.exception.DataException;
import org.openlmis.help.domain.HelpDocument;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.service.HelpTopicService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.NOT_FOUND;
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
    @Value("${help.document.uploadLocation}")
    private String fileStoreLocation;
    @Value("${help.document.accessBaseUrl}")
    private String fileAccessBaseUrl;


    @RequestMapping(value = "/createHelpTopic", method = RequestMethod.POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_HELP_CONTENT')")
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
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_HELP_CONTENT')")
    public ResponseEntity<OpenLmisResponse> edit(@RequestBody HelpTopic helpTopic, HttpServletRequest request) {
        helpTopic.setCreatedBy(loggedInUserId(request));
        helpTopic.setModifiedBy(loggedInUserId(request));
        helpTopic.setModifiedDate(new Date());
        helpTopic.setCreatedDate(new Date());
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

            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        } catch (DataException e) {

            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/helpTopicList", method = RequestMethod.GET, headers = "Accept=application/json")
      public ResponseEntity<OpenLmisResponse> getHelpToicsList() {
       return OpenLmisResponse.response(HELPTOPICLIST, this.helpTopicService.buildHelpTopicTree(null, true));
    }


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
        FileOutputStream outputStream = null;
        try {

            String fileName = null;
            String fileType = null;
            Long userId = loggedInUserId(request);

            String filePath = null;
            String uriPath = null;
            byte[] byteFile = null;
            InputStream inputStream = null;
            uriPath = request.getRequestURL().toString();
            int index = uriPath.indexOf("/uploadDocument");
            uriPath = uriPath.substring(0, index);
            HelpDocument helpDocument = new HelpDocument();
            inputStream = helpDocuments.getInputStream();
            int val = inputStream.available();
            byteFile = new byte[val];
            inputStream.read(byteFile);
            fileName = helpDocuments.getOriginalFilename();
            fileType = helpDocuments.getContentType();


//            filePath = request.getSession().getServletContext().getRealPath("public/images/help/" + fileName);
            filePath = this.fileStoreLocation + fileName;

            helpDocument.setDocumentType(documentType);
            helpDocument.setFileUrl(fileName);
            helpDocument.setCreatedDate(new Date());
            helpDocument.setCreatedBy(userId);
            File file= new File(filePath);
            File directory=new File(this.fileStoreLocation);

            boolean isFileExist=directory.exists();
            if(isFileExist) {
                boolean isWritePermitted= directory.canWrite();
                if (isWritePermitted) {
                    outputStream = new FileOutputStream(file);

                    outputStream.write(byteFile);
                    outputStream.flush();
                    this.helpTopicService.uploadHelpDocument(helpDocument);
                    return this.successPage(1);
                } else {
                    return this.errorPage("No Permission To Upload At Specified Path");
                }
            }else{
                return this.errorPage("Upload Path do not Exist");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return this.errorPage("Cannot upload in this location");
        }

    }

    private ResponseEntity<OpenLmisResponse> successPage(int recordsProcessed) {
        Map<String, String> responseMessages = new HashMap<>();
        String message = messageService.message(UPLOAD_FILE_SUCCESS, recordsProcessed);
        responseMessages.put(SUCCESS, message);
        return response(responseMessages, OK, TEXT_HTML_VALUE);
    }
    private ResponseEntity<OpenLmisResponse> errorPage(String message) {
        Map<String, String> responseMessages = new HashMap<>();
//        String message = "File Location Not Authorized";//messageService.message(UploadException);
        responseMessages.put(ERROR, message);
        return response(responseMessages,NOT_FOUND , TEXT_HTML_VALUE);
    }

    ///////////////////////////////////////
    @RequestMapping(value = "/loadDocumentList", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> loadHelpDocumentList(HttpServletRequest request) {
        List<HelpDocument> helpDocumentList = null;
        String uriPath = null;


        helpDocumentList = this.helpTopicService.loadHelpDocumentList();

        uriPath = this.fileAccessBaseUrl;

        for (HelpDocument helpDocument : helpDocumentList) {
            String imageUrl = uriPath + helpDocument.getFileUrl();
            helpDocument.setFileUrl(imageUrl);
        }
        return OpenLmisResponse.response(HELPDOCUMENTLIST, helpDocumentList);
    }
}
