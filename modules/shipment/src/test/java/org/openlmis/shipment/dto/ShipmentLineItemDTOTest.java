package org.openlmis.shipment.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ShipmentLineItemDTOTest {

  private String orderId = "1l";
  private String concatenatedOrderId = "EM000001R";
  private String facilityCode = "F001";
  private String programCode = "EM";
  private String productCode = "P001";
  private String quantityOrdered = "20";
  private String quantityShipped = "10";
  private String cost = "100";
  private String substitutedProductCode = "";
  private String substitutedProductName = "";
  private String substitutedProductQuantityShipped = "";
  private String packSize = "";
  private String packedDate =  "12-10-2013";
  private String shippedDate = "14-09-2013";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfProductCodeIsMissing() throws Exception {


    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
       orderId,
       concatenatedOrderId,
       facilityCode,
       programCode,
       null,
       quantityOrdered,
       substitutedProductCode,
       quantityShipped,
       cost,
       substitutedProductCode,
       substitutedProductName,
       substitutedProductQuantityShipped,
       packSize,
       packedDate,
       shippedDate
      );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfOrderIdIsMissing() throws Exception {
    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
        null,
        concatenatedOrderId,
        facilityCode,
        programCode,
        productCode,
        quantityOrdered,
        substitutedProductCode,
        quantityShipped,
        cost,
        substitutedProductCode,
        substitutedProductName,
        substitutedProductQuantityShipped,
        packSize,
        packedDate,
        shippedDate
    );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldThrowErrorIfQuantityIsMissing() throws Exception {
    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO(
        orderId,
        concatenatedOrderId,
        null,
        programCode,
        productCode,
        quantityOrdered,
        substitutedProductCode,
        quantityShipped,
        cost,
        substitutedProductCode,
        substitutedProductName,
        substitutedProductQuantityShipped,
        packSize,
        packedDate,
        shippedDate
    );

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    shipmentLineItemDTO.checkMandatoryFields();
  }

  @Test
  public void shouldPopulateShipmentLineItemDTO() throws Exception {
    List<String> fieldsInOneRow = asList("1l", "P10", "2", "45", "12-10-2013", "14-09-2013");
    Collection<EDIFileColumn> shipmentFileColumns = asList(new EDIFileColumn("orderId",
      "label.order.id",
      true,
      true,
      1,
      ""), new EDIFileColumn("productCode", "label.product.code", true, true, 2, ""));

    ShipmentLineItemDTO shipmentLineItemDTO = ShipmentLineItemDTO.populate(fieldsInOneRow, shipmentFileColumns);

    assertThat(shipmentLineItemDTO.getOrderId(), is("1l"));
    assertThat(shipmentLineItemDTO.getProductCode(), is("P10"));
    assertThat(shipmentLineItemDTO.getCost(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getPackedDate(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getShippedDate(), is(nullValue()));
    assertThat(shipmentLineItemDTO.getQuantityShipped(), is(nullValue()));
  }
}
