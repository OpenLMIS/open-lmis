package org.openlmis.core.repository.mapper;

import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramDataColumnMapper {
  ProgramDataColumn getColumnByCode(String public_pharmacy);
}
