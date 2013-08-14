package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.domain.ShipmentFileDetail;
import org.springframework.stereotype.Repository;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 3:47 PM
 */
@Repository
public interface ShipmentFileDetailMapper {

    @Insert("Insert into shipment_file_details(orderId, facilityCode, period, productCode, orderedQuantity, suppliedQuantity, alternativeProductCode, alternativeProductDescription, alternativeOrderedQuantity, alternativeSuppliedQuantity, createdDate , createdBy, modifiedBy , modifiedDate )  " +
            "values( #{orderId}, #{facilityCode}, #{period}, #{productCode}, #{orderedQuantity}, #{suppliedQuantity}, #{alternativeProductCode}, #{alternativeProductDescription}, #{alternativeOrderedQuantity}, #{alternativeSuppliedQuantity}" +
                "   , COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
                      "COALESCE(#{modifiedDate}, NOW())" +
                "  )")
    @Options(useGeneratedKeys = true)
    Integer insert(ShipmentFileDetail detail);


    @Select("select s.* from shipment_file_details s " +
            " where s.productCode = #{productCode} and s.orderId = #{orderId}")
    ShipmentFileDetail getDetailByReferenceCodes(
        @Param(value = "orderId") String orderId
        , @Param(value = "productCode") String productCode

    );


    @Update(" update shipment_file_details " +
            "   set " +
            "       orderId = #{orderId}, facilityCode = #{facilityCode}, period = #{period}, productCode = #{productCode}, orderedQuantity = #{orderedQuantity}, suppliedQuantity = #{suppliedQuantity}, alternativeProductCode = #{alternativeProductCode}, alternativeProductDescription = #{alternativeProductDescription}, alternativeOrderedQuantity = #{alternativeOrderedQuantity}, alternativeSuppliedQuantity = #{alternativeSuppliedQuantity},  modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
            "   where " +
            "       id = #{id}")
    void update(ShipmentFileDetail detail);
}
