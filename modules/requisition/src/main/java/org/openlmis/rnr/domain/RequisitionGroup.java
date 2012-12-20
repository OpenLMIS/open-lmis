package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroup implements Importable {


    Integer id;
    @ImportField(mandatory = true, name = "RG Code")
    String code;
    @ImportField(mandatory = true, name = "Name of RG")
    String name;
    @ImportField(name = "Description")
    String description;
    @ImportField(mandatory = true, nested = "code", name = "Supervisory Node")
    SupervisoryNode supervisoryNode;

    String modifiedBy;
    Date modifiedDate;

}
