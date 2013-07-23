/*
    Henok Getachew
    eLMIS
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@NoArgsConstructor
public class GeographicZoneServiceExtension extends GeographicZoneService {

  @Autowired
  GeographicZoneRepositoryExtension repository;

    public List<GeographicZone> searchGeographicZone(String geographicZoneSearchParam) {
        return repository.searchGeographicZone(geographicZoneSearchParam);
    }

    public List<GeographicZone> getAll() {
        return repository.getAllGeographicZones();
    }

    public void saveNew(GeographicZone geographicZone) {
        repository.insert_Ext(geographicZone);
    }

    public void update(GeographicZone geographicZone) {
        repository.update(geographicZone);
    }

    public GeographicZone getById(int id){
        return repository.getById(id);
    }

}
