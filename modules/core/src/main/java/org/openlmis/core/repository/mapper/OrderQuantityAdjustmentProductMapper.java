package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentProduct;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderQuantityAdjustmentProductMapper {
    @Select("Select * from order_quantity_adjustment_products")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(property = "facility.id", column = "facilityId")
    })
    public List<OrderQuantityAdjustmentProduct> getAll();

    @Insert("INSERT INTO order_quantity_adjustment_products(facilityid, productid, typeid, factorid, startdate, enddate, \n" +
            "            minmonthsofstock, maxmonthsofstock, formula, createdby, createddate, modifiedby, modifieddate)\n" +
            "    VALUES (#{facility.id}, #{product.id}, #{adjustmentType.id}, #{adjustmentFactor.id}, #{startDate}, #{endDate},\n" +
            "     #{minMOS}, #{maxMOS}, #{formula}, #{createdBy}, COALESCE(#{createdDate}, CURRENT_TIMESTAMP), #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP));\n")
    @Options(useGeneratedKeys = true)
    public void insert(OrderQuantityAdjustmentProduct adjustmentProduct);

    @Select("Select * from order_quantity_adjustment_products where productId = #{productId} and facilityId = #{facilityId}")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(property = "facility.id", column = "facilityId"),
            @Result(property = "minMOS", column = "minmonthsofstock"),
            @Result(property = "maxMOS", column = "maxmonthsofstock"),
            @Result(property = "adjustmentType", column = "typeId", javaType = OrderQuantityAdjustmentType.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper.getById")),
            @Result(property = "adjustmentFactor", column = "factorId", javaType = OrderQuantityAdjustmentFactor.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.OrderQuantityAdjustmentFactorMapper.getById")),
    })
    public OrderQuantityAdjustmentProduct getByProductAndFacility(@Param("productId") Long productId, @Param("facilityId") Long facilityId);
}
