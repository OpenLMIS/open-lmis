package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroupProgramSchedule implements Importable {

    @ImportField(mandatory = true, name = "RG Code", nested = "code")
    private RequisitionGroup requisitionGroup;

    @ImportField(mandatory = true, name = "Program", nested = "code")
    private Program program;

    @ImportField(mandatory = true, name = "Schedule", nested = "code")
    private ProcessingSchedule schedule;

    @ImportField(mandatory = true, name = "Direct Delivery")
    private boolean directDelivery;

    @ImportField(name = "Drop off Facility", nested = "code")
    private Facility dropOffFacility;

    String modifiedBy;
    Date modifiedDate;
}
