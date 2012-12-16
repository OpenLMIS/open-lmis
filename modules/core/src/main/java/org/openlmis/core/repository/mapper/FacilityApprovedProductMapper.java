package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityApprovedProductMapper {

  @Insert("INSERT INTO facility_approved_products(" +
      "facilityTypeId, productId, modifiedBy, modifiedDate) values " +
      "((SELECT id FROM facility_type WHERE LOWER(code) = LOWER(#{facilityTypeCode}))," +
      "(SELECT id FROM product WHERE LOWER(code) = LOWER(#{productCode})), #{modifiedBy}, #{modifiedDate})")
  int insert(FacilityApprovedProduct facilityApprovedProduct);

}
