package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CMMMapper {

    @Insert("INSERT INTO cmm_entries (productCode, facilityId, periodBegin, periodEnd, cmmValue, createdBy, createdDate, modifiedBy, modifiedDate) " +
            "VALUES " +
            "(#{productCode}, #{facilityId}, #{periodBegin}, #{periodEnd}, #{cmmValue}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
    @Options(useGeneratedKeys = true)
    void insert(CMMEntry entry);

    @Select("SELECT * FROM cmm_entries WHERE facilityId = #{facilityId} and productCode = #{productCode} and periodBegin = #{periodBegin} and periodEnd = #{periodEnd}")
    CMMEntry getCMMEntryByFacilityAndPeriodAndProductCode(@Param("facilityId") Long facilityId, @Param("productCode") String productCode, @Param("periodBegin") Date periodBegin, @Param("periodEnd") Date periodEnd);

    @Select("SELECT * FROM cmm_entries WHERE facilityId = #{facilityId} and productCode = #{productCode} and periodBegin <= #{day} and periodEnd >= #{day}")
    CMMEntry getCMMEntryByFacilityAndDayAndProductCode(@Param("facilityId") Long facilityId, @Param("productCode") String productCode, @Param("day") Date day);

    @Update("UPDATE cmm_entries SET cmmValue = #{cmmValue} WHERE id = #{id}")
    void update(CMMEntry entry);
}
