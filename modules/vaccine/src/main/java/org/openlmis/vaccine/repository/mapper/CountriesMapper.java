package org.openlmis.vaccine.repository.mapper;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.Countries;
import org.openlmis.vaccine.domain.StorageType;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CountriesMapper {
    @Select("SELECT * FROM countries ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "longName", property = "longName"),
            @Result(column = "isoCode2", property = "isoCode2"),
            @Result(column = "isoCode3", property = "isoCode3")
    })
    List<Countries> loadAllList();
    @Insert({"INSERT INTO countries",
            "( name,longName,isoCode2,isoCode3, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{name},#{longName},#{isoCode2},#{isoCode3} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(Countries countries);

    @Select("SELECT * FROM countries where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "longName", property = "longName"),
            @Result(column = "isoCode2", property = "isoCode2"),
            @Result(column = "isoCode3", property = "isoCode3")
    })
    Countries getById(Long id);
    @Update("UPDATE countries " +
            "   SET name= #{name}," +
            " longName=#{longName}, " +
            "isoCode2=#{isoCode2}, " +
            "isoCode3=#{isoCode3}, " +
            "modifieddate=#{modifiedDate}, " +
            "modifiedby=#{modifiedBy} " +
            " WHERE id=#{id};")
    void update(Countries countries);

    @Delete("DELETE from countries " +
            " WHERE id=#{id};")
    void delete(Countries countries);
    @Select(value = "SELECT * FROM countries WHERE LOWER(name) LIKE '%'|| LOWER(#{param}) ||'%'")
    List<Countries> searchForCountriesList(String param);
}
