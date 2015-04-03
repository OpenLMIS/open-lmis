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
