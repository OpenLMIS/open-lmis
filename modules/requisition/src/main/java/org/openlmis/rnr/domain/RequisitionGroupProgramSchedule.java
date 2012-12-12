package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroupProgramSchedule implements Importable {

    @ImportField(mandatory = true, name = "RG Code", nested = "code")
    RequisitionGroup requisitionGroup;

    @ImportField(mandatory = true, name = "Program", nested = "code")
    Program program;

    @ImportField(mandatory = true, name = "Schedule", nested = "code")
    Schedule schedule;

    String modifiedBy;
    Date modifiedDate;
}
