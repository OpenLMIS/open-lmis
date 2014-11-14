package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.VaccineStorage;
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
public interface VaccineStorageMapper {

    @Insert({"INSERT INTO vaccine_storage",
            "( storagetypeid, location,grosscapacity,netcapacity,temperatureid, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{storageTypeId.id}, #{location}, #{grossCapacity}, #{netCapacity}, #{tempretureId.id} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(VaccineStorage vaccineStorage);


    @Select("SELECT * FROM vaccine_storage where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypeid", property = "storageTypeId.id"),
            @Result(column = "location", property = "location"),
            @Result(column = "grosscapacity", property = "grossCapacity"),
            @Result(column = "netcapacity", property = "netCapacity"),
            @Result(column = "temperatureid", property = "tempretureId.id")
    })
    VaccineStorage getById(Long id);

    @Select("SELECT " +
            "  vaccine_storage.id, " +
            "  vaccine_storage.storagetypeid, " +
            "  vaccine_storage.location, " +
            "  vaccine_storage.grosscapacity, " +
            "  vaccine_storage.netcapacity, " +
            "  vaccine_storage.temperatureid, " +
            "  vaccine_storage.createdby, " +
            "  vaccine_storage.createddate, " +
            "  vaccine_storage.modifiedby, " +
            "  vaccine_storage.modifieddate, " +
            "  temperature.temperaturename, " +
            "  storage_types.storagetypename" +
            " FROM " +
            "  public.vaccine_storage  " +
            " LEFT OUTER JOIN " +
            "   public.temperature  ON vaccine_storage.temperatureid = temperature.id " +
            "   LEFT OUTER JOIN " +
            "   public.storage_types " +
            " ON vaccine_storage.storagetypeid = storage_types.id ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypeid", property = "storageTypeId.id"),
            @Result(column = "storagetypename", property = "storageTypeId.storageTypeName"),
            @Result(column = "location", property = "location"),
            @Result(column = "grosscapacity", property = "grossCapacity"),
            @Result(column = "netcapacity", property = "netCapacity"),
            @Result(column = "temperatureid", property = "tempretureId.id"),
            @Result(column = "temperaturename", property = "tempretureId.tempratureName")
    })
    List<VaccineStorage> loadAllList();

    @Update("UPDATE vaccine_storage " +
            "   SET storagetypeid= #{storageTypeId.id}," +
            " location= #{location}," +
            " grosscapacity=#{grossCapacity}, " +
            "netcapacity=#{netCapacity}, " +
            "temperatureid=#{tempretureId.id}," +
            " modifiedby=#{modifiedBy}, " +
            "modifieddate=#{modifiedDate} " +

            " WHERE id=#{id};")
    void update(VaccineStorage vaccineStorage);

    @Delete("DELETE from vaccine_storage " +
            " WHERE id=#{id};")
    void delete(VaccineStorage vaccineStorage);
}
