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
    "((SELECT id FROM facility_types WHERE LOWER(code) = LOWER(#{facilityType.code}))," +
    "#{programProduct.id}, #{maxMonthsOfStock}, #{modifiedBy}, DEFAULT)")
  @Options(useGeneratedKeys = true)
  Integer insert(FacilityApprovedProduct facilityApprovedProduct);

  @Select("SELECT fap.id, fap.facilityTypeId, fap.programProductId, fap.maxMonthsOfStock " +
    "FROM products p, facility_approved_products fap, program_products pp, facilities f, " +
    "product_forms pf , dosage_units du, product_categories pc where " +
    "pp.programId = #{programId} " +
    "AND f.id = #{facilityId} AND f.typeId = fap.facilityTypeId " +
    "AND fap.programProductId = pp.id " +
    "AND pc.id = p.categoryId " +
    "AND p.id = pp.productId " +
    "AND pf.id = p.formId " +
    "AND du.id = p.dosageUnitId " +
    "AND p.fullSupply = #{fullSupply} " +
    "AND p.active = true " +
    "AND pp.active = true " +
    "ORDER BY pc.displayOrder, pc.code, p.displayOrder NULLS LAST, p.code")
  @Results(value = {
    @Result(property = "programProduct", column = "programProductID", javaType = ProgramProduct.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramProductMapper.getById")),
    @Result(property = "facilityType.id", column = "facilityTypeId")})
  List<FacilityApprovedProduct> getProductsByFacilityAndProgram(@Param("facilityId") Integer facilityId,
                                                                @Param("programId") Integer programId,
                                                                @Param("fullSupply") Boolean fullSupply);

}
