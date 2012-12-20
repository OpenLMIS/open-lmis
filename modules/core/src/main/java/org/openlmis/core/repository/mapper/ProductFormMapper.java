package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFormMapper {

    // Used by mapper
    @Select("SELECT * FROM product_forms WHERE id = #{id}")
    ProductForm getById(Integer id);

}
