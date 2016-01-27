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
import org.openlmis.vaccine.domain.VaccineDisease;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseMapper {

  @Select("select * from vaccine_diseases order by displayOrder")
  List<VaccineDisease> getAll();

  @Insert("insert into vaccine_diseases (name, description, displayOrder, modifiedBy, createdBy) values " +
    "(#{name}, #{description}, #{displayOrder}, #{modifiedBy}, #{createdBy})")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(VaccineDisease disease);

  @Update("update vaccine_diseases " +
    "set " +
    " name = #{name}, " +
    " description = #{description}," +
    " displayOrder = #{displayOrder}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = #{modifiedDate} " +
    "where id = #{id}")
  void update(VaccineDisease disease);

  @Select("select * from vaccine_diseases where id = #{id}")
  VaccineDisease getById(@Param("id") Long id);
}
