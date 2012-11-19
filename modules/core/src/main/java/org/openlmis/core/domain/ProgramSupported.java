package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramSupported implements Importable {
    private String facilityCode;
    private String programCode;
    private boolean isActive = false;
    private String modifiedBy;
    private Date modifiedDate;
}
