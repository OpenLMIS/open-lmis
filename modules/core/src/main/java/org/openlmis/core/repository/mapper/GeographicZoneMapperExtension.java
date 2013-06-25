/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public interface GeographicZoneMapperExtension extends GeographicZoneMapper {

    @Select(value = "SELECT * FROM users where LOWER(name) like '%'|| LOWER(#{geographicZoneSearchParam}) ||'%' OR LOWER(code) like '%'|| " +
            "LOWER(#{geographicZoneSearchParam}) ||'%' ")
    List<GeographicZone> getGeographicZoneWithSearchedName(String geographicZoneSearchParam);
}
