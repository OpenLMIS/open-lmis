package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramProductMapper {

    @Select("INSERT INTO program_product(program_id, product_id, doses_per_month, active, modified_by, modified_date)" +
            "VALUES ((select id from program where LOWER(code)=  LOWER(#{programCode}))," +
            "(select id from product where LOWER(code)=  LOWER(#{productCode})), " +
            "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate}) returning id")
    Integer insert(ProgramProduct programProduct);

    @Select("select p.id as product_id, p.code as product_code, #{programCode} as program_code, p.primary_name as primary_name, " +
            "p.dispensing_unit, p.dosage_unit_id, " +
            "p.form_id, p.strength, p.doses_per_dispensing_unit, " +
            "pf.code as form_code, pf.display_order as form_display_order, " +
            "du.code as dosage_unit_code, du.display_order as dosage_unit_display_order, " +
            "pp.doses_per_month " +
            "from product p, facility_approved_product fap, program_product pp, facility f, " +
            "product_form pf , dosage_unit du where " +
            "pp.program_id = (select id from program where LOWER(code)=  LOWER(#{programCode})) " +
            "and f.id = #{facilityId} " +
            "and fap.facility_type_id= f.type_id " +
            "and fap.product_id = p.id " +
            "and fap.product_id = pp.product_id " +
            "and pp.product_id = p.id " +
            "and pf.id = p.form_id " +
            "and du.id = p.dosage_unit_id " +
            "and p.full_supply = 'TRUE' " +
            "and p.active = true " +
            "and pp.active = true " +
            "ORDER BY p.display_order NULLS LAST, p.code")
    @Results(value = {
            @Result(property = "product", column = "product_id", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getProductById")),
            @Result(property = "productCode", column = "product_code"),
            @Result(property = "programCode", column = "program_code"),
            @Result(property = "dosesPerMonth", column = "doses_per_month"),
            @Result(property = "product.code", column = "product_code"),
            @Result(property = "product.primaryName", column = "primary_name"),
            @Result(property = "product.dispensingUnit", column = "dispensing_unit"),
            @Result(property = "product.dosesPerDispensingUnit", column = "doses_per_dispensing_unit"),
            @Result(property = "product.strength", column = "strength"),
            @Result(property = "product.form.id", column = "form_id"),
            @Result(property = "product.form.code", column = "form_code"),
            @Result(property = "product.form.displayOrder", column = "form_display_order"),
            @Result(property = "product.dosageUnit.id", column = "dosage_unit_id"),
            @Result(property = "product.dosageUnit.code", column = "dosage_unit_code"),
            @Result(property = "product.dosageUnit.displayOrder", column = "dosage_unit_display_order")
    })
    List<ProgramProduct> getFullSupplyProductsByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                                   @Param("programCode") String programCode);
}