package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Rnr {

    private int id;
    private String facilityCode;
    private String programCode;
    private RnrStatus status;

    private List<RnrLineItem> lineItems = new ArrayList<>();

    private String modifiedBy;
    private Date modifiedDate;

    public Rnr(String facilityCode, String programCode, RnrStatus status, String modifiedBy, Date modifiedDate) {
        this.facilityCode = facilityCode;
        this.programCode = programCode;
        this.status = status;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

    public Rnr(String facilityCode, String programCode, RnrStatus status) {
        this.facilityCode = facilityCode;
        this.programCode = programCode;
        this.status = status;
    }

    public void add(RnrLineItem rnrLineItem) {
        lineItems.add(rnrLineItem);
    }

}

