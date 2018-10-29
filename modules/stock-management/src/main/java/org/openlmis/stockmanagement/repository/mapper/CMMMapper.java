package org.openlmis.stockmanagement.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CMMMapper {

    @Insert("INSERT INTO cmm_entries (productCode, facilityId, periodBegin, periodEnd, cmmValue, createdBy, createdDate, modifiedBy, modifiedDate, stockcardid) " +
            "VALUES " +
            "(#{productCode}, #{facilityId}, #{periodBegin}, #{periodEnd}, #{cmmValue}, #{createdBy}, NOW(), #{modifiedBy}, NOW()," +
            "(SELECT DISTINCT(cards.id) FROM cmm_entries cmm" +
            "  LEFT JOIN stock_cards cards" +
            "    ON cmm.facilityid = cards.facilityid" +
            "       AND (SELECT id FROM products WHERE code = cmm.productcode) = cards.productid" +
            "   WHERE cards.facilityid = #{facilityId} AND cmm.productcode = #{productCode}))")
    @Options(useGeneratedKeys = true)
    void insert(CMMEntry entry);

    @Select("SELECT * FROM cmm_entries WHERE facilityId = #{facilityId} and productCode = #{productCode} and periodBegin = #{periodBegin} and periodEnd = #{periodEnd}")
    CMMEntry getCMMEntryByFacilityAndPeriodAndProductCode(@Param("facilityId") Long facilityId, @Param("productCode") String productCode, @Param("periodBegin") Date periodBegin, @Param("periodEnd") Date periodEnd);

    @Select("SELECT * FROM cmm_entries WHERE facilityId = #{facilityId} and productCode = #{productCode} and periodBegin <= #{day} and periodEnd >= #{day}")
    CMMEntry getCMMEntryByFacilityAndDayAndProductCode(@Param("facilityId") Long facilityId, @Param("productCode") String productCode, @Param("day") Date day);

    @Select("SELECT * FROM cmm_entries " +
            "JOIN facilities on cmm_entries.facilityid = facilities.id " +
            "JOIN geographic_zones zone on facilities.geographiczoneid = zone.id " +
            "WHERE zone.id = #{districtId} and cmm_entries.periodBegin <= #{day} and cmm_entries.periodEnd >= #{day}")
    List<CMMEntry> getCMMEntryByDistrictAndDay(@Param("districtId") Long districtId, @Param("day") Date day);

    @Select("SELECT * FROM cmm_entries " +
            "JOIN facilities on cmm_entries.facilityid = facilities.id " +
            "JOIN geographic_zones zone on facilities.geographiczoneid = zone.id " +
            "JOIN geographic_zones parent_zone on zone.parentid = parent_zone.id " +
            "WHERE parent_zone.id = #{provinceId} and cmm_entries.periodBegin <= #{day} and cmm_entries.periodEnd >= #{day}")
    List<CMMEntry> getCMMEntryByProvinceAndDay(@Param("provinceId") Long provinceId, @Param("day") Date day);

    @Select("SELECT * FROM cmm_entries " +
            "JOIN facilities on cmm_entries.facilityid = facilities.id " +
            "JOIN geographic_zones zone on facilities.geographiczoneid = zone.id " +
            "JOIN geographic_zones parent_zone on zone.parentid = parent_zone.id " +
            "WHERE cmm_entries.periodBegin <= #{day} and cmm_entries.periodEnd >= #{day}")
    List<CMMEntry> getCMMEntryByDay(@Param("day") Date day);

    @Update("UPDATE cmm_entries SET cmmValue = #{cmmValue}, modifieddate= NOW() WHERE id = #{id}")
    void update(CMMEntry entry);
}
