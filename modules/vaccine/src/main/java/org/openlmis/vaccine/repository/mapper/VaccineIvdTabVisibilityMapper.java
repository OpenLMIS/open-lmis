/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
