/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;

  @Mock
  private ProductService productService;

  @Mock
  private RequisitionService requisitionService;

  @InjectMocks
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldSaveShipmentLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(requisitionService.getNonSkippedLineItem(1L, "P10")).thenReturn(new RnrLineItem());
    shipmentService.save(shipmentLineItem);

    verify(shipmentRepository).save(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfProductCodeIsNotValid() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(productService.getIdForCode("P10")).thenReturn(null);
    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.save(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfQuantityNegative() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, -1)));

    when(productService.getIdForCode("P10")).thenReturn(1l);
    exException.expect(DataException.class);
    exException.expectMessage("error.negative.shipped.quantity");

    shipmentService.save(shipmentLineItem);
  }

  @Test
  public void shouldFillProductInfoFromRequisitionIfLineItemExists() throws Exception {
    ShipmentLineItem shipmentLineItem = spy(make(a(defaultShipmentLineItem, with(productCode, "P10"), with(orderId, 1L),
      with(quantityShipped, 20))));

    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    when(requisitionService.getNonSkippedLineItem(shipmentLineItem.getOrderId(), "P10")).thenReturn(lineItem);

    shipmentService.save(shipmentLineItem);

    verify(shipmentLineItem).fillReferenceFields(lineItem);
  }

  @Test
  public void shouldFillProductInfoFromProductsIfLineItemDoesNotExist() throws Exception {
    ShipmentLineItem shipmentLineItem = spy(make(a(defaultShipmentLineItem, with(productCode, "P10"), with(orderId, 1L),
      with(quantityShipped, 20))));

    when(requisitionService.getNonSkippedLineItem(shipmentLineItem.getOrderId(), "P10")).thenReturn(null);
    Product product = make(a(defaultProduct));
    when(productService.getByCode("P10")).thenReturn(product);
    when(productService.getByCode("P133")).thenReturn(product);

    shipmentService.save(shipmentLineItem);

    verify(shipmentLineItem).fillReferenceFields(product);
    verify(shipmentRepository).save(shipmentLineItem);
  }

  @Test
  public void shouldThrowExceptionIfInvalidProductCode() throws Exception {
    ShipmentLineItem shipmentLineItem = spy(make(a(defaultShipmentLineItem, with(productCode, "P10"), with(orderId, 1L),
      with(quantityShipped, 20))));

    when(requisitionService.getNonSkippedLineItem(shipmentLineItem.getOrderId(), "P10")).thenReturn(null);
    when(productService.getByCode("P10")).thenReturn(null);

    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.save(shipmentLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

  @Test
  public void shouldGetAllShipmentLineItemsForAnOrder() throws Exception {
    Long orderId = 12345L;

    List<ShipmentLineItem> expectedLineItems = asList(new ShipmentLineItem());
    when(shipmentRepository.getLineItems(orderId)).thenReturn(expectedLineItems);

    List<ShipmentLineItem> lineItems = shipmentService.getLineItems(orderId);

    assertThat(lineItems, is(expectedLineItems));
  }

  @Test
  public void shouldThrowExceptionIfInvalidReplacedProductCode() throws Exception {
    ShipmentLineItem shipmentLineItem = spy(make(a(defaultShipmentLineItem, with(replacedProductCode, "P10"), with(orderId, 1L),
      with(quantityShipped, 20))));

    when(requisitionService.getNonSkippedLineItem(shipmentLineItem.getOrderId(), "P10")).thenReturn(null);
    when(productService.getByCode("P123")).thenReturn(new Product());
    when(productService.getByCode("P10")).thenReturn(null);

    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.save(shipmentLineItem);
  }

}
