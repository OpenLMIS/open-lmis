package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterializedViewMapper {
    @Select("SELECT refresh_stockouts()")
    Integer refreshStockouts();

    @Select("SELECT refresh_start_carry_view()")
    Integer refreshCarryStartDates();

    @Select("SELECT refresh_weekly_tracer_soh()")
    Integer refreshWeeklyTracerSOH();

    @Select("SELECT refresh_period_movements()")
    Integer refreshPeriodMovements();

    @Select("SELECT refresh_daily_full_soh();")
    Integer refreshDailyFullSoh();
}
