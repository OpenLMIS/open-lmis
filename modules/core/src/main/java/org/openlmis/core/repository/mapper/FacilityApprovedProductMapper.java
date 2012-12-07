package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityApprovedProductMapper {

    @Insert("insert into facility_approved_product(facility_type_id, product_id, modified_by, modified_date) " +
            "values ((select id from facility_type where LOWER(code) = LOWER(#{facilityTypeCode})),(select id from product where LOWER(code) = LOWER(#{productCode})) , #{modifiedBy}, #{modifiedDate})")
    int insert(FacilityApprovedProduct facilityApprovedProduct);

    @Delete("delete from facility_approved_product")
     void deleteAll();

}
