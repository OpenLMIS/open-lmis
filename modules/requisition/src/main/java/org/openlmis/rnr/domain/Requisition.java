package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Requisition {

    private int id;
    private String facilityCode;
    private String programCode;

    private List<RequisitionLineItem> lineItems;
    private RnrStatus status;
    private String modifiedBy;
    private Date modifiedDate;

}

