package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.vaccine.domain.inventory.StockMovementLineItem;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementLineItemMapper {

    @Insert("INSERT INTO stock_movement_line_items(" +
            "             stockmovementid, lotid, quantity, notes, createdby, createddate,   " +
            "            modifiedby, modifieddate)  " +
            "    VALUES (#{stockMovementId}, #{lotId}, #{quantity}, #{notes},#{createdBy},NOW() , #{modifiedBy}, NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer Insert(StockMovementLineItem lineItem);

}
