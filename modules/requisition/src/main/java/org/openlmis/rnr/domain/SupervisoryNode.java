package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class SupervisoryNode implements Importable {

    Long id;

    @ImportField(name = "Supervisory Node Code", mandatory = true)
    String code;

    @ImportField(name = "Name of Node", mandatory = true)
    String name;

    @ImportField(name = "Description")
    String description;

    @ImportField(name = "Is Approval Point", type = "boolean", mandatory = true)
    Boolean approvalPoint;

    @ImportField(name = "Parent Node", nested = "code")
    SupervisoryNode parent;

    @ImportField(name = "Facility Code", mandatory = true, nested = "code")
    Facility facility;

    private String modifiedBy;
    private Date modifiedDate;

}
