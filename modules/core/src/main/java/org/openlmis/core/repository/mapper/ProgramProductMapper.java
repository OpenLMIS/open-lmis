package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramProductMapper {

    @Select("INSERT INTO program_products(programId, productId, dosesPerMonth, active, modifiedBy, modifiedDate)" +
            "VALUES ((select id from programs where LOWER(code)=  LOWER(#{program.code}))," +
            "(select id from products where LOWER(code)=  LOWER(#{product.code})), " +
            "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate}) returning id")
    Integer insert(ProgramProduct programProduct);
    // TODO : use programId

    // Used by FacilityApprovedProductMapper
    @Select("SELECT * FROM program_products WHERE id = #{id}")
    @Results(value = {
            @Result(property = "product", column = "productId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "program", column = "programId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
    })
    ProgramProduct getById(Integer id);

}