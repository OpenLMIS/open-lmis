/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.config.VaccineIvdTabVisibility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineIvdTabVisibilityMapper {

  @Select("select * from vaccine_ivd_tab_visibilities where programId = #{programId}")
  List<VaccineIvdTabVisibility> getTabVisibilityForProgram(@Param("programId") Long programId);

  @Select("select tab, name, true as visible from vaccine_ivd_tabs")
  List<VaccineIvdTabVisibility> getTabVisibilityForNewProgram();

  @Insert("INSERT INTO vaccine_ivd_tab_visibilities (tab, programId, name, visible)" +
    "values (#{tab}, #{programId}, #{name}, #{visible})")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(VaccineIvdTabVisibility visibility);

  @Update("UPDATE vaccine_ivd_tab_visibilities " +
      " set name = #{name}, visible = #{visible}" +
    " WHERE id = #{id} ")
  Integer update (VaccineIvdTabVisibility visibility);

}
