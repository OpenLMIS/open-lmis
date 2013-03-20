package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class GeographicZoneRepository {

  private GeographicZoneMapper mapper;
  private GeographicLevelMapper geographicLevelMapper;

  @Autowired
  public GeographicZoneRepository(GeographicZoneMapper mapper, GeographicLevelMapper geographicLevelMapper) {
    this.mapper = mapper;
    this.geographicLevelMapper = geographicLevelMapper;
  }

  public GeographicZone getByCode(String code) {
    return mapper.getGeographicZoneByCode(code);
  }

  public Integer getLowestGeographicLevel() {
    return geographicLevelMapper.getLowestGeographicLevel();
  }

  public List<GeographicZone> getAllGeographicZones() {
    return mapper.getAllGeographicZones();
  }

  public void insert(GeographicZone zone) {
    try {
      mapper.insert(zone);
    } catch (DataIntegrityViolationException e) {
      throw new DataException("Incorrect Data Length");
    }
  }

  public void update(GeographicZone zone) {
    try {
      mapper.update(zone);
    } catch (DataIntegrityViolationException e) {
      throw new DataException("Incorrect Data Length");
    }
  }

  public GeographicLevel getGeographicLevelByCode(String code) {
    return mapper.getGeographicLevelByCode(code);
  }
}
