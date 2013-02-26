package org.openlmis.core.repository.mapper;

import org.openlmis.core.domain.GeographicZone;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicZoneMapper {
  void insert(GeographicZone geographicZone);
}
