package org.openlmis.core.repository;

import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;

public class ProgramSupportedEventRepository {
  public ProgramSupportedEventDTO getProgramSupportedEventDTO(ProgramSupported programSupported) {
    return new ProgramSupportedEventDTO();
  }
}
