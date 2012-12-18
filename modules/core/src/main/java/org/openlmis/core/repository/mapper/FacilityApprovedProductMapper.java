package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityApprovedProductMapper {

    @Insert("INSERT INTO facility_approved_products(" +
            "facilityTypeId, programProductId, maxMonthsOfStock, modifiedBy, modifiedDate) values " +
            "((SELECT id FROM facility_type WHERE LOWER(code) = LOWER(#{facilityTypeCode}))," +
            "#{programProductId}, #{maxMonthsOfStock}, #{modifiedBy}, #{modifiedDate})")
    int insert(FacilityApprovedProduct facilityApprovedProduct);
}
