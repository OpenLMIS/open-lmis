package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicLevelMapper {
  @Select("SELECT MAX(levelNumber) FROM geographic_levels")
  Integer getLowestGeographicLevel();
}
