package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivedProductsMapper {

    @Insert({
            "INSERT INTO archived_products (facilityId, productCode) ",
            "VALUES (#{facilityId}, #{archivedProductsCode})"
    })
    void updateArchivedProductList(@Param("facilityId")long facilityId, @Param("archivedProductsCode") String archivedProductsCode);

    @Select({
            "SELECT productCode FROM archived_products WHERE facilityId = #{facilityId}"
    })
    List<String> listArchivedProducts(@Param("facilityId") long facilityId);

    @Delete({
            "DELETE FROM archived_products WHERE facilityId = #{facilityId}"
    })
    void clearArchivedProductList(@Param("facilityId") long facilityId);
}
