package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.KitProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * KitProductMapper maps the KitProduct entity to corresponding representation in database.
 */
@Repository
public interface KitProductMapper {

    @Select("SELECT * FROM kit_products_relation WHERE productcode = #{productCode}")
    public List<KitProduct> getByProductCode(@Param("productCode") String productCode);
}
