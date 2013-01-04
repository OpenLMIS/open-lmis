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

  private Integer facilityId;
  private Integer programId;

  @ImportField(mandatory = true, name = "Facility Code")
  private String facilityCode;

  @ImportField(mandatory = true, name = "Program Code")
  private String programCode;

  @ImportField(mandatory = true, name = "Program Is Active", type = "boolean")
  private Boolean active;

  private String modifiedBy;
  private Date modifiedDate;

  public ProgramSupported(Integer facilityId, Integer programId, Boolean active, String modifiedBy, Date modifiedDate) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.active = active;
    this.modifiedBy = modifiedBy;
    this.modifiedDate = modifiedDate;
  }
}
