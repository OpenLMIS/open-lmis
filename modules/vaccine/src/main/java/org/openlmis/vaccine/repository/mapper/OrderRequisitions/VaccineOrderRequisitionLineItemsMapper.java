package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineOrderRequisitionLineItemsMapper {

    @Insert("INSERT INTO vaccine_order_requisition_line_items  " +
            "(orderId,productId,productName,maximumStock,reOrderLevel,bufferStock,stockOnHand,quantityRequested,orderedDate,createdBy, createdDate,modifiedBy,modifiedDate)   " +
            "VALUES(#{orderId},#{productId},#{productName},#{maximumStock},#{reOrderLevel},#{bufferStock},#{stockOnHand},#{quantityRequested},#{orderedDate},#{createdBy}, NOW(),#{modifiedBy},NOW())  ")
    @Options(useGeneratedKeys = true)
    Integer insert(VaccineOrderRequisitionLineItem item);

    @Insert("  INSERT INTO vaccine_order_requisition_line_items(  " +
            "             orderid, productid, productName, maximumStock, reOrderLevel,  " +
            "            bufferStock, stockOnHand, quantityRequested, ordereddate, overriddenisa,  " +
            "            maxmonthsofstock,minMonthsOfStock, eop, createdBy, createddate, modifiedBy, modifieddate )  " +
            "    VALUES ( #{orderId}, #{productId}, #{productName}, #{maximumStock}, #{reOrderLevel},  " +
            "            #{bufferStock}, #{stockOnHand}, #{quantityRequested}, #{orderedDate}, #{overriddenisa},   " +
            "            #{maxmonthsofstock},#{minMonthsOfStock}, #{eop}, #{createdBy}, NOW(), #{modifiedBy}, NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer Insert(VaccineOrderRequisitionLineItem item);

    @Update("UPDATE vaccine_order_requisition_line_items SET " +
            "orderId = #{orderId}, " +
            "productId = #{productId}, " +
            "productName = #{productName}, " +
            "maximumStock = #{maximumStock}, " +
            "reOrderLevel = #{reOrderLevel}, " +
            "bufferStock = #{bufferStock}, " +
            "stockOnHand = #{stockOnHand}, " +
            "quantityRequested = #{quantityRequested}, " +
            "orderedDate = #{orderedDate}, " +
            "modifiedBy = #{modifiedBy}, " +
            "modifiedDate = NOW() " +
            "WHERE id = #{id}"
    )
    void Update(VaccineOrderRequisitionLineItem item);

    @Select("select * from vaccine_order_requisition_line_items where orderId = #{orderId} order by id")
    @Results(value = {
            @Result(property = "productId", column = "productId"),
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
    })
    List<VaccineOrderRequisitionLineItem> getLineItems(@Param("orderId") Long orderId);


}
