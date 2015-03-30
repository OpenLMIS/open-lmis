package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.OrderQuantityAdjustmentProduct;
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
}
