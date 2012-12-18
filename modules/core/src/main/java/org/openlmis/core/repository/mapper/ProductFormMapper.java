package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFormMapper {

    // Used by mapper
    @Select("SELECT * FROM product_form WHERE id = #{id}")
    ProductForm getById(Integer id);

}
