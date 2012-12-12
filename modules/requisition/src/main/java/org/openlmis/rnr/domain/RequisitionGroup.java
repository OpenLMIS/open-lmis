package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroup implements Importable {


    Long id;
    @ImportField(mandatory = true, name = "RG Code")
    String code;
    @ImportField(mandatory = true, name = "Name of RG")
    String name;
    @ImportField(mandatory = true, name = "Description")
    String description;
    @ImportField(mandatory = true, nested = "code", name = "Supervisory Node")
    SupervisoryNode supervisoryNode;

    String modifiedBy;
    Date modifiedDate;

}
