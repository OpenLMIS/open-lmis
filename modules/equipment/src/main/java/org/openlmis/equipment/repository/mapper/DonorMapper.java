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

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.Donor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonorMapper {

  @Select("SELECT * from donors order by shortName, longName")
  List<Donor> getAll();

  @Select("Select * from donors AS d " +
      "LEFT JOIN (SELECT primaryDonorId AS id, Count(*) donationCount FROM equipment_inventories fpi " +
      "          GROUP  BY primaryDonorId) AS x " +
      "          ON d.id = x.id " +
      " Order By d.shortName, d.longName")
  @Results(value = {
      @Result(property = "donationCount",column = "countOfDonations")
  })
  List<Donor> getAllWithDetails();

  @Insert("INSERT INTO donors" +
      "(code, shortName, longName, createdBy, modifiedBy, modifiedDate) " +
      "values (#{code}, #{shortName}, #{longName}, #{createdBy}, #{modifiedBy}, #{modifiedDate}) ")
  @Options(useGeneratedKeys = true)
  Integer insert(Donor donor);

  @Update("UPDATE donors " +
      "SET shortName = #{shortName}, longName =  #{longName}, code = #{code}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
      "WHERE id = #{id}")
  void update(Donor donor);

  @Delete("DELETE FROM donors WHERE ID = #{id}")
  void remove(@Param(value = "id") Long id);

  @Select("SELECT id, code, shortName, longName, modifiedBy, modifiedDate " +
      "FROM donors WHERE id = #{id}")
  Donor getById(Long id);

  @Select("SELECT * FROM donors where LOWER(code) = LOWER(#{code})")
  Donor getByCode(String code);
}