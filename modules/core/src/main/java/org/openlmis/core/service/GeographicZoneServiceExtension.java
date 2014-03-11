/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.dto.GeographicZoneGeometry;
import org.openlmis.core.repository.GeographicZoneRepositoryExtension;
import org.openlmis.core.repository.mapper.GeographicZoneGeoJSONMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Primary
@Service
@NoArgsConstructor
public class GeographicZoneServiceExtension extends GeographicZoneService {

  @Autowired
  GeographicZoneRepositoryExtension repository;

  @Autowired
  GeographicZoneGeoJSONMapper geoJsonMapper;

  @Autowired
  private SMSService smsService;

  public List<GeographicZone> searchGeographicZone(String geographicZoneSearchParam) {
    return repository.searchGeographicZone(geographicZoneSearchParam);
  }

  public List<GeographicZone> getAll() {
    return repository.getAllGeographicZones();
  }

  public void saveNew(GeographicZone geographicZone) throws IOException {
    repository.insert_Ext(geographicZone);
    try{
        smsService.SendSMSMessage(String.format("Geo zone %s just added to the database.",geographicZone.getName()),"0911305468");
    }
    catch (IOException e){
        throw e;
    }

  }

  public void update(GeographicZone geographicZone) {
    repository.update(geographicZone);
  }

  public GeographicZone getById(int id) {
    return repository.getById(id);
  }

  public void saveGisInfo(List<GeographicZoneGeometry> geoZoneGeometries, Long userId) {
     for(GeographicZoneGeometry geoData: geoZoneGeometries ){
       // check if the zone has an entry
       GeographicZoneGeometry existing = geoJsonMapper.getGeographicZoneGeoJSONbyZoneId(geoData.getZoneId());
       geoData.setModifiedBy(userId);
       if(existing != null){
         geoData.setId(existing.getId());
         geoJsonMapper.update(geoData);
       }else{
         geoData.setCreatedBy(userId);
         geoJsonMapper.insert(geoData);
       }
     }
  }
}
