/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.dto.GeographicZoneGeometry;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.mapper.GeographicZoneGeoJSONMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@NoArgsConstructor
public class GeographicZoneService {

  @Autowired
  GeographicZoneRepository repository;

  @Autowired
  GeographicZoneGeoJSONMapper geoJsonMapper;

  @Autowired
  private SMSService smsService;

    public void save(GeographicZone geographicZone) {
    geographicZone.setLevel(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
    geographicZone.validateLevel();

    if (!geographicZone.isRootLevel()) {
      geographicZone.validateParentExists();
      geographicZone.setParent(repository.getByCode(geographicZone.getParent().getCode()));
      geographicZone.validateParentExists();
      geographicZone.validateParentIsHigherInHierarchy();
    }

    repository.save(geographicZone);
  }

  public GeographicZone getByCode(GeographicZone geographicZone) {
    return repository.getByCode(geographicZone.getCode());
  }

  public GeographicZone getById(long id) {
    return repository.getById(id);
  }


  public List<GeographicZone> searchGeographicZone(String geographicZoneSearchParam) {
    return repository.searchGeographicZone(geographicZoneSearchParam);
  }

  public List<GeographicZone> getAll() {
    return repository.getAllGeographicZones();
  }

  public void saveNew(GeographicZone geographicZone) throws IOException {
    repository.insert_Ext(geographicZone);
    try{
        smsService.SendSMSMessage(String.format("Geo zone %s just added to the database.",geographicZone.getName()),"255689303142"); //17033422762
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
