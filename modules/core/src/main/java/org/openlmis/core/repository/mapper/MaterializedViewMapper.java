package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterializedViewMapper {
    @Select("SELECT refresh_stockouts()")
    Integer refreshStockouts();

    @Select("SELECT refresh_start_carry_view()")
    Integer refreshCarryStartDates();
}
