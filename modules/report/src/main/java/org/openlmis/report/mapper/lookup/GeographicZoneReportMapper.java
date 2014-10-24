/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.*;
import org.openlmis.report.builder.LabEquipmentStatusByLocationQueryBuilder;
import org.openlmis.report.model.*;
import org.openlmis.report.model.dto.FlatGeographicZone;
import org.openlmis.report.model.dto.GeoZoneTree;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.model.dto.GeographicZoneJsonDto;
import org.openlmis.report.model.geo.GeoFacilityIndicator;
import org.openlmis.report.model.geo.GeoStockStatusFacility;
import org.openlmis.report.model.geo.GeoStockStatusProduct;
import org.openlmis.report.model.geo.GeoStockStatusProductConsumption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneReportMapper {

  @Select("SELECT g.* , p.id as parentId" +
    "   FROM " +
    "       geographic_zones g left join geographic_zones p on g.parentId = p.id order by p.name, g.name")
  List<GeographicZone> getAll();

  @Select("SELECT * FROM geographic_zones gz INNER JOIN geographic_levels gl ON gz.levelId = gl.id " +
    "  where levelId = #{geographicLevelId} ORDER BY gz.id,gl.id")
  List<GeographicZone> getGeographicZoneByLevel(Long id);

  @Select("select gz2.name ADM1,gz1.name ADM2, gz.name ADM3, gz.* from geographic_zones gz " +
        "left join geographic_zones gz1  " +
        "   on gz.parentId = gz1.id " +
        " left join geographic_zones gz2 " +
        "   on gz1.parentId = gz2.id" +
    " order by ADM1, ADM2, ADM3")
  List<FlatGeographicZone> getFlatGeographicZoneList();

  // TODO: refactor this for simplicity,
  // most of this query should move to a respective view.
  @Select("select gzz.id, gzz.name, gjson.geometry,COALESCE(expected.count) expected, COALESCE(total.count) total, COALESCE(ever.count,0) as ever, COALESCE(period.count,0) as period  " +
    " from  " +
        " geographic_zones gzz " +
      " left join  " +
         " geographic_zone_geojson gjson on  " +
          " gzz.id = gjson.zoneId " +
      " left join " +
      " (select geographicZoneId, count(*) from facilities  " +
      " join programs_supported ps on ps.facilityId = facilities.id " +
      " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
      " join requisition_group_members rgm on rgm.facilityId = facilities.id " +
      " join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  " +
      " join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = #{processingPeriodId}  " +
      " where gz.levelId = (select max(id) from geographic_levels) and ps.programId = #{programId} " +
      " group by geographicZoneId" +
      " ) expected " +
      " on gzz.id = expected.geographicZoneId " +
      " left join " +
        " (select geographicZoneId, count(*) from facilities  " +
          " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
          " where gz.levelId = (select max(id) from geographic_levels)  " +
          " group by geographicZoneId" +
        " ) total " +
          " on gzz.id = total.geographicZoneId " +
    " left join  " +
        " (select geographicZoneId, count(*) from facilities  " +
        " join programs_supported ps on ps.facilityId = facilities.id " +
        " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
        " where ps.programId = #{programId} and facilities.id in  " +
          "(select facilityId from requisitions where programId = #{programId} ) " +
            "group by geographicZoneId" +
        " ) ever " +
        " on gzz.id = ever.geographicZoneId " +
     " left join " +
         " (select geographicZoneId, count(*) from facilities  " +
             " join programs_supported ps on ps.facilityId = facilities.id " +
             " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
             " where  ps.programId = #{programId} and facilities.id in  " +
             " (select facilityId from requisitions where periodId = #{processingPeriodId} and programId = #{programId} and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false ) " +
              " group by geographicZoneId" +
    " ) period" +
    " on gzz.id = period.geographicZoneId order by gzz.name" )
  List<GeoZoneReportingRate> getGeoReportingRate(@Param("programId") Long programId, @Param("processingPeriodId") Long processingPeriodId);


  @Select("select f.id, f.name, f.mainPhone, f.longitude, f.latitude, false reported , (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts from facilities f\n" +
            "  join requisition_group_members m on f.id = m.facilityId\n" +
            "  join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
            "  join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
            "where f.id not in (select facilityId from requisitions r where r.programId = #{programId} and r.periodId = #{periodId}) \n" +
            "  and f.enabled = true\n" +
            "  and f.geographicZoneId = #{geographicZoneId}" +
            " order by f.name ")
  List<GeoFacilityIndicator> getNonReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);

  @Select("select rq.id rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true reported, (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts " +
      " from facilities f " +
      " join (select facilityId, r.id from requisitions r where r.programId = #{programId} and r.periodId = #{periodId} and emergency = false and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')) rq on rq.facilityId = f.id " +
    "where  f.enabled = true\n" +
    " and f.geographicZoneId = #{geographicZoneId}" +
    " order by f.name")
  List<GeoFacilityIndicator> getReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);





  @Select("select * from geographic_zones where parentId is null")
  GeoZoneTree getParentZoneTree();

  // NO MORE USED
  // This does not pay attention to user's permission and program permission.
  @Select("select * from geographic_zones where id = #{zoneId}")
  GeoZoneTree getGeographicZoneById(int zoneId);

  @Select("select * from geographic_zones where parentId = #{parentId} order by name")
  List<GeoZoneTree> getChildrenZoneTree(int parentId);

  @Select("select distinct gz.* from geographic_zones gz " +
                                " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.program_id = #{programId} and vud.user_id = #{userId}) sq" +
                                " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
  List<GeoZoneTree> getGeoZonesForUserByProgram( @Param("userId") Long userId, @Param("programId")Long programId);


  @Select("select distinct gz.* from geographic_zones gz " +
      " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.user_id = #{userId}) sq" +
      " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
  List<GeoZoneTree> getGeoZonesForUser( @Param("userId") Long userId);



  @Select("WITH  recursive  userGeographicZonesRec AS \n" +
          "(SELECT *\n" +
          "FROM geographic_zones \n" +
          "WHERE id in  (Select geographiczoneid from vw_user_geographic_zones where userid = #{userId}  " +
          "and case when COALESCE(#{programId},0) > 0 THEN programId = #{programId} ELSE programId = programId END ) \n" +
          "UNION \n" +
          "SELECT sn.* \n" +
          "FROM geographic_zones sn \n" +
          "JOIN userGeographicZonesRec \n" +
          "ON sn.id = userGeographicZonesRec.parentId )\n" +
          "SELECT * from geographic_zones gz\n" +
          "INNER JOIN userGeographicZonesRec gzRec on gz.id = gzRec.id\n" +
          "WHERE gz.parentId = #{parentId} order by gz.name\n")
  List<GeoZoneTree> getUserGeographicZoneChildren(@Param("programId") Long programId, @Param("parentId")int parentId, @Param("userId")Long userId);

    @Select("   SELECT  "  +
            "   	gzz. ID,  "  +
            "   	gzz. NAME,  "  +
            "       fn_get_parent_geographiczone(gzz.ID,1) georegion, " +
            "       fn_get_parent_geographiczone(gzz.ID,2) geozone, " +
            "   	gjson.geometry,  "  +
            "   	COALESCE (period. COUNT) period,  "  +
            "   	COALESCE (total. COUNT) total,  "  +
            "   	COALESCE (expected. COUNT, 0) AS expected,  "  +
            "   	COALESCE (ever. COUNT, 0) AS ever,  "  +
            "   	COALESCE (stockedout. COUNT, 0) AS stockedout,  "  +
            "   	COALESCE (understocked. COUNT, 0) AS understocked,  "  +
            "   	COALESCE (overstocked. COUNT, 0) AS overstocked,  "  +
            "   	COALESCE (adequatelystocked. COUNT, 0) AS adequatelystocked,  "  +
            "   	COALESCE (stockedoutprev. COUNT, 0) AS stockedoutprev,  "  +
            "   	COALESCE (understockedprev. COUNT, 0) AS understockedprev,  "  +
            "   	COALESCE (overstockedprev. COUNT, 0) AS overstockedprev,  "  +
            "   	COALESCE (adequatelystockedprev. COUNT,	0) AS adequatelystockedprev  "  +
            "   FROM  "  +
            "   	geographic_zones gzz  "  +
            "   LEFT JOIN geographic_zone_geojson gjson ON gzz. ID = gjson.zoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		facilities  "  +
            "   	JOIN programs_supported ps ON ps.facilityId = facilities. ID  "  +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  "  +
            "   	JOIN requisition_group_members rgm ON rgm.facilityId = facilities. ID  "  +
            "   	JOIN requisition_group_program_schedules rgps ON rgps.requisitionGroupId = rgm.requisitionGroupId  "  +
            "   	AND rgps.programId = ps.programId  "  +
            "   	JOIN processing_periods pp ON pp.scheduleId = rgps.scheduleId  "  +
            "   	AND pp. ID = #{processingPeriodId}  "  +
            "   	WHERE  "  +
            "   		gz.levelId = (  "  +
            "   			SELECT  "  +
            "   				MAX (ID)  "  +
            "   			FROM  "  +
            "   				geographic_levels  "  +
            "   		)  "  +
            "   	AND ps.programid = #{programId}  "  +
            "   	GROUP BY  "  +
            "   		geographicZoneId  "  +
            "   ) expected ON gzz. ID = expected.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		facilities  "  +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  "  +
            "   	WHERE  "  +
            "   		gz.levelId = (  "  +
            "   			SELECT  "  +
            "   				MAX (ID)  "  +
            "   			FROM  "  +
            "   				geographic_levels  "  +
            "   		)  "  +
            "   	GROUP BY  "  +
            "   		geographicZoneId  "  +
            "   ) total ON gzz. ID = total.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		facilities  "  +
            "   	JOIN programs_supported ps ON ps.facilityId = facilities. ID  "  +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  "  +
            "   	WHERE  "  +
            "   		ps.programid = #{programId}  "  +
            "   	AND facilities. ID IN (  "  +
            "   		SELECT  "  +
            "   			facilityId  "  +
            "   		FROM  "  +
            "   			requisitions  "  +
            "   		WHERE  "  +
            "   			periodId = #{processingPeriodId}  "  +
            "   		AND programid = #{programId}  "  +
            "   	)  "  +
            "   	GROUP BY  "  +
            "   		geographicZoneId  "  +
            "   ) period ON gzz. ID = period.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = #{processingPeriodId}  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'SO'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) stockedout ON gzz. ID = stockedout.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'SO'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) ever ON gzz. ID = ever.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = #{processingPeriodId}  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'US'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) understocked ON gzz. ID = understocked.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = #{processingPeriodId}  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'OS'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) overstocked ON gzz. ID = overstocked.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = #{processingPeriodId}  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'SP'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) adequatelystocked ON gzz. ID = adequatelystocked.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = (  "  +
            "   			SELECT  "  +
            "   				COALESCE (MAX(periodid), 0)  "  +
            "   			FROM  "  +
            "   				requisitions  "  +
            "   			WHERE  "  +
            "   				programid = #{programId}  "  +
            "   			AND periodId < #{processingPeriodId}  "  +
            "   		)  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'SO'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) stockedoutprev ON gzz. ID = stockedoutprev.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = (  "  +
            "   			SELECT  "  +
            "   				COALESCE (MAX(periodid), 0)  "  +
            "   			FROM  "  +
            "   				requisitions  "  +
            "   			WHERE  "  +
            "   				programid = #{programId}  "  +
            "   			AND periodId < #{processingPeriodId}  "  +
            "   		)  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'US'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) understockedprev ON gzz. ID = understockedprev.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = (  "  +
            "   			SELECT  "  +
            "   				COALESCE (MAX(periodid), 0)  "  +
            "   			FROM  "  +
            "   				requisitions  "  +
            "   			WHERE  "  +
            "   				programid = #{programId}  "  +
            "   			AND periodId < #{processingPeriodId}  "  +
            "   		)  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'OS'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) overstockedprev ON gzz. ID = overstockedprev.geographicZoneId  "  +
            "   LEFT JOIN (  "  +
            "   	SELECT  "  +
            "   		gz_id geographicZoneId,  "  +
            "   		COUNT (*)  "  +
            "   	FROM  "  +
            "   		vw_stock_status_2  "  +
            "   	WHERE  "  +
            "   		periodId = (  "  +
            "   			SELECT  "  +
            "   				COALESCE (MAX(periodid), 0)  "  +
            "   			FROM  "  +
            "   				requisitions  "  +
            "   			WHERE  "  +
            "   				programid = #{programId}  "  +
            "   			AND periodId < #{processingPeriodId}  "  +
            "   		)  "  +
            "   	AND programid = #{programId}  "  +
            "   	AND productId = #{productId}  "  +
            "   	AND status = 'SP'  "  +
            "   	GROUP BY  "  +
            "   		gz_id  "  +
            "   ) adequatelystockedprev ON gzz. ID = adequatelystockedprev.geographicZoneId  "  +
            "   ORDER BY  "  +
            "   	gzz. NAME  ")
  List<GeoStockStatusFacilitySummary> getGeoStockStatusFacilitySummary(@Param("programId") Long programId, @Param("processingPeriodId") Long processingPeriodId, @Param("productId") Long productId);


    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true stockedout, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} AND ss.status = 'SO' order by f.name")

    List<GeoStockStatusFacility> getStockedOutFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true understocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} AND ss.status = 'US' order by f.name")

    List<GeoStockStatusFacility> getUnderStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

   @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true overstocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
    " ss.product, ss.amc, ss.stockinhand, ss.mos " +
    " FROM vw_stock_status_2 ss " +
    " INNER JOIN facilities f ON f.id = ss.facility_id " +
    " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} AND ss.status = 'OS' order by f.name")


    List<GeoStockStatusFacility> getOverStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true adequatelystocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} AND ss.status = 'SP' order by f.name")

    List<GeoStockStatusFacility> getAdequatelyStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);


    @Select("   SELECT p.id, p.code,p.primaryname, COALESCE(reported.count) AS reported, COALESCE(stockedout.count) AS stockedout,COALESCE(understocked.count) AS understocked,  "  +
            "   COALESCE(overstocked.count) AS overstocked, COALESCE(adequatelystocked.count) AS adequatelystocked  "  +
            "   FROM public.products AS p  "  +
            "    LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) group by productid " +
            "    ) AS reported ON p.id = reported.productid " +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and status = 'SO' group by productid  "  +
            "   ) AS stockedout ON p.id = stockedout.productid  "  +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and status = 'US' group by productid  "  +
            "   ) AS understocked ON p.id = understocked.productid  "  +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and status = 'US' group by productid  "  +
            "   ) AS overstocked ON p.id = overstocked.productid  "  +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and status = 'SP' group by productid  "  +
            "   ) AS adequatelystocked ON p.id = adequatelystocked.productid  "  +
            "   INNER JOIN public.program_products ON p.id = public.program_products.productid  "  +
            "   where programid = 1 and p.active  = true  "  +
            "   and ((COALESCE(stockedout.count) is not null) or (COALESCE(understocked.count) is not null) or (COALESCE(overstocked.count) is not null)   "  +
            "        or ((COALESCE(adequatelystocked.count) is not null)))  ")

    List<GeoStockStatusProductSummary> getStockStatusProductSummary(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);



     @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true stockedout, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) AND ss.status = 'SO' order by f.name")

    List<GeoStockStatusProduct> getStockedOutProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true understocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) AND ss.status = 'US' order by f.name")

    List<GeoStockStatusProduct> getUnderStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true overstocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) AND ss.status = 'OS' order by f.name")

    List<GeoStockStatusProduct> getOverStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true adequatelystocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) AND ss.status = 'SP' order by f.name")

    List<GeoStockStatusProduct> getAdequatelyStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

   //@Select("select facility_id, facility_code, facility_name, serial_number, equipment_name, equipment_status, longitude, latitude from vw_lab_equipment_status")
   @SelectProvider(type=LabEquipmentStatusByLocationQueryBuilder.class, method="getFacilitiesEquipmentsData")
    List<GeoZoneEquipmentStatus> getFacilitiesEquipments(@Param("program") Long program,
                                                         @Param("zone") Long zone,
                                                         @Param("facilityType") Long facilityType,
                                                         @Param("facility") Long facility,
                                                         @Param("equipmentType") Long equipmentType,
                                                         @Param("userId")Long userId,
                                                         @Param("equipment")Long equipment);


    @SelectProvider(type=LabEquipmentStatusByLocationQueryBuilder.class, method="getFacilityEquipmentStatusGeoData")
    List<GeoZoneEquipmentStatus> getFacilityEquipmentStatusGeo2(@Param("program") Long program,
                                                                @Param("zone") Long zone,
                                                                @Param("facilityType") Long facilityType,
                                                                @Param("facility") Long facility,
                                                                @Param("equipmentType") Long equipmentType,
                                                                @Param("userId")Long userId,
                                                                @Param("equipment")Long equipment

    );

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Not Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithNonOperationalEquipments();

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Fully Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithFullyOperationalEquipments();

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Partially Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithPartiallyOperationalEquipments();


    @SelectProvider(type=LabEquipmentStatusByLocationQueryBuilder.class, method="getFacilitiesByEquipmentStatus")
    List<GeoZoneEquipmentStatus> getFacilitiesByEquipmentOperationalStatus(@Param("program") Long program,
                                                                           @Param("zone") Long zone,
                                                                           @Param("facilityType") Long facilityType,
                                                                           @Param("facility") Long facility,
                                                                           @Param("equipmentType") Long equipmentType,
                                                                           @Param("userId")Long userId,
                                                                           @Param("status") String status,
                                                                           @Param("equipment")Long equipment);


    @SelectProvider(type=LabEquipmentStatusByLocationQueryBuilder.class, method="getFacilityEquipmentStatusGeoSummaryData")
    List<GeoZoneEquipmentStatusSummary> getFacilitiesEquipmentStatusSummary( @Param("program") Long program,
                                                                             @Param("zone") Long zone,
                                                                             @Param("facilityType") Long facilityType,
                                                                             @Param("facility") Long facility,
                                                                             @Param("equipmentType") Long equipmentType,
                                                                             @Param("userId")Long userId,
                                                                             @Param("equipment")Long equipment);

    @Select("   select productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc from fn_getstockstatusgraphdata(#{programId}::int,#{geographicZoneId}::int,#{periodId}::int,#{productId}); ")

    List<GeoStockStatusProductConsumption> getStockStatusProductConsumption(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("geographicZoneId") Long geographicZoneId, @Param("productId") String ProductIds);

    @Select("select * from geographic_zone_geojson")
    List<GeographicZoneJsonDto> getGeoZoneGeometryJson();
}
