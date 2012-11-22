package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RnrLineItem {

    private int id;
    private int rnrId;
    private String productCode;

    private int quantityReceived;
    private int quantityDispensed ;
    private int beginningBalance;
    private int estimatedConsumption;
    private int stockInHand;
    private int quantityRequested;
    private String reasonForRequestedQuantity;
    private int calculatedOrderQuantity;

    private int quantityApproved;
    private int lossesAndAdjustments;
    private String reasonForLossesAndAdjustments;

    private int patientCount;
    private int stockOutDays;
    private float normalizedConsumption;
    private float amc;
    private String maxStockQuantity;

    private int packsToShip;
    private float cost;
    private String remarks;

    private String modifiedBy;
    private Date modifiedDate;

    public RnrLineItem(int rnrId, String productCode, String modifiedBy, Date modifiedDate) {
        this.rnrId = rnrId;
        this.productCode = productCode;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

}
