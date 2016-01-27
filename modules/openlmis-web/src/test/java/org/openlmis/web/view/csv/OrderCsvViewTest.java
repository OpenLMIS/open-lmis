/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.helper.OrderCsvHelper;
import org.openlmis.web.controller.OrderController;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(OrderCsvView.class)
public class OrderCsvViewTest {

  @Mock
  OrderCsvHelper csvHelper;

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
    initMocks(this);
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