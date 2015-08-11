/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
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
public interface RMNCHStatusReportMapper {

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


  @Select("select f.id, f.name, f.mainPhone, f.longitude, f.latitude, false reported , (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts," +
              " ( SELECT count(*) >0 \n" +
              "  FROM role_assignments\n" +
              "  JOIN supervisory_nodes on supervisory_nodes.id = role_assignments.supervisorynodeid\n" +
              "  JOIN users on users.id = role_assignments.userid AND users.active = true\n" +
              "  WHERE supervisory_nodes.facilityid = f.id" +
              " ) as hasSupervisors" +
          " from facilities f\n" +
            "  join requisition_group_members m on f.id = m.facilityId\n" +
            "  join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
            "  join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
            " where f.id not in (select facilityId from requisitions r where r.programId = #{programId} and r.periodId = #{periodId}) \n" +
            "  and f.enabled = true\n" +
            "  and f.geographicZoneId = #{geographicZoneId}" +
            " order by f.name ")
  List<GeoFacilityIndicator> getNonReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);

  @Select("select rq.id rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true reported, (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
          " ( SELECT count(*) >0 \n" +
          "  FROM role_assignments\n" +
          "  JOIN supervisory_nodes on supervisory_nodes.id = role_assignments.supervisorynodeid\n" +
          "  JOIN users on users.id = role_assignments.userid AND users.active = true\n" +
          "  WHERE supervisory_nodes.facilityid = f.id" +
          "  ) as hasSupervisors" +
      " from facilities f " +
      " join (select facilityId, r.id from requisitions r where r.programId = #{programId} and r.periodId = #{periodId} and emergency = false and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')) rq on rq.facilityId = f.id " +
    " where  f.enabled = true\n" +
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
  List<GeoZoneTree> getGeoZonesForUserByProgram(@Param("userId") Long userId, @Param("programId") Long programId);


  @Select("select distinct gz.* from geographic_zones gz " +
      " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.user_id = #{userId}) sq" +
      " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
  List<GeoZoneTree> getGeoZonesForUser(@Param("userId") Long userId);



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
  List<GeoZoneTree> getUserGeographicZoneChildren(@Param("programId") Long programId, @Param("parentId") int parentId, @Param("userId") Long userId);

    @Select("SELECT  \n" +
            "gzz. ID,  \n" +
            "gzz. NAME,  \n" +
            "   fn_get_parent_geographiczone(gzz.ID,1) georegion, \n" +
            "   fn_get_parent_geographiczone(gzz.ID,2) geozone, \n" +
            "gjson.geometry,  \n" +
            "COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0) period,  \n" +
            "COALESCE (total. COUNT) total,  \n" +
            "COALESCE (expected. COUNT, 0) AS expected,  \n" +
            "COALESCE (ever. COUNT, 0) AS ever,  \n" +
            "COALESCE (stockedout. COUNT, 0) AS stockedout,  \n" +
            "COALESCE (understocked. COUNT, 0) AS understocked,  \n" +
            "COALESCE (overstocked. COUNT, 0) AS overstocked,  \n" +
            "COALESCE (adequatelystocked. COUNT, 0) AS adequatelystocked,  \n" +
            "COALESCE (stockedoutprev. COUNT, 0) AS stockedoutprev,  \n" +
            "COALESCE (understockedprev. COUNT, 0) AS understockedprev,  \n" +
            "COALESCE (overstockedprev. COUNT, 0) AS overstockedprev,  \n" +
            "COALESCE (adequatelystockedprev. COUNT,\t0) AS adequatelystockedprev  \n" +
            "FROM  \n" +
            "geographic_zones gzz  \n" +
            "LEFT JOIN geographic_zone_geojson gjson ON gzz. ID = gjson.zoneId  \n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "LEFT JOIN (  \n" +
            "SELECT  \n" +
            "\tgeographicZoneId,  \n" +
            "\tCOUNT (*)  \n" +
            "FROM  \n" +
            "\tfacilities  \n" +
            "JOIN programs_supported ps ON ps.facilityId = facilities. ID  \n" +
            "JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  \n" +
            "JOIN requisition_group_members rgm ON rgm.facilityId = facilities. ID  \n" +
            "JOIN requisition_group_program_schedules rgps ON rgps.requisitionGroupId = rgm.requisitionGroupId  \n" +
            "AND rgps.programId = ps.programId  \n" +
            "JOIN processing_periods pp ON pp.scheduleId = rgps.scheduleId  \n" +
            "AND pp. ID = #{processingPeriodId}  \n" +
            "WHERE  \n" +
            "\tgz.levelId = (  \n" +
            "\t\tSELECT  \n" +
            "\t\t\tMAX (ID)  \n" +
            "\t\tFROM  \n" +
            "\t\t\tgeographic_levels  \n" +
            "\t)  \n" +
            "GROUP BY  \n" +
            "\tgeographicZoneId  \n" +
            ") expected ON gzz. ID = expected.geographicZoneId            \n" +
            "\n" +
            "\n" +
            "LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "facilities  \n" +
            "JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  \n" +
            "WHERE  \n" +
            "gz.levelId = (  \n" +
            "\tSELECT  \n" +
            "\t\tMAX (ID)  \n" +
            "\tFROM  \n" +
            "\t\tgeographic_levels  \n" +
            ")  \n" +
            "GROUP BY  \n" +
            "geographicZoneId  \n" +
            ") total ON gzz. ID = total.geographicZoneId  \n" +
            "\n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT      \n" +
            "geographicZoneId,      \n" +
            "COUNT (*)      \n" +
            "FROM      \n" +
            "facilities              \t    \n" +
            "WHERE facilities. ID IN (      \n" +
            "SELECT      \n" +
            "facilityId      \n" +
            "FROM      \n" +
            "dw_orders      \n" +
            "WHERE rmnch = true    \n" +
            "AND periodId = 7    \n" +
            ")      \n" +
            "GROUP BY geographicZoneId    \n" +
            "   ) period ON gzz. ID = period.geographicZoneId  \n" +
            "\n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodId = #{ processingPeriodId }  \n" +
            "AND productId = #{ productId }  \n" +
            "AND stocking = 'S'  \n" +
            "GROUP BY geographicZoneId  \n" +
            "   ) stockedout ON gzz. ID = stockedout.geographicZoneId  \n" +
            "\n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND productId = #{ productId }  \n" +
            "AND stocking = 'S'  \n" +
            "GROUP BY geographicZoneId  \n" +
            "   ) ever ON gzz. ID = ever.geographicZoneId  \n" +
            "\n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodId = #{ processingPeriodId }  \n" +
            "AND productId = #{ productId }  \n" +
            "AND stocking = 'U'  \n" +
            "GROUP BY geographicZoneId  \n" +
            "   ) understocked ON gzz. ID = understocked.geographicZoneId  \n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodId = #{ processingPeriodId }  \n" +
            "AND productId = #{ productId }  \n" +
            "AND stocking = 'O'  \n" +
            "GROUP BY geographicZoneId  \n" +
            "   ) overstocked ON gzz. ID = overstocked.geographicZoneId             \n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodId = #{ processingPeriodId }  \n" +
            "AND productId = #{ productId }  \n" +
            "AND stocking = 'A'  \n" +
            "GROUP BY geographicZoneId  \n" +
            "   ) adequatelystocked ON gzz. ID = adequatelystocked.geographicZoneId             \n" +
            "\n" +
            "LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "INNER JOIN processing_periods ON dw_orders.periodid = processing_periods. ID  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodid = ( SELECT ID FROM processing_periods   \n" +
            "\t\t   WHERE startdate < (SELECT startdate FROM processing_periods WHERE ID = #{processingPeriodId} ) ORDER BY startdate DESC LIMIT 1  \n" +
            ")  \n" +
            "AND productId = #{productId}  \n" +
            "AND stocking = 'S'  \n" +
            "GROUP BY geographiczoneid  \n" +
            ") stockedoutprev ON gzz. ID = stockedoutprev.geographicZoneId    \n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "INNER JOIN processing_periods ON dw_orders.periodid = processing_periods. ID  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodid = ( SELECT ID FROM processing_periods   \n" +
            "\t\t   WHERE startdate < (SELECT startdate FROM processing_periods WHERE ID = #{processingPeriodId} ) ORDER BY startdate DESC LIMIT 1  \n" +
            ")  \n" +
            "AND productId = #{productId}  \n" +
            "AND stocking = 'U'  \n" +
            "GROUP BY geographiczoneid  \n" +
            "   ) understockedprev ON gzz. ID = understockedprev.geographicZoneId             \n" +
            "\n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "INNER JOIN processing_periods ON dw_orders.periodid = processing_periods. ID  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodid = ( SELECT ID FROM processing_periods   \n" +
            "\t\t   WHERE startdate < (SELECT startdate FROM processing_periods WHERE ID = #{processingPeriodId} ) ORDER BY startdate DESC LIMIT 1  \n" +
            ")  \n" +
            "AND productId = #{productId}  \n" +
            "AND stocking = 'O'  \n" +
            "GROUP BY geographiczoneid  \n" +
            "   ) overstockedprev ON gzz. ID = overstockedprev.geographicZoneId          \n" +
            "   LEFT JOIN (  \n" +
            "SELECT  \n" +
            "geographicZoneId,  \n" +
            "COUNT (*)  \n" +
            "FROM  \n" +
            "dw_orders  \n" +
            "INNER JOIN processing_periods ON dw_orders.periodid = processing_periods. ID  \n" +
            "WHERE  \n" +
            "rmnch = TRUE  \n" +
            "AND periodid = ( SELECT ID FROM processing_periods   \n" +
            "\t\t   WHERE startdate < (SELECT startdate FROM processing_periods WHERE ID = #{processingPeriodId} ) ORDER BY startdate DESC LIMIT 1  \n" +
            ")  \n" +
            "AND productId = #{productId}  \n" +
            "AND stocking = 'A'  \n" +
            "GROUP BY geographiczoneid  \n" +
            "   ) adequatelystockedprev ON gzz. ID = adequatelystockedprev.geographicZoneId  \n" +
            "   ORDER BY  \n" +
            "\tgzz. NAME  ")
  List<GeoStockStatusFacilitySummary> getGeoStockStatusFacilitySummary(@Param("processingPeriodId") Long processingPeriodId, @Param("productId") Long productId);


    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true stockedout, \n" +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
            " ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
            " FROM dw_orders ss \n" +
            " INNER JOIN facilities f ON f.id = ss.facilityid \n" +
            " WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.geographiczoneid = #{geographicZoneId}  AND ss.stocking = 'S' order by f.name")

    List<GeoStockStatusFacility> getStockedOutFacilities(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true understocked,\n" +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts,\n" +
            " ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos\n" +
            " FROM dw_orders ss\n" +
            " INNER JOIN facilities f ON f.id = ss.facilityid\n" +
            " WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.geographiczoneid = #{geographicZoneId}  AND ss.stocking = 'U' order by f.name")

    List<GeoStockStatusFacility> getUnderStockedFacilities(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

   @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true overstocked, \n" +
           " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
           " ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
           " FROM dw_orders ss \n" +
           " INNER JOIN facilities f ON f.id = ss.facilityid \n" +
           " WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.geographiczoneid = #{geographicZoneId} AND ss.stocking = 'O' order by f.name")
    List<GeoStockStatusFacility> getOverStockedFacilities(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true adequatelystocked, \n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
            "ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
            "FROM dw_orders ss \n" +
            "INNER JOIN facilities f ON f.id = ss.facilityid \n" +
            "WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.geographiczoneid = #{geographicZoneId}  AND ss.stocking = 'A' order by f.name")

    List<GeoStockStatusFacility> getAdequatelyStockedFacilities(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);


    @Select("SELECT p.id, p.code,p.primaryname,  COALESCE(stockedout.count,0) AS stockedout,COALESCE(understocked.count,0) AS understocked,  \n" +
            "COALESCE(overstocked.count,0) AS overstocked, COALESCE(adequatelystocked.count,0) AS adequatelystocked,   \n" +
            "COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0) reported  \n" +
            "FROM public.products AS p   \n" +
            "LEFT JOIN ( select productid, count(*) from dw_orders where rmnch = true and periodId = #{periodId}  AND (geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0)  group by productid \n" +
            ") AS reported ON p.id = reported.productid \n" +
            "LEFT JOIN ( select productid, count(*) from dw_orders where rmnch = true and periodId = #{periodId}   AND (geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0)   and stocking = 'S' group by productid   \n" +
            ") AS stockedout ON p.id = stockedout.productid   \n" +
            "LEFT JOIN ( select productid, count(*) from dw_orders where rmnch = true and  periodId = #{periodId}  AND (geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0)   and stocking = 'U' group by productid   \n" +
            ") AS understocked ON p.id = understocked.productid   \n" +
            "LEFT JOIN ( select productid, count(*) from dw_orders where rmnch = true and  periodId = #{periodId}  AND (geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0)   and stocking = 'O' group by productid   \n" +
            ") AS overstocked ON p.id = overstocked.productid   \n" +
            "LEFT JOIN ( select productid, count(*) from dw_orders where rmnch = true and  periodId = #{periodId}  AND (geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0)   and stocking = 'A' group by productid   \n" +
            ") AS adequatelystocked ON p.id = adequatelystocked.productid   \n" +
            "INNER JOIN public.program_products ON p.id = public.program_products.productid   \n" +
            "where programid = (select id from programs where lower(code) = 'rmnch' ) and p.active  = true   \n" +
            "and COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0)  > 0 ")

    List<GeoStockStatusProductSummary> getStockStatusProductSummary(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);



     @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, ss.geographiczonename, true stockedout, \n" +
             "(select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
             "ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
             "FROM dw_orders ss \n" +
             "INNER JOIN facilities f ON f.id = ss.facilityid \n" +
             "WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.stocking = 'S' order by f.name")

    List<GeoStockStatusProduct> getStockedOutProducts(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, ss.geographiczonename, true understocked, \n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
            "ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
            "FROM dw_orders ss \n" +
            "INNER JOIN facilities f ON f.id = ss.facilityid \n" +
            "WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.stocking = 'U' order by f.name")

    List<GeoStockStatusProduct> getUnderStockedProducts(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, ss.geographiczonename, true overstocked, \n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
            "ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
            "FROM dw_orders ss \n" +
            "INNER JOIN facilities f ON f.id = ss.facilityid \n" +
            "WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.stocking = 'O' order by f.name")

    List<GeoStockStatusProduct> getOverStockedProducts(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, ss.geographiczonename, true adequatelystocked, \n" +
            "(select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
            "ss.productprimaryname product, ss.amc, ss.soh stockinhand, ss.mos \n" +
            "FROM dw_orders ss \n" +
            "INNER JOIN facilities f ON f.id = ss.facilityid \n" +
            "WHERE ss.rmnch = true AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.geographiczoneid = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.stocking = 'A' order by f.name")

    List<GeoStockStatusProduct> getAdequatelyStockedProducts(@Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);


    @Select("select productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc from fn_get_rmnch_stock_status_data(#{geographicZoneId}::int,#{periodId}::int,#{productId}); ")
    List<GeoStockStatusProductConsumption> getStockStatusProductConsumption(@Param("periodId") Long periodId, @Param("geographicZoneId") Long geographicZoneId, @Param("productId") String ProductIds);

    @Select("select * from geographic_zone_geojson")
    List<GeographicZoneJsonDto> getGeoZoneGeometryJson();
}
