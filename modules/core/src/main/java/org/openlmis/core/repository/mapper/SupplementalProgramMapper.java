package org.openlmis.core.repository.mapper;

import org.openlmis.core.domain.moz.SupplementalProgram;

public interface SupplementalProgramMapper {

  SupplementalProgram getSupplementalProgramByCode(String code);
}
