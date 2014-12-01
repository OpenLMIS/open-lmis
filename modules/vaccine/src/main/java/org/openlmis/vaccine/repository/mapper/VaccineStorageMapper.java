package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.vaccine.domain.StorageType;
import org.openlmis.vaccine.domain.Temperature;
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
            "( storagetypeid, locCode ,name,grosscapacity,netcapacity,temperatureid, createdby, createddate, modifiedby,modifieddate,  dimension,facilityId) ",
            "VALUES",
            "( #{storageType.id}, #{location},#{name}, #{grossCapacity}, #{netCapacity}, #{temperature.id} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}, #{dimension}, #{facility.id}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(VaccineStorage vaccineStorage);


    @Select("SELECT * FROM vaccine_storage where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "loccode", property = "location"),
            @Result(column = "name", property = "name"),
            @Result(column = "grosscapacity", property = "grossCapacity"),
            @Result(column = "netcapacity", property = "netCapacity"),
            @Result(column = "dimension", property = "dimension"),
            @Result(column = "storageTypeId", javaType = StorageType.class, property = "storageType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.StorageTypeMapper.getById")),
            @Result(column = "facilityId", javaType = Facility.class, property = "facility",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(column = "temperatureid", javaType = Temperature.class, property = "temperature",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.TempratureMapper.getById"))
    })
    VaccineStorage getById(Long id);

    @Select("SELECT * " +
            " FROM " +
            "  vaccine_storage   ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "loccode", property = "location"),
            @Result(column = "name", property = "name"),
            @Result(column = "grosscapacity", property = "grossCapacity"),
            @Result(column = "netcapacity", property = "netCapacity"),
            @Result(column = "dimension", property = "dimension"),
            @Result(column = "storageTypeId", javaType = StorageType.class, property = "storageType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.StorageTypeMapper.getById")),
            @Result(column = "facilityId", javaType = Facility.class, property = "facility",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(column = "temperatureid", javaType = Temperature.class, property = "temperature",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.TempratureMapper.getById"))
    })
    List<VaccineStorage> loadAllList();

    @Select("Select * from vaccine_storage where facilityId = #{facilityId}")
    @Results({
            @Result(column = "facilityId", property = "facility.id"),
            @Result(column = "storageTypeId", javaType = StorageType.class, property = "storageType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.StorageTypeMapper.getById"))
    })
    List<VaccineStorage> getByFacilityId(Long facilityId);

    @Update("UPDATE vaccine_storage " +
            "   SET storagetypeid= #{storageType.id}," +
            "  locCode =#{location}, " +
            "  name=#{name}, " +
            " grosscapacity=#{grossCapacity}, " +
            "netcapacity=#{netCapacity}, " +
            "temperatureid=#{temperature.id}," +
            " modifiedby=#{modifiedBy}, " +
            " facilityId=#{facility.id}, " +
            "modifieddate=#{modifiedDate} " +

            " WHERE id=#{id};")
    void update(VaccineStorage vaccineStorage);

    @Delete("DELETE from vaccine_storage " +
            " WHERE id=#{id};")
    void delete(VaccineStorage vaccineStorage);
}
