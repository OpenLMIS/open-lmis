/*
  * This program is part of the OpenLMIS logistics management information system platform software.
  * Copyright © 2013 VillageReach
  *
  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
  *  
  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
  */

package org.openlmis.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;

import java.util.Date;
import java.util.List;

/**
 * This entity represents DTO for product form containing product details and list of programs associated with it.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ProductDTO {

  private Product product;

  private Date productLastUpdated;

  private List<ProgramProduct> programProducts;

  private List<ProductPriceSchedule> productPriceSchedules;
}
