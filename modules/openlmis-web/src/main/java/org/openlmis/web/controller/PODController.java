/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.dto.OrderPODDTO;
import org.openlmis.pod.service.PODService;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.openlmis.core.web.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to create, submit, update, print, get details for a POD(proof od delivery).
 */

@Controller
public class PODController extends BaseController {

  public static final String ORDER_POD = "orderPOD";
  public static final String ORDER = "order";
  public static final String RECEIVED_DATE = "receivedDate";
  public static final String PRINT_POD = "Print POD";

  @Autowired
  private PODService service;

  @Autowired
  private OrderService orderService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private JasperReportsViewFactory jasperReportsViewFactory;

  @RequestMapping(value = "/pods", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> createPOD(@RequestParam Long orderId,
                                                    HttpServletRequest request) throws ParseException {
    Order order = orderService.getOrder(orderId);
    OrderPODDTO orderPODDTO = OrderPODDTO.getOrderDetailsForPOD(order);
    OrderPOD existingPOD = service.getPODByOrderId(orderId);
    ResponseEntity<OpenLmisResponse> response;
    if (existingPOD != null) {
      response = response(ORDER_POD, existingPOD);
    } else {
      OrderPOD orderPOD = new OrderPOD(orderId, order.getOrderNumber(), loggedInUserId(request));
      OrderPOD createdPOD = service.createPOD(orderPOD);
      response = response(ORDER_POD, createdPOD, HttpStatus.CREATED);
    }
    response.getBody().addData(ORDER, orderPODDTO);
    return response;
  }

  @RequestMapping(value = "/pods/{id}", method = GET, headers = ACCEPT_JSON)
  @PostAuthorize("@fulfillmentPermissionService.hasPermission(principal, returnObject.body.data.get(\"order\").getId(), 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> getPOD(@PathVariable("id") Long podId) throws ParseException {
    OrderPOD orderPOD = service.getPodById(podId);
    OrderPODDTO orderPODDTO = OrderPODDTO.getOrderDetailsForPOD(orderService.getOrder(orderPOD.getOrderId()));
    ResponseEntity<OpenLmisResponse> response = response(ORDER_POD, orderPOD);
    response.getBody().addData(ORDER, orderPODDTO);
    response.getBody().addData(RECEIVED_DATE, orderPOD.getStringReceivedDate());
    return response;
  }

  @RequestMapping(value = "/pods/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody OrderPOD orderPOD, @PathVariable Long id, HttpServletRequest request) throws ParseException {
    try {
      orderPOD.setModifiedBy(loggedInUserId(request));
      orderPOD.setId(id);
      service.save(orderPOD);
      return success("msg.pod.save.success");
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/pods/submit/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> submit(@PathVariable Long id, HttpServletRequest request) {
    try {
      service.submit(id, loggedInUserId(request));
      return success("msg.pod.submit.success");
    } catch (DataException exception) {
      return error(exception, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/pods/{id}/print", method = GET, headers = ACCEPT_JSON)
  public ModelAndView print(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
    Template podPrintTemplate = templateService.getByName(PRINT_POD);
    JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(podPrintTemplate);
    Map<String, Object> map = new HashMap<>();
    map.put("format", "pdf");

    Locale currentLocale = messageService.getCurrentLocale();
    map.put(JRParameter.REPORT_LOCALE, currentLocale);
    ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
    map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);

    Resource reportResource = new ClassPathResource("reports");
    Resource imgResource = new ClassPathResource("images");

    String separator = System.getProperty("file.separator");
    map.put("subreport_dir", reportResource.getFile().getAbsolutePath() + separator);
    map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
    map.put("pod_id", id.intValue());
    return new ModelAndView(jasperView, map);
  }
}
