package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.ProgramSupported;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProgramSupportedEventDTO extends BaseFeedDTO {

  private String facilityCode;
  private List<ProgramSupported> programSupportedList;

}
