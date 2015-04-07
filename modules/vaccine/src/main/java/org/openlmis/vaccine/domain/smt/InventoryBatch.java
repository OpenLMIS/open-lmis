/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.domain.smt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class InventoryBatch extends BaseModel{
    private InventoryTransaction inventoryTransaction;
    private String batchNumber;
    private Date productionDate;
    private Date expiryDate;
    private Integer quantity;
    private Integer vvm1;
    private Integer vvm2;
    private Integer vvm3;
    private Integer vvm4;
    private String note;

    @SuppressWarnings("unused")
    public String getStringProductionDate() throws ParseException {
        return this.productionDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.productionDate);
    }

    @SuppressWarnings("unused")
    public String getStringExpiryDate() throws ParseException {
        return this.expiryDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.expiryDate);
    }
}
