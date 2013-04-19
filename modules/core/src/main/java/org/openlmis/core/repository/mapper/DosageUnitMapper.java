/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.DosageUnit;
import org.springframework.stereotype.Repository;

@Repository
public interface DosageUnitMapper {

    // Used by mapper
    @Select("SELECT * FROM dosage_units WHERE id = #{id}")
    DosageUnit getById(Integer id);

}
