package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.libs.com.zwitserloot.cmdreader.Mandatory;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class SupplyLine {

    @ImportField (mandatory = true , name = "Supervising Node" , nested = "code")
    SupervisoryNode supervisoryNode;

    @ImportField ( name ="Description" )
    String description;

    @ImportField ( mandatory = true , name="Program" , nested = "code")
    Program program;

    @ImportField ( mandatory = true , name="Facility" , nested="code")
    Facility supplyingFacility;

    private Date modifiedDate;
    private String modifiedBy;

}
