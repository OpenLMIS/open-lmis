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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * DosageUnit represents the Dosage Unit for any product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DosageUnit extends BaseModel implements Importable {
  @ImportField(mandatory = true, name="Dosage Unit Code")
  private String code;

  @ImportField(mandatory = true, name="Display Order")
  private int displayOrder;

  /**
   * Validation method for an instantiated DosageUnit.  A valid dosage unit has a code and a display order.
   * @throws DataException if this dosage unit is not defined well.
   */
  public void isValid() {
    if (code == null
      || code.length() == 0
      || displayOrder <= 0 ) throw new DataException("error.reference.data.missing");
  }
}
