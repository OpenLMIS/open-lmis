package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.VaccineStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Teklu on 11/12/2014.
 */
@Repository
public interface VaccineStorageMapper {
    /*
    this is to create the vaccine storage
     */
    @Insert({"INSERT INTO vaccine_storage",
            "( storagetypeid, location,grosscapacity,netcapacity,temperatureid, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{storageTypeId.id}, #{location}, #{grossCapacity}, #{netCapacity}, #{tempretureId.id} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(VaccineStorage vaccineStorage);
    /*
    load storage detail
     */

    @Select("SELECT * FROM vaccine_storage where id =#{id} ")
        VaccineStorage getById(Long id);
    /*
    load list
     */
    @Select("SELECT * FROM vaccine_storage ")
    List<VaccineStorage> loadAllList();
    /*
    upadte
     */
    /*

     */
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
    /*
    delete
     */
    @Delete("DELETE from vaccine_storage " +
                       " WHERE id=#{id};")
    void delete(VaccineStorage vaccineStorage);
}
