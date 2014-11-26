/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.vaccine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionBatch extends BaseModel {

    private Long batchId;
    private Long originId;
    private String dispatchId;
    private Date expiryDate;
    private Date productionDate;
    private Date recallDate;
    private Manufacturer manufacturer;
    private Donor donor;
    private Date receiveDate;
    private Product product;
    private Facility fromFacility;
    private Facility toFacility;
    private DistributionType distributionTypeId;
    private Long voucherNumber;
    private Integer vialsPerBox;
    private Integer boxLength;
    private Integer boxWidth;
    private Integer boxHeight;
    private Integer unitCost;
    private Integer freight;
    private Integer totalCost;
    private Integer purposeId;

    @SuppressWarnings("unused")
    public String getStringProductionDate() throws ParseException {
        return this.productionDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.productionDate);
    }
    @SuppressWarnings("unused")
    public String getStringReceiveDate() throws ParseException {
        return this.receiveDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.receiveDate);
    }
    @SuppressWarnings("unused")
    public String getStringRecallDate() throws ParseException {
        return this.recallDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.recallDate);
    }
    @SuppressWarnings("unused")
    public String getStringExpiryDate() throws ParseException {
        return this.expiryDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.expiryDate);
    }

}
