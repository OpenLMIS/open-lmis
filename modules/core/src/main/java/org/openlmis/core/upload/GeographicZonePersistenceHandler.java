/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.GeographicZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("geographicZonePersistenceHandler")
public class GeographicZonePersistenceHandler extends AbstractModelPersistenceHandler {
  public static final String DUPLICATE_GEOGRAPHIC_ZONE_CODE = "Duplicate Geographic Zone Code";
  GeographicZoneService geographicZoneService;

  @Autowired
  public GeographicZonePersistenceHandler(GeographicZoneService geographicZoneService) {
    super(DUPLICATE_GEOGRAPHIC_ZONE_CODE);
    this.geographicZoneService = geographicZoneService;
  }


  @Override
  protected BaseModel getExisting(BaseModel record) {
    return geographicZoneService.getByCode((GeographicZone) record);
  }

  @Override
  protected void save(BaseModel record) {
    geographicZoneService.save((GeographicZone) record);
  }


}