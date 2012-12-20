package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityApprovedProductMapper {

    @Insert("INSERT INTO facility_approved_products(" +
            "facilityTypeId, programProductId, maxMonthsOfStock, modifiedBy, modifiedDate) values " +
            "((SELECT id FROM facility_type WHERE LOWER(code) = LOWER(#{facilityTypeCode}))," +
            "#{programProduct.id}, #{maxMonthsOfStock}, #{modifiedBy}, #{modifiedDate})")
    int insert(FacilityApprovedProduct facilityApprovedProduct);

    @Select("SELECT * " +
            "FROM products p, facility_approved_products fap, program_products pp, facilities f, " +
            "product_forms pf , dosage_unit du where " +
            "pp.programId = (select id from programs WHERE LOWER(code) =  LOWER(#{programCode})) " +
            "AND f.id = #{facilityId} AND f.typeId = fap.facilityTypeId " +
            "AND fap.programProductId = pp.id " +
            "AND p.id = pp.productId " +
            "AND pf.id = p.formId " +
            "AND du.id = p.dosageUnitId " +
            "AND p.fullSupply = 'TRUE' " +
            "AND p.active = true " +
            "AND pp.active = true " +
            "ORDER BY p.displayOrder NULLS LAST, p.code")
    @Results(value = {
            @Result(property = "programProduct", column = "programProductID", javaType = ProgramProduct.class, one = @One(select =  "org.openlmis.core.repository.mapper.ProgramProductMapper.getById")),
    })
    List<FacilityApprovedProduct> getFullSupplyProductsByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                                            @Param("programCode") String programCode);

}
