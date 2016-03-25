package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StockoutViewMapper {
    @Select("SELECT refresh_stockouts()")
    Integer refresh();
}
