/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.openlmis.core.domain.GeographicLevel;

@Primary
@Repository
public interface GeographicLevelMapperExtension extends GeographicLevelMapper {

    @Select("SELECT * FROM geographic_levels" +
            "WHERE id={#id}")
    GeographicLevel loadGeographicLevelById(int id);

}
