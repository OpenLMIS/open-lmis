package org.openlmis.restapi.domain.integration;

import lombok.Data;

@Data
public class RequisitionLineItemIntergration {

    private String facilityCode;

    private Integer requisitionId;

    private String programCode;

    private String productCode;

    private Integer beginningBalance;

    private Integer quantityReceived;

    private Integer quantityDispensed;

    private Integer totalLossesAndAdjustments;

    private Integer inventory;

    private Integer quantityRequested;

    private Integer quantityApproved;

    private String productFullName;
}