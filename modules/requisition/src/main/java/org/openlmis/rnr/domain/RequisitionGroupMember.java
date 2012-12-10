package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroupMember {
    private Integer id;
    private Integer requisitionGroupId;
    private Integer facilityId;
    private String modifiedBy;
    private Date modifiedDate;

    public RequisitionGroupMember(Integer facilityId, Integer requisitionGroupId) {
        this.facilityId = facilityId;
        this.requisitionGroupId = requisitionGroupId;
    }
}
