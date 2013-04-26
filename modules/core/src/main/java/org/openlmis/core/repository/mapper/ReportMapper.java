/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Report;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportMapper {

  @Select("SELECT * from reports WHERE name=#{name}")
  Report getByName(String name);

  @Select("SELECT * from reports WHERE id=#{id}")
  Report getById(Integer id);

  @Insert("INSERT INTO Reports (name, data, parameters, modifiedBy, modifiedDate) " +
    "VALUES (#{name}, #{data}, #{parameters}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))")
  void insert(Report report);

}
