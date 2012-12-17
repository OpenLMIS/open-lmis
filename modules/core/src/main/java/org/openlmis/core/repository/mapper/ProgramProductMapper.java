package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
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

    @Select("SELECT p.id AS product_id, p.code AS productCode, #{programCode} AS programCode, p.primaryName, " +
            "p.dispensingUnit, p.dosageUnitId, " +
            "p.formId, p.strength, p.dosesPerDispensingUnit, " +
            "pf.code AS form_code, pf.display_order AS form_display_order, " +
            "du.code AS dosage_unit_code, du.display_order AS dosage_unit_display_order, " +
            "pp.dosesPerMonth " +
            "from products p, facility_approved_products fap, program_products pp, facilities f, " +
            "product_form pf , dosage_unit du where " +
            "pp.programId = (select id from programs where LOWER(code) =  LOWER(#{programCode})) " +
            "AND f.id = #{facilityId} AND f.typeId = fap.facilityTypeId " +
            "AND fap.productId = p.id " +
            "AND fap.productId = pp.productId " +
            "AND pp.productId = p.id " +
            "AND pf.id = p.formId " +
            "AND du.id = p.dosageUnitId " +
            "AND p.fullSupply = 'TRUE' " +
            "AND p.active = true " +
            "AND pp.active = true " +
            "ORDER BY p.displayOrder NULLS LAST, p.code")
    @Results(value = {
            @Result(property = "product", column = "product_id", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getProductById")),
            @Result(property = "product.code", column = "productCode"),
            @Result(property = "product.primaryName", column = "primaryName"),
            @Result(property = "product.dispensingUnit", column = "dispensingUnit"),
            @Result(property = "product.dosesPerDispensingUnit", column = "dosesPerDispensingUnit"),
            @Result(property = "product.strength", column = "strength"),
            @Result(property = "product.form.id", column = "formId"),
            @Result(property = "product.form.code", column = "form_code"),
            @Result(property = "product.form.displayOrder", column = "form_display_order"),
            @Result(property = "product.dosageUnit.id", column = "dosage_unit_id"),
            @Result(property = "product.dosageUnit.code", column = "dosage_unit_code"),
            @Result(property = "product.dosageUnit.displayOrder", column = "dosage_unit_display_order")
    })
    List<ProgramProduct> getFullSupplyProductsByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                                   @Param("programCode") String programCode);
}