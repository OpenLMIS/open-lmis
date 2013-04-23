package org.openlmis.web.view.csv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.OrderController;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenLmisCsvView.class)
public class OpenLmisCsvViewTest {


  @Test
  public void shouldGenerateCsvForGivenOrder() throws Exception {

    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    Map<String, Object> model = new HashMap<>();
    Order order = mock(Order.class);
    PrintWriter writer = mock(PrintWriter.class);
    BufferedWriter bufferedWriter = mock(BufferedWriter.class);
    Rnr rnr = mock(Rnr.class);
    List<RnrLineItem> rnrLineItems = new ArrayList<>();
    RnrLineItem rnrLineItem = mock(RnrLineItem.class);
    rnrLineItems.add(rnrLineItem);
    Facility facility = mock(Facility.class);
    Date today = new Date();

    when(response.getWriter()).thenReturn(writer);
    when(rnr.getFullSupplyLineItems()).thenReturn(rnrLineItems);
    when(rnr.getNonFullSupplyLineItems()).thenReturn(rnrLineItems);
    when(rnr.getFacility()).thenReturn(facility);
    when(order.getRnr()).thenReturn(rnr);
    whenNew(BufferedWriter.class).withArguments(writer).thenReturn(bufferedWriter);
    whenNew(Date.class).withNoArguments().thenReturn(today);
    model.put(OrderController.ORDER, order);
    String fileName = "O" + today + ".csv";

    OpenLmisCsvView csvView = new OpenLmisCsvView();

    csvView.renderMergedOutputModel(model, request, response);

    verify(response).setHeader("Content-Disposition", "attachment; filename=" + fileName);
    verify(rnrLineItem, times(2)).getProductCode();
    verify(rnrLineItem, times(2)).getPacksToShip();
    verify(rnrLineItem, times(2)).getPackSize();
    verify(facility, times(2)).getCode();
    verify(bufferedWriter, times(2)).newLine();
    verify(bufferedWriter, times(2)).write(anyString());
    verify(rnr, times(2)).getId();
  }

}
