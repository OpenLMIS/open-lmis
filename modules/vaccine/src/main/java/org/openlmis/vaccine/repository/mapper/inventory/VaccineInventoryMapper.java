package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.stockmanagement.domain.Lot;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineInventoryMapper {

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE productid = #{productId} ")
    @Results({
            @Result(property = "lotCode", column = "lotnumber"),
    })
    List<Lot> getLotsByProductId(@Param("productId") Long productId);
}
