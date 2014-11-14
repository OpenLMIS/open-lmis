package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.StorageType;
import org.openlmis.vaccine.domain.Temprature;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
@Repository
public interface TempratureMapper {
    @Select("SELECT * FROM temperature ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "temperaturename", property = "tempratureName")
    })
    List<Temprature> loadAllList();

    @Insert({"INSERT INTO temperature",
            "( temperaturename, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{tempratureName} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(Temprature temprature);

    @Select("SELECT * FROM temperature where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "temperaturename", property = "tempratureName")
    })
    Temprature getById(Long id);
    @Update("UPDATE temperature " +
            "   SET temperaturename= #{tempratureName}," +
            " modifiedby=#{modifiedBy}, " +
            "modifieddate=#{modifiedDate} " +

            " WHERE id=#{id};")
    void update(Temprature temprature);

    @Delete("DELETE from temperature " +
            " WHERE id=#{id};")
    void delete(Temprature temprature);
}
