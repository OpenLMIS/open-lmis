package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.vaccine.domain.smt.StorageType;
import org.openlmis.vaccine.domain.smt.Temperature;
import org.openlmis.vaccine.domain.smt.VaccineStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
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
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.StorageTypeMapper.getById")),
            @Result(column = "facilityId", javaType = Facility.class, property = "facility",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(column = "temperatureid", javaType = Temperature.class, property = "temperature",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TempratureMapper.getById"))
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
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.StorageTypeMapper.getById")),
            @Result(column = "facilityId", javaType = Facility.class, property = "facility",
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(column = "temperatureid", javaType = Temperature.class, property = "temperature",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TempratureMapper.getById"))
    })
    List<VaccineStorage> loadAllList();

    @Select("Select * from vaccine_storage where facilityId = #{facilityId}")
    @Results({
            @Result(column = "facilityId", property = "facility.id"),
            @Result(column = "storageTypeId", javaType = StorageType.class, property = "storageType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.StorageTypeMapper.getById"))
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
