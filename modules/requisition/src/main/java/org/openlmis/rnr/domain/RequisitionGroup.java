package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class RequisitionGroup implements Importable {


    Integer id;
    @ImportField(mandatory = true, type = "String", name = "RG Code")
    String code;
    @ImportField(mandatory = true, type = "String", name = "Name of RG")
    String name;
    @ImportField(mandatory = true, type = "String", name = "Description")
    String description;
    @ImportField(mandatory = true, type = "String", name = "Level")
    String levelId;

    @ImportField(mandatory = true, type = "String", name = "Head Facility", nested = "code")
    Facility headFacility;
    @ImportField(mandatory = true, type = "String", name = "Parent RG", nested = "code")
    RequisitionGroup parent;
    @ImportField(mandatory = true, type = "boolean", name = "Is Active")
    Boolean active;

    String modifiedBy;
    Date modifiedDate;

}
