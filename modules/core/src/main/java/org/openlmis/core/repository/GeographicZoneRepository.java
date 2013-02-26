package org.openlmis.core.repository;

import lombok.AllArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

@AllArgsConstructor
public class GeographicZoneRepository {

  @Autowired
  private GeographicZoneMapper mapper;

  public void save(GeographicZone geographicZone) {
    try {
      mapper.insert(geographicZone);
    } catch (DuplicateKeyException exception) {
      throw new DataException("Duplicate Geographic Zone Code found");
    } catch (DataIntegrityViolationException exception) {
      throw new DataException("Invalid Reference Data");
    }
  }
}
