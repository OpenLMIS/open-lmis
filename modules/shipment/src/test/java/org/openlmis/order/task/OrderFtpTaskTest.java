package org.openlmis.order.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.order.service.OrderService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.openlmis.order.domain.OrderStatus.TRANSFER_FAILED;
import static org.openlmis.order.task.OrderFtpTask.FTP_CREDENTIAL_MISSING_COMMENT;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderFtpTask.class)
public class OrderFtpTaskTest {

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
    orderList = new ArrayList();
    orderList.add(order);

  }

  @Test
  public void shouldProcessOrders() throws Exception {
    SupplyLine supplyLine = mock(SupplyLine.class);
    when(orderService.getOrderForDownload(order.getId())).thenReturn(fullOrder);
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
    File file  = mock(File.class);
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
    SupplyLine supplyLine = mock(SupplyLine.class);
    when(orderService.getOrderForDownload(order.getId())).thenReturn(fullOrder);
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
