package org.openlmis.web.view.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.shipment.builder.OrderFileColumnBuilder;
import org.openlmis.web.controller.OrderController;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.time.DateUtils.parseDate;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.openlmis.shipment.builder.OrderFileColumnBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderCsvView.class)
public class OrderCsvViewTest {

  public static final String FILE_PREFIX = "O_";
  @Mock
  MessageService messageService;

  BufferedWriter bufferedWriter;
  HttpServletResponse response;
  PrintWriter writer;
  HttpServletRequest request;
  Map<String, Object> model;
  Order order;
  OrderFileTemplateDTO orderFileTemplate;
  private OrderCsvView csvView;

  final String columnLabel = "label";
  Long orderId = 2L;
  String facilityCode = "F10";
  int quantityApproved = 67;
  int quantityApprovedForNonFullSupply = 567;
  Date createdDate;
  Date periodStartDate = new Date(234123123l);

  @Before
  public void setUp() throws Exception {
    createdDate = parseDate("22/12/13", "dd/MM/yy");
    response = mock(HttpServletResponse.class);
    request = mock(HttpServletRequest.class);
    model = new HashMap<>();
    Facility facility = new Facility();
    facility.setCode(facilityCode);
    Rnr rnr = new Rnr();
    ProcessingPeriod period = new ProcessingPeriod();
    period.setStartDate(periodStartDate);
    rnr.setPeriod(period);
    rnr.setFacility(facility);
    List<RnrLineItem> fullSupplyLineItems = new ArrayList<>();
    RnrLineItem rnrLineItem = new RnrLineItem();
    rnrLineItem.setQuantityApproved(quantityApproved);
    fullSupplyLineItems.add(rnrLineItem);
    rnr.setFullSupplyLineItems(fullSupplyLineItems);
    List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
    RnrLineItem nonFullLineItem = new RnrLineItem();
    nonFullLineItem.setQuantityApproved(quantityApprovedForNonFullSupply);
    nonFullSupplyLineItems.add(nonFullLineItem);
    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
    order = new Order(orderId);
    order.setCreatedDate(createdDate);
    order.setRnr(rnr);
    writer = mock(PrintWriter.class);
    bufferedWriter = mock(BufferedWriter.class);
    model.put(OrderController.ORDER, order);
    orderFileTemplate = getOrderFileTemplate();
    model.put(OrderController.ORDER_FILE_TEMPLATE, orderFileTemplate);

    csvView = new OrderCsvView(messageService);

    String nullString = null;
    when(response.getWriter()).thenReturn(writer);

    doThrow(new IOException()).when(bufferedWriter).write(nullString);
    whenNew(BufferedWriter.class).withArguments(writer).thenReturn(bufferedWriter);
  }

  private OrderFileTemplateDTO getOrderFileTemplate() {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfiguration.setHeaderInFile(true);
    final String nullString = null;
    orderConfiguration.setFilePrefix(FILE_PREFIX);
    List<OrderFileColumn> orderFileColumns = new ArrayList<OrderFileColumn>() {{
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, columnLabel), with(keyPath, "id"), with(nested, "order"))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "otherLabel"), with(keyPath, "createdDate"), with(nested, "order"), with(format, "dd/MM/yy"))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "lineItemColumn"), with(keyPath, "quantityApproved"), with(nested, "lineItem"))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "facilityCode"), with(keyPath, "rnr/facility/code"), with(nested, "order"))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "periodStartDate"), with(format, "MM/yy"),
        with(keyPath, "rnr/period/startDate"), with(nested, "order"), with(includeInOrderFile, false))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "NA"), with(nested, ""))));
      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, nullString), with(nested, ""))));
    }};
    return new OrderFileTemplateDTO(orderConfiguration, orderFileColumns);
  }

  @Test
  public void shouldSetHeadersInCSVFileReadingFromOrderTemplateIfHeadersIncluded() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);
    verifyHeaderWritten();
  }

  private void verifyHeaderWritten() throws IOException {
    verify(bufferedWriter).write(columnLabel);
    verify(bufferedWriter).write("otherLabel");
    verify(bufferedWriter).write("NA");
  }

  @Test
  public void shouldAddNewLineAfterHeader() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);
    verifyHeaderWritten();
    verify(bufferedWriter, atLeastOnce()).newLine();
  }

  @Test
  public void shouldNotSetHeadersInCSVFileIfHeadersNotIncluded() throws Exception {
    orderFileTemplate.getOrderConfiguration().setHeaderInFile(false);
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter, never()).write(columnLabel);
    verify(bufferedWriter, never()).write("otherLabel");
    verify(bufferedWriter, atLeastOnce()).newLine();
  }

  @Test
  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplate() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter, times(2)).write(orderId.toString());
    verify(bufferedWriter, times(2)).write("22/12/13");
    verify(bufferedWriter, atLeast(2)).newLine();
  }

  @Test
  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplateFromRnrLineItem() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter).write(valueOf(quantityApproved));
    verify(bufferedWriter, atLeast(2)).newLine();
  }

  @Test
  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplateForFacilityCode() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter, times(2)).write(valueOf(facilityCode));
    verify(bufferedWriter, atLeast(3)).newLine();
  }

  @Test
  public void shouldWriteValuesToCSVForNonFullSupplyLineItems() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter).write(valueOf(quantityApprovedForNonFullSupply));
    verify(bufferedWriter, atLeast(2)).newLine();
  }

  @Test
  public void shouldNotWriteColumnValuesForExcludedColumns() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter, never()).write(periodStartDate.toString());
  }

  @Test
  public void shouldNotWriteColumnHeaderValuesForExcludedColumns() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    verify(bufferedWriter, never()).write("periodStartDate");
  }

  @Test
  public void shouldWriteOrderCreatedDateInCorrectFormat() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);
    String formattedDate = forPattern("dd/MM/yy").print(createdDate.getTime());

    verify(bufferedWriter, times(2)).write(formattedDate);
  }

  @Test
  public void shouldAddFileNamePrefixFromConfiguration() throws Exception {
    csvView.renderMergedOutputModel(model, request, response);

    String expectedFileName = FILE_PREFIX + orderId + ".csv";
    verify(response).setHeader("Content-Disposition", "attachment; filename=" + expectedFileName);
  }

}
