package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductMapper {

    @Insert({"INSERT INTO program_products(programId, productId, dosesPerMonth, active, modifiedBy, modifiedDate)",
            "VALUES ((SELECT id FROM programs WHERE LOWER(code) = LOWER(#{program.code})),",
            "(SELECT id FROM products WHERE LOWER(code) = LOWER(#{product.code})),",
            "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate})"})
    @Options(useGeneratedKeys = true)
    Integer insert(ProgramProduct programProduct);
    // TODO : use programId

    // Used by FacilityApprovedProductMapper
    @SuppressWarnings("unused")
    @Select("SELECT * FROM program_products WHERE id = #{id}")
    @Results(value = {
            @Result(property = "product", column = "productId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "program", column = "programId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
    })
    ProgramProduct getById(Integer id);

}