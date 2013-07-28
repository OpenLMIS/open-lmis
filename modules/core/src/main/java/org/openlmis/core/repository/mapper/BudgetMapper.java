package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.core.domain.Budget;
import org.springframework.stereotype.Repository;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 3:47 PM
 */
@Repository
public interface BudgetMapper {

    @Insert("Insert into budgets(code, name, description, gln, mainPhone, fax, address1, address2, " +
            "geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById," +
            "coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, online," +
            "satellite, satelliteParentId, hasElectricity, hasElectronicScc, hasElectronicDar, active," +
            "goLiveDate, goDownDate, comment, dataReportable, createdDate,createdBy, modifiedBy, modifiedDate) " +
            "values(#{code}, #{name}, #{description}, #{gln}, #{mainPhone}, #{fax}, #{address1}, #{address2}," +
            "#{geographicZone.id}," +
            "#{facilityType.id}," +
            "#{catchmentPopulation}, #{latitude}, #{longitude}, #{altitude}," +
            "#{operatedBy.id}," +
            "#{coldStorageGrossCapacity}, #{coldStorageNetCapacity}, #{suppliesOthers}, #{sdp},#{online}," +
            "#{satellite}, #{satelliteParentId}, #{hasElectricity}, #{hasElectronicScc}, #{hasElectronicDar}, #{active}," +
            "#{goLiveDate}, #{goDownDate}, #{comment}, #{dataReportable},COALESCE(#{createdDate}, NOW()), #{createdBy}, #{modifiedBy}, " +
            "COALESCE(#{modifiedDate}, NOW()))")
    @Options(useGeneratedKeys = true)
    Integer insert(Budget budget);

}
