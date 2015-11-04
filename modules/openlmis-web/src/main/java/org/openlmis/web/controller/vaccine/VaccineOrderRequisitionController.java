package org.openlmis.web.controller.vaccine;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.util.Constants;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionLineItemService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionsColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping(value = "/vaccine/orderRequisition/")
public class VaccineOrderRequisitionController extends BaseController {
    public static final String VaccineOrderRequisition = "orderRequisition";
    public static final String OrderRequisitionColumns = "columns";
    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    private static final String PRINT_ORDER_REQUISITION = "print_vaccine_Order_Requisition";
    private static final String PRINT_ISSUE_STOCK = "vims_distribution";
    private static final String ORDER_REQUISITION_SEARCH = "search";

    @Autowired
    VaccineOrderRequisitionService service;

    @Autowired
    FacilityService facilityService;
    @Autowired
    VaccineOrderRequisitionLineItemService lineItemService;
    @Autowired
    TemplateService templateService;
    @Autowired
    VaccineOrderRequisitionsColumnService columnService;
    @Autowired
    ProgramService programService;
    @Autowired
    UserService userService;
    @Autowired
    ConfigurationSettingService settingService;
    @Autowired
    private ProgramProductService programProductService;
    @Autowired
    private JasperReportsViewFactory jasperReportsViewFactory;

    @RequestMapping(value = "periods/{facilityId}/{programId}", method = RequestMethod.GET)
   //TODO// @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
        return response("periods", service.getPeriodsFor(facilityId, programId, new Date()));
    }

    @RequestMapping(value = "view-periods/{facilityId}/{programId}", method = RequestMethod.GET)
   //TODO// @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
        return response("periods", service.getReportedPeriodsFor(facilityId, programId));
    }



    @RequestMapping(value = "initialize/{periodId}/{programId}/{facilityId}")
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initialize(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ){
        return response("report", service.initialize(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "initializeEmergency/{periodId}/{programId}/{facilityId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initializeEmergency(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ){
        return response("report", service.initializeEmergency(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "submit")
   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> submit(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        service.submit(orderRequisition, loggedInUserId(request));
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "save")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "lastReport/{facilityId}/{programId}", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse>
    getLastReport(@PathVariable  Long facilityId,@PathVariable Long programId,HttpServletRequest request){

        return response("lastReport", service.getLastReport(facilityId, programId));
    }

    @RequestMapping(value = "get/{id}.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id, HttpServletRequest request){
        return response("report", service.getAllDetailsById(id));
    }

    @RequestMapping(value = "userHomeFacility.json", method = RequestMethod.GET)
        public ResponseEntity<OpenLmisResponse> getUserHomeFacilities(HttpServletRequest request){
            return  response("homeFacility", facilityService.getHomeFacility(loggedInUserId(request)));
        }

    @RequestMapping(value = "getPendingRequest/{facilityId}/{programId}", method = RequestMethod.GET,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getPendingRequest(@PathVariable  Long facilityId,@PathVariable Long programId,HttpServletRequest request){

        return response("pendingRequest", service.getPendingRequest(loggedInUserId(request), facilityId, programId));
    }

    @RequestMapping(value = "getAllBy/{programId}/{periodId}/{facilityId}", method = RequestMethod.GET,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAllBy(@PathVariable Long programId ,@PathVariable Long periodId,@PathVariable  Long facilityId,HttpServletRequest request){
        return response("requisitionList", service.getAllBy(programId, periodId, facilityId));
    }


    @RequestMapping(value = "updateOrderRequest/{orderId}", method = RequestMethod.PUT,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>
    updateORStatus(@PathVariable  Long orderId,HttpServletRequest request){
    try{
        service.updateORStatus(orderId);
        return success("Saved Successifully");

    } catch (DataException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    }

    @RequestMapping(value = "updateOrderRequisition/{orderId}", method = RequestMethod.PUT,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> update(@PathVariable  Long orderId,@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        orderRequisition.setId(orderId);
        orderRequisition.setStatus(VaccineOrderStatus.ISSUED);
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "programs.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return response("programs", programService.getAllIvdPrograms());
    }



    @RequestMapping(value = "loggedInUserDetails.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getLoggedInUserProfiles(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        User user = userService.getById(userId);
        return response("userDetails", user);
    }

    @RequestMapping(value = "order-requisition/programs.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getProgramFormHomeFacility(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        User user = userService.getById(userId);
        return response("programs", programService.getProgramsSupportedByUserHomeFacilityWithRights(user.getFacilityId(), userId, "CREATE_REQUISITION", "AUTHORIZE_REQUISITION"));
    }


    @RequestMapping(value = "/{programId}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = programProductService.getByProgram(new Program(programId));
        return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

    @RequestMapping(value = "{id}/print", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printOrder(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
        Template orPrintTemplate = templateService.getByName(PRINT_ORDER_REQUISITION);

        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");

        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);
        map.put(Constants.OPERATOR_NAME, configuration.getValue());

        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("ORDER_ID", id.intValue());

        return new ModelAndView(jasperView, map);
    }


    @RequestMapping(value = "issue/print/{id}", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printIssueStock(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
        Template orPrintTemplate = templateService.getByName(PRINT_ISSUE_STOCK);
   JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");

        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);
        map.put(Constants.OPERATOR_NAME, configuration.getValue());

        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("ISSUE_ID", id.intValue());

        return new ModelAndView(jasperView, map);
    }


    @RequestMapping(value = "search", method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> searchUser(@RequestParam(value = "facilityId", required = false) Long facilityId,
                                                       @RequestParam(value = "dateRangeStart", required = false) String dateRangeStart,
                                                       @RequestParam(value = "dateRangeEnd", required = false) String dateRangeEnd,
                                                       @RequestParam(value = "programId", required = false) Long programId,

     HttpServletRequest request
    ) {
        return response(ORDER_REQUISITION_SEARCH, service.getAllSearchBy(facilityId,dateRangeStart,dateRangeEnd,programId));

    }

    @RequestMapping(value = "facilities/{facilityId}/programs/{programId}/stockCards", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getStockCards(@PathVariable Long facilityId,
                                        @PathVariable Long programId,
                                        @RequestParam(value = "entries", defaultValue = "1")Integer entries,
                                        @RequestParam(value = "countOnly", defaultValue = "false")Boolean countOnly)
    {

        List<OrderRequisitionStockCardDTO> stockCards = service.getStockCards(facilityId, programId);
        return OpenLmisResponse.response("stockCards", stockCards);
    }



}
