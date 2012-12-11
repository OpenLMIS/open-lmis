package org.openlmis.rnr.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Rnr {

    private Long id;
    private Long facilityId;

    private String programCode;
    private RnrStatus status;

    private List<RnrLineItem> lineItems = new ArrayList<>();

    private String modifiedBy;
    private Date modifiedDate;

    public Rnr(){
        System.out.println("");
    }
    public Rnr(Long facilityId, String programCode, RnrStatus status, String modifiedBy) {
        this.facilityId = facilityId;
        this.programCode = programCode;
        this.status = status;
        this.modifiedBy = modifiedBy;
    }

    public void add(RnrLineItem rnrLineItem) {
        lineItems.add(rnrLineItem);
    }

}

