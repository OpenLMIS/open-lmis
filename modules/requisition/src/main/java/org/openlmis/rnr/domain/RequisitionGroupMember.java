package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroupMember {
    private Long id;
    private Long requisitionGroupId;
    private Long facilityId;
    private String modifiedBy;
    private Date modifiedDate;

    public RequisitionGroupMember(Long facilityId, Long requisitionGroupId) {
        this.facilityId = facilityId;
        this.requisitionGroupId = requisitionGroupId;
    }
}
