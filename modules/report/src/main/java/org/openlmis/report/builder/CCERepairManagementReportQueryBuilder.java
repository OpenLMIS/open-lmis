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
package org.openlmis.report.builder;

import org.openlmis.report.model.params.CCERepairManagementReportParam;
import org.openlmis.report.model.params.CCERepairManagementEquipmentListParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class CCERepairManagementReportQueryBuilder {

    public static String SelectEquipmentCountByStatusEnergySql(Map params) {

        CCERepairManagementReportParam filter = (CCERepairManagementReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        String sql = "";
        String facilityFilter=null;
        BEGIN();
        String aggregateOpen = "SELECT  SUM(agg.Functional) AS Functional " +
                ",SUM(agg.Not_functional) AS Not_Functional " +
                ",SUM(agg.Functional_not_installed) AS Functional_not_Installed " +
                ",SUM(agg.Obsolete) AS Obsolete " +
                ",SUM(agg.Waiting_For_Repair) AS Waiting_For_Repair " +
                ",SUM(agg.Waiting_For_Spare_Parts) AS Waiting_For_Spare_Parts " +
                ",SUM(agg.Electricity) AS Electricity " +
                ",SUM(agg.Solar) AS Solar " +
                ",SUM(agg.Gas) AS Gas " +
                ",SUM(agg.Kerosene) AS Kerosene " +
                "FROM(";

        String allFacilities = "SELECT  " +
                "f.name AS Facility_name " +
                ", g.name AS geographic_zone " +
                ", ft.name AS facility_type " +
                ", \"Functional\" AS Functional " +
                ", \"Functional But Not Installed\" AS Functional_not_installed " +
                ", \"Not Functional\" AS Not_functional " +
                ", \"Obsolete\" AS Obsolete " +
                ", \"Waiting For Repair\" AS Waiting_For_Repair " +
                ", \"Waiting For Spare Parts\" AS Waiting_For_Spare_Parts " +
                ", \"Electricity\" AS Electricity " +
                ", \"Solar\" AS Solar " +
                ", \"Gas\" AS Gas " +
                ", \"Kerosene\" AS Kerosene " +
                ", ws.fid AS facility_id " +
                ", f.typeid as facility_type_id " +
                ", f.geographiczoneid AS geographic_zone_id " +
                ", prog.pid AS program_id " +
                "FROM " +
                "( " +
                "SELECT * " +
                "FROM   crosstab(" +
                "      'SELECT fid, operationalstatus, operationalstatuscount" +
                "       FROM   vw_cce_repair_management" +
                "       ORDER  BY 1,2'" +
                "       ,$$VALUES ('Functional'), ('Not Functional'),('Functional But Not Installed')$$)  " +
                "AS r (fid integer, \"Functional\" bigint, \"Not Functional\" bigint, \"Functional But Not Installed\" bigint) " +
                ") AS ws " +
                "LEFT  JOIN " +
                "(SELECT * " +
                "FROM   crosstab(" +
                "      'SELECT fid, energytype, energytypecount" +
                "       FROM   vw_cce_repair_management" +
                "       ORDER  BY 1,2'" +
                "       ,$$VALUES ('Electricity'), ('Solar'),('Gas'),('Kerosene')$$)  " +
                "AS r (fid integer, \"Electricity\" bigint, \"Solar\" bigint, \"Gas\" bigint, \"Kerosene\" bigint)" +
                ")AS es ON es.fid=ws.fid " +
                "LEFT  JOIN " +
                "(SELECT * " +
                "FROM   crosstab(" +
                "      'SELECT fid, operationalstatus, notfunctionalstatuscount" +
                "       FROM   vw_cce_repair_management_not_functional" +
                "       ORDER  BY 1,2'" +
                "       ,$$VALUES ('Obsolete'), ('Waiting For Repair'),('Waiting For Spare Parts')$$)  " +
                "AS r (fid integer, \"Obsolete\" bigint, \"Waiting For Repair\" bigint, \"Waiting For Spare Parts\" bigint) " +
                ")as notfunctional ON notfunctional.fid=ws.fid " +
                "LEFT OUTER JOIN " +
                "(SELECT pid, fid FROM vw_cce_repair_management GROUP BY pid, fid) as prog ON prog.fid=ws.fid " +
                "LEFT OUTER JOIN " +
                "facilities f ON f.id=ws.fid " +
                "LEFT OUTER JOIN " +
                "geographic_zones g ON g.id=f.geographiczoneid " +
                "LEFT OUTER JOIN " +
                "facility_types ft ON ft.id=f.typeid";

        String aggregateClose = " ) AS agg ";

        if (filter.getProgramId() != 0) {
            if (!filter.getFacilityLevel().isEmpty()) {
                String facilityLevel = filter.getFacilityLevel();
                if(facilityLevel.equalsIgnoreCase("cvs") || facilityLevel.equalsIgnoreCase("rvs") || facilityLevel.equalsIgnoreCase("dvs")) {
                    facilityFilter = " WHERE ws.fid IN(" + filter.getFacilityIds() + ") AND ft.code ='" + facilityLevel + "' ";
                }
                else{
                    facilityFilter = " WHERE ws.fid IN("+filter.getFacilityIds()+") AND ft.code NOT IN ('cvs','rvs','dvs')";
                }

                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            } else {
                 facilityFilter = " WHERE ws.fid IN("+filter.getFacilityIds()+")";
                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            }
        } else {
            sql="";
        }
        return sql;
    }


    public static String EquipmentListSql(Map params) {

        CCERepairManagementEquipmentListParam filter = (CCERepairManagementEquipmentListParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        String sql = "";
        String workingStatus="";
        if(filter.getWorkingStatus().equalsIgnoreCase("functional")){
            workingStatus="functional";
        }
        if(filter.getWorkingStatus().equalsIgnoreCase("not_functional")){
            workingStatus="not functional";
        }
        if(filter.getWorkingStatus().equalsIgnoreCase("functional_not_installed")){
            workingStatus="functional but not installed";
        }

        BEGIN();
       String equipmentSql="SELECT " +
                "  ei.facilityid as facility_id " +
                ", ei.programid as program_id " +
                ", f.typeid as type_id " +
                ", vwd.region_name as region " +
                ", vwd.district_name as district " +
                ", f.name as facility_name " +
                ", ft.name as facility_type " +
                ", eos.name as working_status " +
                ", brkd.break_down as break_down " +
                ", e.manufacturer as manufacturer " +
                ", e.model as model " +
                ", COALESCE(cce.refrigeratorcapacity,0) as capacity "+
                "FROM equipment_inventories ei " +
                "     JOIN equipment_inventory_statuses eis ON eis.id = (( SELECT eisb.id " +
                "           FROM equipment_inventory_statuses eisb " +
                "          WHERE eisb.inventoryid = ei.id " +
                "          ORDER BY eisb.createddate DESC " +
                "         LIMIT 1))" +
                "     JOIN equipment_operational_status eos ON eis.statusid = eos.id" +
                "     JOIN equipments e ON ei.equipmentid = e.id" +
                "     JOIN equipment_cold_chain_equipments cce ON cce.equipmentid = e.id"+
                "     JOIN equipment_types et ON e.equipmenttypeid = et.id" +
                "     JOIN facilities f ON f.id = ei.facilityid" +
                "     JOIN facility_types ft ON ft.id = f.typeid" +
                "     JOIN vw_districts vwd ON vwd.district_id=f.geographiczoneid" +
                "     LEFT JOIN (Select eis.inventoryid as id ,count(eis.id) as break_down from equipment_inventory_statuses eis" +
                "     LEFT JOIN equipment_operational_status eos on eos.id=eis.statusid" +
                "     WHERE eos.name='Not Functional'" +
                "     GROUP BY eis.inventoryid) as brkd ON brkd.id=ei.id" +
                "     LEFT JOIN equipment_energy_types eet ON e.energytypeid = eet.id" +
                "     WHERE et.iscoldchain IS TRUE " +
                "     AND f.id IN ("+filter.getFacilityIds()+") ";

        String program=" AND ei.programid="+filter.getProgramId();
        String status=" AND LOWER(eos.name) IN('"+workingStatus+"') ";
        String level=null;
        String facilityId=" AND f.id="+filter.getFacilityId();

        String facilityLevel = filter.getFacilityLevel();
        sql=equipmentSql;
        if (filter.getProgramId() != 0L) {
            sql=sql+program;
        }
        if (filter.getFacilityId() != 0L) {
            sql=sql+facilityId;
        }
        if(!facilityLevel.isEmpty())
        {
            if(facilityLevel.equalsIgnoreCase("cvs") || facilityLevel.equalsIgnoreCase("rvs") || facilityLevel.equalsIgnoreCase("dvs")) {
                level=" AND ft.code='"+facilityLevel+"' ";
            }
            else{
                level = " AND ft.code NOT IN ('cvs','rvs','dvs') ";
            }
            sql=sql+level;
        }
        if(!workingStatus.isEmpty())
        {
            sql=sql+status;
        }

        return sql;
    }
}
