package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.inventory.Lot;
import org.springframework.stereotype.Repository;

@Repository
public interface LotDtoMapper {

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE id = #{id}")
    @Results({
            @Result(
                    property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "lotCode", column = "lotnumber"),
    })
    Lot getById(@Param("id")Long id);
}
