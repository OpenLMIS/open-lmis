/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryTransaction extends BaseModel{
    private Date today;
    private TransactionType transactionType;
    private List<InventoryBatch> inventoryBatches;
    private Facility fromFacility;
    private Facility toFacility;
    private Product product;
    private String dispatchReference;
    private Date dispatchDate;
    private String bol;
    private Donor donor;
    private Long originId;
    private Manufacturer manufacturer;
    private Status status;
    private String purpose;
    private Boolean vvmTracked;
    private Boolean barCoded;
    private Boolean gs1;
    private Integer quantity;
    private Integer packSize;
    private Double unitPrice;
    private Double totalCost;
    private VaccineStorage storageLocation;
    private Date expectedDate;
    private Date arrivalDate;
    private User confirmedBy;
    private String note;

    @SuppressWarnings("unused")
    public String getStringArrivalDate() throws ParseException {
        return this.arrivalDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.arrivalDate);
    }
}
