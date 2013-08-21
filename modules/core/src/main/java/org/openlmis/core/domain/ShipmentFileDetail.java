/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentFileDetail extends BaseModel implements Importable {

  @ImportField(name = "OrderID", mandatory = true)
  private String orderId;

  @ImportField(name = "Facility Code", mandatory = true)
  private String facilityCode;

  @ImportField(name = "Product Code", mandatory = true)
  private String productCode;

  @ImportField(name = "Ordered Quantity", mandatory = true)
  private int orderedQuantity;

  @ImportField(name = "Supplied Quantity", mandatory = true)
  private int suppliedQuantity;

  @ImportField(name = "Period")
  private String period;

  @ImportField(name = "Alternative Product Code")
  private String alternativeProductCode;

  @ImportField(name = "Alternative Product Description")
  private String alternativeProductDescription;

  @ImportField(name = "Alternative Ordered Quantity")
  private int alternativeOrderedQuantity;

  @ImportField(name = "Alternative Supplied Quantity")
  private int alternativeSuppliedQuantity;


  public ShipmentFileDetail(Long id) {
        this.id = id;
  }
}
