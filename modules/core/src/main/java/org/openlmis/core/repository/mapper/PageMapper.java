package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PageMapper {

    @Select("SELECT count(1) FROM ${tableName} WHERE modifieddate > #{fromStartDate}")
    Integer getPageInfo(@Param(value = "tableName") String tableName, @Param(value = "fromStartDate") Date fromStartDate);
}
