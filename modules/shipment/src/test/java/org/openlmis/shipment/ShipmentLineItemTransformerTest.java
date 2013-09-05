/*
 * CShipment © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentLineItem;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ShipmentLineItemTransformerTest {


  public static final String SIMPLE_DATE_FORMAT = "MM/dd/yyyy";
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldTrimAndParseFieldsWithSpaces() throws Exception {

    String orderIdWithSpaces = " 11 ";
    String productCodeWithSpaces = " P111 ";
    String quantityShippedWithSpaces = " 22 ";
    String costWithSpaces = "21 ";
    String packedDateWithSpaces = " 10/10/2013 ";
    String shippedDateWithSpaces = "10/12/2013";
    ShipmentLineItem lineItem = new ShipmentLineItemTransformer(orderIdWithSpaces, productCodeWithSpaces,
      quantityShippedWithSpaces, costWithSpaces, packedDateWithSpaces, shippedDateWithSpaces)
      .transform("MM/dd/yyyy", "MM/dd/yyyy");

    assertThat(lineItem.getProductCode(), is("P111"));
    assertThat(lineItem.getQuantityShipped(), is(22));
    assertThat(lineItem.getOrderId(), is(11L));
    assertThat(lineItem.getPackedDate().toString(), is("Thu Oct 10 00:00:00 IST 2013"));
    assertThat(lineItem.getShippedDate().toString(), is("Sat Oct 12 00:00:00 IST 2013"));
    assertThat(lineItem.getCost().toString(), is("21"));
  }

  @Test
  public void shouldCreateLineItemIfOnlyMandatoryFieldsArePresent() throws Exception {

    ShipmentLineItemTransformer transformer = transformerWithMandatoryFields();

    ShipmentLineItem lineItem = transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertNull(lineItem.getPackedDate());
    assertNull(lineItem.getShippedDate());
    assertNull(lineItem.getCost());
  }

  @Test
  public void shouldThrowErrorForWrongRnrIdDataType() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setOrderId("3333.33");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

  }


  @Test
  public void shouldThrowErrorForWrongQuantityShippedDataType() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setQuantityShipped("E333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

  }

  @Test
  public void shouldThrowErrorForWrongCostDataType() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setCost("EE333");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

  }

  @Test
  public void shouldThrowErrorForWrongPackedDateDataType() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setPackedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

  }

  @Test
  public void shouldThrowErrorForWrongShippedDateDataType() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setShippedDate("AAA");

    expectException.expect(DataException.class);
    expectException.expectMessage("wrong.data.type");

    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);

  }

  @Test
  public void shouldThrowErrorIfProductCodeIsMissing() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setProductCode(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
  }

  @Test
  public void shouldThrowErrorIfRnrIdIsMissing() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setOrderId(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
  }

  @Test
  public void shouldThrowErrorIfQuantityShippedIsMissing() {
    ShipmentLineItemTransformer transformer = transformerWithAllFields();
    transformer.setQuantityShipped(null);

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.field.missing");
    transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
  }

  @Test
  public void shouldCreateLineItemWithAllMandatoryAndGivenOptionalFields() throws Exception {
    ShipmentLineItemTransformer transformer = transformerWithMandatoryFields();
    transformer.setCost("3333.33");


    ShipmentLineItem lineItem = transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
    assertThat(lineItem.getProductCode(), is("P123"));
    assertThat(lineItem.getQuantityShipped(), is(1234));
    assertThat(lineItem.getOrderId(), is(111L));
    assertThat(lineItem.getCost().toString(), is("3333.33"));


    transformer = transformerWithMandatoryFields();
    transformer.setPackedDate("01/01/2011");
    lineItem = transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
    assertThat(lineItem.getPackedDate().toString(), is("Sat Jan 01 00:00:00 IST 2011"));

    transformer = transformerWithMandatoryFields();
    transformer.setShippedDate("01/01/2012");
    lineItem = transformer.transform(SIMPLE_DATE_FORMAT, SIMPLE_DATE_FORMAT);
    assertThat(lineItem.getShippedDate().toString(), is("Sun Jan 01 00:00:00 IST 2012"));
  }

  private ShipmentLineItemTransformer transformerWithMandatoryFields() {

    ShipmentLineItemTransformer transformer = new ShipmentLineItemTransformer();
    transformer.setProductCode("P123");
    transformer.setQuantityShipped("1234");
    transformer.setOrderId("111");

    return transformer;
  }

  private ShipmentLineItemTransformer transformerWithAllFields() {

    ShipmentLineItemTransformer transformer = new ShipmentLineItemTransformer();

    transformer.setProductCode("P123");
    transformer.setQuantityShipped("1234");
    transformer.setOrderId("111");
    transformer.setCost("11");
    transformer.setShippedDate("03/03/2012");
    transformer.setPackedDate("03/03/2012");

    return transformer;
  }


}
