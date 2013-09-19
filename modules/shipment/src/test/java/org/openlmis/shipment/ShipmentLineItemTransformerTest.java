/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.domain.ShipmentLineItem;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ShipmentLineItemTransformerTest {


  public static final String SIMPLE_DATE_FORMAT = "MM/dd/yyyy";
  @Rule
  public ExpectedException expectException = ExpectedException.none();
  private ShipmentLineItemTransformer transformer = new ShipmentLineItemTransformer();

  @Test
  public void shouldTrimAndParseFieldsWithSpaces() throws Exception {

    String orderIdWithSpaces = " 11 ";
    String productCodeWithSpaces = " P111 ";
    String quantityShippedWithSpaces = " 22 ";
    String costWithSpaces = "21 ";
    String packedDateWithSpaces = " 10/10/2013 ";
    String shippedDateWithSpaces = "10/12/2013";

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO(orderIdWithSpaces, productCodeWithSpaces,
      quantityShippedWithSpaces, costWithSpaces, packedDateWithSpaces, shippedDateWithSpaces);


    ShipmentLineItem lineItem = new ShipmentLineItemTransformer().transform(dto, "MM/dd/yyyy", "MM/dd/yyyy", new Date());

    assertThat(lineItem.getProductCode(), is("P111"));
    assertThat(lineItem.getQuantityShipped(), is(22));
    assertThat(lineItem.getOrderId(), is(11L));
    assertThat(lineItem.getPackedDate().toString(), is("Thu Oct 10 00:00:00 IST 2013"));
    assertThat(lineItem.getShippedDate().toString(), is("Sat Oct 12 00:00:00 IST 2013"));
    assertThat(lineItem.getCost().toString(), is("21"));
  }

  @Test
  public void shouldCreateLineItemIfOnlyMandatoryFieldsArePresent() throws Exception {

    ShipmentLineItemDTO dto = dtoWithMandatoryFields();

    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertNull(lineItem.getShippedDate());
    assertNull(lineItem.getCost());
  }

  @Test
  public void shouldSetPackedDateToCreationDateIfPackedDateIsNull(){
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();

    Date ftpDate = new Date();
    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, ftpDate);

    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertThat(lineItem.getPackedDate(), is(ftpDate));
    assertNull(lineItem.getShippedDate());
    assertNull(lineItem.getCost());

  }

  @Test
  public void shouldThrowErrorForWrongRnrIdDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setOrderId("3333.33");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

  }


  @Test
  public void shouldThrowErrorForWrongQuantityShippedDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setQuantityShipped("E333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

  }

  @Test
  public void shouldThrowErrorForWrongCostDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setCost("EE333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

  }

  @Test
  public void shouldThrowErrorForWrongPackedDateDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setPackedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

  }

  @Test
  public void shouldThrowErrorForWrongShippedDateDataType() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setShippedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());

  }

  @Test
  public void shouldThrowErrorIfProductCodeIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setProductCode(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfRnrIdIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setOrderId(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldThrowErrorIfQuantityShippedIsMissing() {
    ShipmentLineItemDTO dto = dtoWithAllFields();
    dto.setQuantityShipped(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
  }

  @Test
  public void shouldCreateLineItemWithAllMandatoryAndGivenOptionalFields() throws Exception {
    ShipmentLineItemDTO dto = dtoWithMandatoryFields();
    dto.setCost("3333.33");


    ShipmentLineItem lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertThat(lineItem.getCost().toString(), is("3333.33"));


    dto = dtoWithMandatoryFields();
    dto.setPackedDate("01/01/2011");
    lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getPackedDate().toString(), is("Sat Jan 01 00:00:00 IST 2011"));

    dto = dtoWithMandatoryFields();
    dto.setShippedDate("01/01/2012");
    lineItem = transformer.transform(dto, SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT, new Date());
    assertThat(lineItem.getShippedDate().toString(), is("Sun Jan 01 00:00:00 IST 2012"));
  }

  private ShipmentLineItemDTO dtoWithMandatoryFields() {

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();
    dto.setProductCode("P123");
    dto.setQuantityShipped("1234");
    dto.setOrderId("111");

    return dto;
  }

  private ShipmentLineItemDTO dtoWithAllFields() {

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    dto.setProductCode("P123");
    dto.setQuantityShipped("1234");
    dto.setOrderId("111");
    dto.setCost("11");
    dto.setShippedDate("03/03/2012");
    dto.setPackedDate("03/03/2012");

    return dto;
  }


}
