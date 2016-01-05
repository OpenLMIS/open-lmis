package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KitProductMapper {

    @Insert({"INSERT INTO kit_products_relation(kitId, productId, quantity)",
            "VALUES (#{kit.id}, #{product.id}, #{quantity})"})
    @Options(useGeneratedKeys = true)
    Integer insert(KitProduct kitProduct);

    @Select({"SELECT * FROM kit_products_relation kp INNER JOIN products p ON kp.productId = p.id WHERE kp.kitId = #{id}"})
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "kit", column = "kitId", javaType = Kit.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getKitById")),
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
    })
    List<KitProduct> getByKit(Kit kit);
}
