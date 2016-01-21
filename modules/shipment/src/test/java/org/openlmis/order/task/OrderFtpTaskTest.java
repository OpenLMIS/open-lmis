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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.order.service.OrderService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.openlmis.order.domain.OrderStatus.TRANSFER_FAILED;
import static org.openlmis.order.task.OrderFtpTask.FTP_CREDENTIAL_MISSING_COMMENT;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderFtpTask.class, ConfigurationSettingService.class})
public class OrderFtpTaskTest {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Mock
  ConfigurationSettingService configurationSettingService;

  @Mock
  OrderService orderService;

  @Mock
  private SupplyLineService supplyLineService;

  @Mock
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @Mock
  private OrderCsvHelper orderCsvHelper;

  @Mock
  private OrderFtpSender ftpSender;

  @Mock
  String localFileDirectory = "test";

  @InjectMocks
  private OrderFtpTask orderFtpTask;

  private Order fullOrder;
  private List<Order> orderList;
  private Order order;

  @Before
  public void setUp() throws Exception {
    fullOrder = mock(Order.class);
    order = new Order(1l);
    orderList = asList(order);
  }

  @Test
  public void shouldProcessOrders() throws Exception {

    SupplyLine supplyLine = mock(SupplyLine.class);
    when(configurationSettingService.getBoolValue("USE_FTP_TO_SEND_ORDERS")).thenReturn(true);
    when(configurationSettingService.getConfigurationStringValue("LOCAL_ORDER_EXPORT_DIRECTORY")).thenReturn("./local-order-ftp-data");
    when(orderService.getOrder(order.getId())).thenReturn(fullOrder);
    when(fullOrder.getId()).thenReturn(1l);
    when(fullOrder.getSupplyLine()).thenReturn(supplyLine);
    Long supplyLineId = 1l;
    when(supplyLine.getId()).thenReturn(supplyLineId);
    when(supplyLineService.getById(supplyLineId)).thenReturn(supplyLine);
    Facility facility = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(facility);
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    when(facilityFtpDetailsService.getByFacilityId(facility)).thenReturn(facilityFtpDetails);
    OrderFileTemplateDTO orderFileTemplateDTO = new OrderFileTemplateDTO();
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderFileTemplateDTO.setOrderConfiguration(orderConfiguration);
    orderConfiguration.setFilePrefix("Order");
    when(orderService.getOrderFileTemplateDTO()).thenReturn(orderFileTemplateDTO);
    File file = mock(File.class);
    whenNew(File.class).withArguments(localFileDirectory).thenReturn(file);
    whenNew(File.class).withArguments(localFileDirectory + System.getProperty("file.separator") + "Order1.csv").thenReturn(file);
    FileWriter fileWriter = mock(FileWriter.class);
    whenNew(FileWriter.class).withArguments(file).thenReturn(fileWriter);

    orderFtpTask.processOrder(orderList);

    verify(orderCsvHelper).writeCsvFile(fullOrder, orderFileTemplateDTO, fileWriter);
    verify(fileWriter).flush();
    verify(ftpSender).sendFile(facilityFtpDetails, file);
  }

  @Test
  public void shouldUpdateOrderAsTransferFailedIfFacilityFtpDetailsDoesNotExist() {
    when(configurationSettingService.getBoolValue("USE_FTP_TO_SEND_ORDERS")).thenReturn(true);
    when(configurationSettingService.getConfigurationStringValue("LOCAL_ORDER_EXPORT_DIRECTORY")).thenReturn("./local-order-ftp-data");

    SupplyLine supplyLine = mock(SupplyLine.class);
    when(orderService.getOrder(order.getId())).thenReturn(fullOrder);
    when(fullOrder.getSupplyLine()).thenReturn(supplyLine);
    Long supplyLineId = 1l;
    when(supplyLine.getId()).thenReturn(supplyLineId);
    when(supplyLineService.getById(supplyLineId)).thenReturn(supplyLine);
    Facility facility = new Facility();
    when(supplyLine.getSupplyingFacility()).thenReturn(facility);
    when(facilityFtpDetailsService.getByFacilityId(facility)).thenReturn(null);

    orderFtpTask.processOrder(orderList);

    verify(fullOrder).setStatus(TRANSFER_FAILED);
    verify(fullOrder).setFtpComment(FTP_CREDENTIAL_MISSING_COMMENT);
    verify(orderService).updateOrderStatus(fullOrder);
  }
}
