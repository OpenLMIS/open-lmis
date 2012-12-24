package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class   RequisitionGroupMember implements Importable {

    @ImportField(mandatory = true, name = "RG Code", nested = "code")
    private RequisitionGroup requisitionGroup;

    @ImportField(mandatory = true, name = "Member Facility", nested = "code")
    private Facility facility;

    private String modifiedBy;
    private Date modifiedDate;

    public RequisitionGroupMember(RequisitionGroup requisitionGroup, Facility facility) {
        this.requisitionGroup = requisitionGroup;
        this.facility = facility;
    }
}
