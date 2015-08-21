/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


package org.openlmis.order.task;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openlmis.order.domain.OrderStatus.RELEASED;
import static org.openlmis.order.domain.OrderStatus.TRANSFER_FAILED;

/**
 * Exposes the services for processing orders, sending their order files to respective ftp locations.
 */

@Component
public class OrderFtpTask {

  @Autowired
  private ConfigurationSettingService configurationSettingService;

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

  @Value("${order.ftp.local.directory}")
  String localFileDirectory;

  Boolean sendFtp;

  private void initiateSettings(){
    sendFtp = configurationSettingService.getBoolValue("USE_FTP_TO_SEND_ORDERS");
    localFileDirectory = configurationSettingService.getConfigurationStringValue("LOCAL_ORDER_EXPORT_DIRECTORY");
  }

  private static Logger logger = Logger.getLogger(OrderFtpTask.class);

  private static String CONNECTION_REFUSED = "Connection refused";
  private static String CONNECTION_REFUSED_TIMEOUT = "connect timed out";
  private static String LOGIN_INCORRECT = "Login incorrect";
  private static String PERMISSION_DENIED = "Error writing file";
  private static String CONNECTION_REFUSED_COMMENT = "order.ftpComment.connection.refused";
  private static String LOGIN_INCORRECT_COMMENT = "order.ftpComment.incorrect.login";
  private static String PERMISSION_DENIED_COMMENT = "order.ftpComment.permission.denied";
  public static String FTP_CREDENTIAL_MISSING_COMMENT = "order.ftpComment.ftpcredential.missing";

  public void processOrder(@Payload List<Order> orders) {

    initiateSettings();

    for (Order order : orders) {
      order = orderService.getOrder(order.getId());

      SupplyLine supplyLine = order.getSupplyLine();

      supplyLine = supplyLineService.getById(supplyLine.getId());
      FacilityFtpDetails supplyingFacilityFtpDetails = facilityFtpDetailsService.getByFacilityId(supplyLine.getSupplyingFacility());
      if (sendFtp && supplyingFacilityFtpDetails == null) {
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
        if(sendFtp) {
          ftpSender.sendFile(supplyingFacilityFtpDetails, file);
        }
        updateOrder(order, RELEASED, null);
      } catch (FailedToCreateProducerException producerException) {
        updateOrder(order, TRANSFER_FAILED, CONNECTION_REFUSED_COMMENT);
      } catch (CamelExecutionException camelException) {
        handleException(camelException, order);
      } catch (Exception e) {
        logger.error("Error in ftp of order file", e);
        updateOrder(order, TRANSFER_FAILED, null);
      } finally {
        if(sendFtp) {
          file.delete();
        }
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
