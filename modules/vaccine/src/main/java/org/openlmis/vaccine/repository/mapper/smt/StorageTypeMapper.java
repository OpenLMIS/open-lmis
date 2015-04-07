package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.smt.StorageType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface StorageTypeMapper {
    @Select("SELECT * FROM storage_types ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypename", property = "storageTypeName")
    })

    List<StorageType> loadAllList();
    @Insert({"INSERT INTO storage_types",
            "( storagetypename, createdby, createddate, modifiedby,modifieddate) ",
            "VALUES",
            "( #{storageTypeName} ,#{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(StorageType storageType);

    @Select("SELECT * FROM storage_types where id =#{id} ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "storagetypename", property = "storageTypeName")
    })
    StorageType getById(Long id);
    @Update("UPDATE storage_types " +
            "   SET storagetypename= #{storageTypeName}," +
                       " modifiedby=#{modifiedBy}, " +
            "modifieddate=#{modifiedDate} " +

            " WHERE id=#{id};")
    void update(StorageType storageType);

    @Delete("DELETE from storage_types " +
            " WHERE id=#{id};")
    void delete(StorageType storageType);
    @Select(value = "SELECT * FROM storage_types WHERE LOWER(storagetypename) LIKE '%'|| LOWER(#{param}) ||'%'")
    List<StorageType> searchForStorageTypeList(String param);
}
