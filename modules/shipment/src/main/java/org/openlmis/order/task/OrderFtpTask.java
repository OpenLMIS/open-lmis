/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.openlmis.order.task;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openlmis.order.domain.OrderStatus.RELEASED;
import static org.openlmis.order.domain.OrderStatus.TRANSFER_FAILED;

@Component
public class OrderFtpTask {

  @Autowired
  private SupplyLineService supplyLineService;

  @Autowired
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderCsvHelper orderCsvHelper;

  @Autowired
  private OrderFtpSender ftpSender;

  @Value("${ftp.order.local.directory}")
  String localFileDirectory;

  private static Logger logger = Logger.getLogger(OrderFtpTask.class);

  private static String CONNECTION_REFUSED = "Connection refused";
  private static String CONNECTION_REFUSED_TIMEOUT = "connect timed out";
  private static String LOGIN_INCORRECT = "Login incorrect";
  private static String PERMISSION_DENIED = "Error writing file";
  private static String CONNECTION_REFUSED_COMMENT = "order.ftpComment.connection.refused";
  private static String LOGIN_INCORRECT_COMMENT = "order.ftpComment.incorrect.login";
  private static String PERMISSION_DENIED_COMMENT = "order.ftpComment.permission.denied";
  public static String FTP_CREDENTIAL_MISSING_COMMENT = "order.ftpComment.ftpcredential.missing";

  @ServiceActivator(inputChannel = "orderInputChannel")
  public void processOrder(List<Order> orders) {
    for (Order order : orders) {
      order = orderService.getOrderForDownload(order.getId());

      SupplyLine supplyLine = order.getSupplyLine();

      supplyLine = supplyLineService.getById(supplyLine.getId());
      FacilityFtpDetails supplyingFacilityFtpDetails = facilityFtpDetailsService.getByFacilityId(supplyLine.getSupplyingFacility());
      if (supplyingFacilityFtpDetails == null) {
        updateOrder(order, TRANSFER_FAILED, FTP_CREDENTIAL_MISSING_COMMENT);
        continue;
      }

      OrderFileTemplateDTO orderFileTemplateDTO = orderService.getOrderFileTemplateDTO();
      String fileName = orderFileTemplateDTO.getOrderConfiguration().getFilePrefix() + order.getId() + ".csv";
      File localDirectory = new File(localFileDirectory);
      if (!localDirectory.exists()) {
        localDirectory.mkdir();
      }
      File file = new File(localFileDirectory + System.getProperty("file.separator") + fileName);
      try (FileWriter fileWriter = new FileWriter(file)) {
        orderCsvHelper.writeCsvFile(order, orderFileTemplateDTO, fileWriter);
        fileWriter.flush();
        ftpSender.sendFile(supplyingFacilityFtpDetails, file);
        updateOrder(order, RELEASED, null);
      } catch (FailedToCreateProducerException producerException) {
        updateOrder(order, TRANSFER_FAILED, CONNECTION_REFUSED_COMMENT);
      } catch (CamelExecutionException camelException) {
        handleException(camelException, order);
      } catch (Exception e) {
        logger.error("Error in ftp of order file", e);
        updateOrder(order, TRANSFER_FAILED, null);
      } finally {
        file.delete();
      }
    }
  }

  private void handleException(CamelExecutionException camelException, Order order) {
    logger.error("Error in ftp of order file", camelException);
    if (!(updateOrderForException(CONNECTION_REFUSED_TIMEOUT, camelException, order, CONNECTION_REFUSED_COMMENT) ||
      updateOrderForException(CONNECTION_REFUSED, camelException, order, CONNECTION_REFUSED_COMMENT) ||
      updateOrderForException(LOGIN_INCORRECT, camelException, order, LOGIN_INCORRECT_COMMENT) ||
      updateOrderForException(PERMISSION_DENIED, camelException, order, PERMISSION_DENIED_COMMENT))) {
      order.setStatus(TRANSFER_FAILED);
      order.setFtpComment(null);
    }
    orderService.updateOrderStatus(order);
  }

  private boolean updateOrderForException(String error, Exception exception, Order order, String ftpComment) {
    Pattern pattern = Pattern.compile(error);
    Matcher matcher = pattern.matcher(exception.getCause().getMessage());
    if (matcher.find()) {
      order.setStatus(TRANSFER_FAILED);
      order.setFtpComment(ftpComment);
      return true;
    }
    return false;
  }

  private void updateOrder(Order order, OrderStatus status, String ftpComment) {
    order.setStatus(status);
    order.setFtpComment(ftpComment);
    orderService.updateOrderStatus(order);
  }

}
