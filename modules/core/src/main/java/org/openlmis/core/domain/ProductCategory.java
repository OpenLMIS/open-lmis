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

import lombok.*;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * ProductCategory represents the category for product. Also defines the contract for creation/upload of ProductCategory like
 * code, name and displayOrder are mandatory, their respective data types and headers in upload csv.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductCategory extends BaseModel implements Importable {
  @ImportField(mandatory = true, name = "Category Code")
  private String code;

  @ImportField(mandatory = true, name = "Category Name")
  private String name;

  @ImportField(mandatory = true, type = "int", name = "Display Order")
  private Integer displayOrder;
}
