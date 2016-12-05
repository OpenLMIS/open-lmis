package org.openlmis.core.repository.mapper;

import org.openlmis.core.domain.moz.SupplementalProgram;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplementalProgramMapper {

  SupplementalProgram getSupplementalProgramByCode(String code);
}
