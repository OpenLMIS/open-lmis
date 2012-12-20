package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class SupervisoryNode implements Importable {

    Integer id;

    @ImportField(name = "Supervisory Node Code", mandatory = true)
    private String code;

    @ImportField(name = "Name of Node", mandatory = true)
    private String name;

    @ImportField(name = "Description")
    private String description;

    @ImportField(name = "Is Approval Point", type = "boolean", mandatory = true)
    private Boolean approvalPoint;

    @ImportField(name = "Parent Node", nested = "code")
    private SupervisoryNode parent;

    @ImportField(name = "Facility Code", mandatory = true, nested = "code")
    private Facility facility;

    private String modifiedBy;
    private Date modifiedDate;

}
