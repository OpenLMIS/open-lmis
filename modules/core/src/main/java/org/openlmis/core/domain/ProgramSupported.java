package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramSupported implements Importable {

    @ImportField(mandatory = true, name = "FacilityCode")
    private String facilityCode;
    @ImportField(mandatory = true, name = "ProgramCode")
    private String programCode;
    @ImportField(mandatory = true, name = "ProgramIsActive", type = "boolean")
    private boolean isActive = false;

    private String modifiedBy;
    private Date modifiedDate;

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}
