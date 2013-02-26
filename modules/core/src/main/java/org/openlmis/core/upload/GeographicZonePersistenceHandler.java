package org.openlmis.core.upload;

import lombok.AllArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.upload.Importable;

@AllArgsConstructor
public class GeographicZonePersistenceHandler extends AbstractModelPersistenceHandler {
  private GeographicZoneRepository repository;

  @Override
  protected void save(Importable modelClass, String modifiedBy) {
    GeographicZone geographicZone = (GeographicZone) modelClass;
    geographicZone.setModifiedBy(modifiedBy);
    repository.save(geographicZone);
  }
}