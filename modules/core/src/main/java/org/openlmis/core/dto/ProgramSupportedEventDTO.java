package org.openlmis.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProgramSupportedEventDTO extends BaseFeedDTO {

  private String facilityCode;
  private String programCode;
  private String programName;
  private Boolean programStatus;
}
