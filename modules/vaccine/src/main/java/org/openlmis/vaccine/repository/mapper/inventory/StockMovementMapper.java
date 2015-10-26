package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.vaccine.domain.inventory.StockMovement;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementMapper {

    @Insert("INSERT INTO stock_movements(type, fromfacilityid, tofacilityid, initiateddate,shippeddate,expecteddate,receiveddate,createdby,createddate,modifiedby,modifieddate) " +
            "    VALUES (#{type}::StockMovementType,#{fromFacilityId}, #{toFacilityId}, #{initiatedDate}, #{shippedDate},#{expectedDate},#{receivedDate},#{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer Insert(StockMovement stockMovement);

    @Update("UPDATE stock_movements  " +
            "   SET type=#{type}, fromfacilityid=#{fromFacilityId}, tofacilityid=#{toFacilityId}, initiateddate=#{initiatedDate},   " +
            "       modifiedby=#{modifiedBy}, modifieddate=#{modifiedDate}  " +
            " WHERE id = #{ID} ")
    Integer update(StockMovement stockMovement);

    @Select("select * from stock_movements order by ID DESC LIMIT 1 ")
    StockMovement getLastInsertedStock();

}
