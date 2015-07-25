/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.apache.commons.lang.time.DateFormatUtils;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * Lot represents a product-batch, with a specific manufacturer, manufacture date, etc.
 */
@Setter
@Getter
@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Lot extends BaseModel implements Importable {

  @JsonIgnore //Any field which should be represented as its own (potentially expanded) object should be ignored
  @ImportField(mandatory = true)
  private Long productId;

  /* The product field corresponds to the productId field, and is included specifically for consumption by Jackson.
     It should be set to an Integer (productId value) or to a complete Product instance. */
  @JsonSerialize
  private Object product;

  @ImportField(mandatory = true)
  private String lotCode;

  @ImportField(mandatory = true)
  private String manufacturerName;

  @JsonIgnore
  private Date manufactureDate;

  @JsonIgnore
  private Date expirationDate;

  @ImportField
  private Integer quantityOnHand;


  public Lot(Long id, Long productId)
  {
    this.id = id;
    this.productId = productId;
  }

  public void setProduct(Product product)
  {
    this.product = product;
    this.productId = product.id;
  }

  public void setProduct(Long productId)
  {
    HashMap<String, Object> map = new HashMap<>();
    map.put("id" , productId);
    this.product = map;

    this.productId = productId;
  }

  @JsonProperty
  public String getManufactureDate()
  {
    return DateFormatUtils.format(manufactureDate, "yyyy-dd-MM");
  }

  @JsonProperty
  public String getExpirationDate()
  {
    return DateFormatUtils.format(expirationDate, "yyyy-dd-MM");
  }
}
