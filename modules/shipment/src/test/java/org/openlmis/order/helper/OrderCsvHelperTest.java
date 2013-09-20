///*
// * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// *
// * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
// */
//
//
//package org.openlmis.order.helper;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.openlmis.core.domain.Facility;
//import org.openlmis.core.domain.OrderConfiguration;
//import org.openlmis.core.domain.ProcessingPeriod;
//import org.openlmis.order.domain.Order;
//import org.openlmis.order.domain.OrderFileColumn;
//import org.openlmis.order.dto.OrderFileTemplateDTO;
//import org.openlmis.rnr.domain.Rnr;
//import org.openlmis.rnr.domain.RnrLineItem;
//import org.openlmis.shipment.builder.OrderFileColumnBuilder;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static com.natpryce.makeiteasy.MakeItEasy.*;
//import static java.lang.String.valueOf;
//import static org.apache.commons.lang3.time.DateUtils.parseDate;
//import static org.joda.time.format.DateTimeFormat.forPattern;
//import static org.mockito.Mockito.*;
//import static org.openlmis.shipment.builder.OrderFileColumnBuilder.*;
//import static org.powermock.api.mockito.PowerMockito.mock;
//
//public class OrderCsvHelperTest {
//
//
//  OrderCsvHelper csvHelper = new OrderCsvHelper();
//
//  public static final String FILE_PREFIX = "O_";
//  private Date createdDate;
//  final String columnLabel = "label";
//  String facilityCode = "F10";
//  Date periodStartDate = new Date(234123123l);
//  int quantityApproved = 67;
//  Long orderId = 2L;
//  int quantityApprovedForNonFullSupply = 567;
//  Order order;
//  PrintWriter writer;
//  OrderFileTemplateDTO orderFileTemplate;
//
//  @Before
//  public void setUp() throws Exception {
//    createdDate = parseDate("22/12/13", "dd/MM/yy");
//    Facility facility = new Facility();
//    facility.setCode(facilityCode);
//    Rnr rnr = new Rnr();
//    ProcessingPeriod period = new ProcessingPeriod();
//    period.setStartDate(periodStartDate);
//    rnr.setPeriod(period);
//    rnr.setFacility(facility);
//    List<RnrLineItem> fullSupplyLineItems = new ArrayList<>();
//    RnrLineItem rnrLineItem = new RnrLineItem();
//    rnrLineItem.setQuantityApproved(quantityApproved);
//    fullSupplyLineItems.add(rnrLineItem);
//    rnr.setFullSupplyLineItems(fullSupplyLineItems);
//    List<RnrLineItem> nonFullSupplyLineItems = new ArrayList<>();
//    RnrLineItem nonFullLineItem = new RnrLineItem();
//    nonFullLineItem.setQuantityApproved(quantityApprovedForNonFullSupply);
//    nonFullSupplyLineItems.add(nonFullLineItem);
//    rnr.setNonFullSupplyLineItems(nonFullSupplyLineItems);
//    order = new Order(orderId);
//    order.setCreatedDate(createdDate);
//    order.setRnr(rnr);
//    writer = mock(PrintWriter.class);
//    orderFileTemplate = getOrderFileTemplate();
//  }
//
//  private void verifyHeaderWritten() throws IOException {
//    verify(writer).write(columnLabel);
//    verify(writer).write("otherLabel");
//    verify(writer).write("NA");
//  }
//
//  @Test
//  public void shouldSetHeadersInCSVFileReadingFromOrderTemplateIfHeadersIncluded() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//    verifyHeaderWritten();
//  }
//
//  @Test
//  public void shouldNotSetHeadersInCSVFileIfHeadersNotIncluded() throws Exception {
//    orderFileTemplate.getOrderConfiguration().setHeaderInFile(false);
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer, never()).write(columnLabel);
//    verify(writer, never()).write("otherLabel");
//    verify(writer, atLeastOnce()).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldAddNewLineAfterHeader() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//    verifyHeaderWritten();
//    verify(writer, atLeastOnce()).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplate() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer, times(2)).write(orderId.toString());
//    verify(writer, times(2)).write("22/12/13");
//    verify(writer, atLeast(2)).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplateFromRnrLineItem() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer).write(valueOf(quantityApproved));
//    verify(writer, atLeast(2)).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldWriteValuesToCSVBasedOnFieldOrderInTemplateForFacilityCode() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer, times(2)).write(valueOf(facilityCode));
//    verify(writer, atLeast(3)).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldWriteValuesToCSVForNonFullSupplyLineItems() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer).write(valueOf(quantityApprovedForNonFullSupply));
//    verify(writer, atLeast(2)).write(System.getProperty("line.separator"));
//  }
//
//  @Test
//  public void shouldNotWriteColumnValuesForExcludedColumns() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer, never()).write(periodStartDate.toString());
//  }
//
//
//  @Test
//  public void shouldNotWriteColumnHeaderValuesForExcludedColumns() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//
//    verify(writer, never()).write("periodStartDate");
//  }
//
//  @Test
//  public void shouldWriteOrderCreatedDateInCorrectFormat() throws Exception {
//    csvHelper.writeCsvFile(order, orderFileTemplate, writer);
//    String formattedDate = forPattern("dd/MM/yy").print(createdDate.getTime());
//
//    verify(writer, times(2)).write(formattedDate);
//  }
//
//
//  private OrderFileTemplateDTO getOrderFileTemplate() {
//    OrderConfiguration orderConfiguration = new OrderConfiguration();
//    orderConfiguration.setHeaderInFile(true);
//    final String nullString = null;
//    orderConfiguration.setFilePrefix(FILE_PREFIX);
//    List<OrderFileColumn> orderFileColumns = new ArrayList<OrderFileColumn>() {{
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, columnLabel), with(keyPath, "id"), with(nested, "order"))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "otherLabel"), with(keyPath, "createdDate"), with(nested, "order"), with(format, "dd/MM/yy"))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "lineItemColumn"), with(keyPath, "quantityApproved"), with(nested, "lineItem"))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "facilityCode"), with(keyPath, "rnr/facility/code"), with(nested, "order"))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "periodStartDate"), with(format, "MM/yy"),
//        with(keyPath, "rnr/period/startDate"), with(nested, "order"), with(includeInOrderFile, false))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, "NA"), with(nested, ""))));
//      add(make(a(OrderFileColumnBuilder.defaultColumn, with(OrderFileColumnBuilder.columnLabel, nullString), with(nested, ""))));
//    }};
//    return new OrderFileTemplateDTO(orderConfiguration, orderFileColumns);
//  }
//
//}
