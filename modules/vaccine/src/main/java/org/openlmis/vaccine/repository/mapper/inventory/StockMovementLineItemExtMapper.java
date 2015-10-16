package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.vaccine.domain.inventory.StockMovementLineItemExt;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementLineItemExtMapper {

    @Insert("INSERT INTO stock_movement_line_item_extra_fields(stockmovementlineitemid, issueVoucher, issuedate,toFacilityName, productid, dosesrequested, gap,productCategoryId,quantityOnHand, createdby, createddate)  " +
            "    VALUES (#{stockMovementLineItemId}, #{issueVoucher}, #{issueDate},#{toFacilityName}, #{productId}, #{dosesRequested}, #{gap},#{productCategoryId},#{quantityOnHand}, #{createdBy}, NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer insert(StockMovementLineItemExt lineItemExt);
}
