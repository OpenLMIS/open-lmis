package org.openlmis.web.view.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.web.controller.OrderController;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderCsvView.class)
public class OrderCsvViewTest {


  @Mock
  OrderCsvHelper csvHelper;

  @Mock
  MessageService messageService;

  @InjectMocks
  OrderCsvView csvView;

  BufferedWriter bufferedWriter;
  PrintWriter writer;
  HttpServletResponse response;
  HttpServletRequest request;
  Map<String, Object> model;

  public static final String FILE_PREFIX = "O_";
  Long orderId = 2L;
  private Order order;
  private OrderFileTemplateDTO orderFileTemplateDTO;

  @Before
  public void setUp() throws Exception {
    response = mock(HttpServletResponse.class);
    request = mock(HttpServletRequest.class);
    model = new HashMap<>();
    order = new Order(orderId);
    orderFileTemplateDTO = new OrderFileTemplateDTO();
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfiguration.setFilePrefix(FILE_PREFIX);
    orderFileTemplateDTO.setOrderConfiguration(orderConfiguration);
    model.put(OrderController.ORDER_FILE_TEMPLATE, orderFileTemplateDTO);
    model.put(OrderController.ORDER, order);

    writer = mock(PrintWriter.class);
    bufferedWriter = mock(BufferedWriter.class);

    when(response.getWriter()).thenReturn(writer);
    whenNew(BufferedWriter.class).withArguments(writer).thenReturn(bufferedWriter);

  }

  @Test
  public void shouldSetResponseInHeader() throws Exception {

    csvView.renderMergedOutputModel(model, request, response);

    String expectedFileName = FILE_PREFIX + orderId + ".csv";
    verify(response).setHeader("Content-Disposition", "attachment; filename=" + expectedFileName);
  }

  @Test
  public void shouldCreateCsvFile() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(csvHelper).writeCsvFile(order, orderFileTemplateDTO, bufferedWriter);

  }
}




