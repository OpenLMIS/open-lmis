/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.params.RepairManagementEquipmentListParam;
import org.openlmis.report.model.params.RepairManagementReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class RepairManagementReportQueryBuilder {

    public static String SelectEquipmentCountByStatusEnergySql(Map params) {

        RepairManagementReportParam filter = (RepairManagementReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        String sql = "";

        BEGIN();
        String aggregateOpen = "SELECT  sum(agg.Functional) AS Functional\n" +
                ",sum(agg.Not_functional) AS Not_Functional\n" +
                ",sum(agg.Functional_not_installed) AS Functional_not_Installed\n" +
                ",sum(agg.Obsolete) AS Obsolete\n" +
                ",sum(agg.Waiting_For_Repair) AS Waiting_For_Repair\n" +
                ",sum(agg.Waiting_For_Spare_Parts) AS Waiting_For_Spare_Parts\n" +
                ",sum(agg.Electricity) AS Electricity\n" +
                ",sum(agg.Solar) AS Solar\n" +
                ",sum(agg.Gas) AS Gas\n" +
                ",sum(agg.Kerosene) AS Kerosene\n" +
                "FROM(";

        String allFacilities = "SELECT \n" +
                "  f.name AS Facility_name\n" +
                ", g.name AS geographic_zone\n" +
                ", ft.name AS facility_type\n" +
                ", \"Functional\" AS Functional\n" +
                ", \"Functional But Not Installed\" AS Functional_not_installed\n" +
                ", \"Not Functional\" AS Not_functional\n" +
                ", \"Obsolete\" AS Obsolete\n" +
                ", \"Waiting For Repair\" AS Waiting_For_Repair\n" +
                ", \"Waiting For Spare Parts\" AS Waiting_For_Spare_Parts\n" +
                ", \"Electricity\" AS Electricity  \n" +
                ", \"Solar\" AS Solar\n" +
                ", \"Gas\" AS Gas\n" +
                ", \"Kerosene\" AS Kerosene\n" +
                ", ws.fid AS facility_id\n" +
                ", f.typeid as facility_type_id\n" +
                ", f.geographiczoneid AS geographic_zone_id\n" +
                ", prog.pid AS program_id\n" +
                "FROM\n" +
                "(\n" +
                "\n" +
                "SELECT *\n" +
                "FROM   crosstab(\n" +
                "      'SELECT fid, operationalstatus, operationalstatuscount\n" +
                "       FROM   vw_repair_management\n" +
                "       ORDER  BY 1,2'\n" +
                "       ,$$VALUES ('Functional'), ('Not Functional'),('Functional But Not Installed')$$)  \n" +
                "AS r (fid integer, \"Functional\" bigint, \"Not Functional\" bigint, \"Functional But Not Installed\" bigint)\n" +
                "\n" +
                ") AS ws\n" +
                "LEFT  join\n" +
                "(\n" +
                "\n" +
                "SELECT *\n" +
                "FROM   crosstab(\n" +
                "      'SELECT fid, energytype, energytypecount\n" +
                "       FROM   vw_repair_management\n" +
                "       ORDER  BY 1,2'\n" +
                "       ,$$VALUES ('Electricity'), ('Solar'),('Gas'),('Kerosene')$$)  \n" +
                "AS r (fid integer, \"Electricity\" bigint, \"Solar\" bigint, \"Gas\" bigint, \"Kerosene\" bigint)\n" +
                "\n" +
                ")AS es ON es.fid=ws.fid\n" +
                "\n" +
                "LEFT  join\n" +
                "(\n" +
                "SELECT *\n" +
                "FROM   crosstab(\n" +
                "      'SELECT fid, operationalstatus, notfunctionalstatuscount\n" +
                "       FROM   vw_repair_management_not_functional\n" +
                "       ORDER  BY 1,2'\n" +
                "       ,$$VALUES ('Obsolete'), ('Waiting For Repair'),('Waiting For Spare Parts')$$)  \n" +
                "AS r (fid integer, \"Obsolete\" bigint, \"Waiting For Repair\" bigint, \"Waiting For Spare Parts\" bigint)\n" +
                ")as noffunctional on noffunctional.fid=ws.fid\n" +
                "\n" +
                "\n" +
                "LEFT OUTER join\n" +
                "(Select pid, fid from vw_repair_management group by pid, fid) as prog ON prog.fid=ws.fid\n" +
                "LEFT OUTER join\n" +
                "facilities f ON f.id=ws.fid\n" +
                "LEFT OUTER join\n" +
                "geographic_zones g ON g.id=f.geographiczoneid\n" +
                "LEFT OUTER join\n" +
                "facility_types ft ON ft.id=f.typeid";

        String aggregateClose = " ) AS agg\n";

        if (filter.getProgramId() != 0) {
            if (filter.getFacilityTypeId() != 0) {
                String facilityFilter = " WHERE ft.id=" + filter.getFacilityTypeId() + " AND ws.fid in(select facility_id from vw_user_facilities where user_id=" + userId + " and program_id=" + filter.getProgramId() + ")";
                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            } else {
                String facilityFilter = " WHERE ws.fid in(select facility_id from vw_user_facilities where user_id=" + userId + " and program_id=" + filter.getProgramId() + ")";
                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            }
        } else {
            if (filter.getFacilityTypeId() != 0) {
                String facilityFilter = " WHERE ft.id=" + filter.getFacilityTypeId() + " AND  ws.fid in(select facility_id from vw_user_facilities where user_id=" + userId + ")";
                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            } else {
                String facilityFilter = " WHERE ws.fid in(select facility_id from vw_user_facilities where user_id=" + userId + ")";
                if (filter.getAggregate()) {
                    sql = aggregateOpen + allFacilities + facilityFilter + aggregateClose;
                } else {
                    sql = allFacilities + facilityFilter;
                }
            }
        }
        return sql;
    }


    public static String RepairManagementEquipmentListSql(Map params) {

        RepairManagementEquipmentListParam filter = (RepairManagementEquipmentListParam) params.get("filterCriteria");
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
       String equipmentSql="SELECT \n" +
                "  ei.facilityid as facility_id\n" +
                ", ei.programid as program_id\n" +
                ", f.typeid as type_id\n" +
                ", vwd.region_name as region\n" +
                ", vwd.district_name as district\n" +
                ", f.name as facility_name\n" +
                ", ft.name as facility_type\n" +
                ", eos.name as working_status\n" +
                ", brkd.break_down as break_down\n" +
                ", e.manufacturer as manufacturer\n" +
                ", e.model as model\n" +
                ", COALESCE(cce.refrigeratorcapacity,0) as capacity\n"+
                "FROM equipment_inventories ei\n" +
                "     JOIN equipment_inventory_statuses eis ON eis.id = (( SELECT eisb.id\n" +
                "           FROM equipment_inventory_statuses eisb\n" +
                "          WHERE eisb.inventoryid = ei.id\n" +
                "          ORDER BY eisb.createddate DESC\n" +
                "         LIMIT 1))\n" +
                "     JOIN equipment_operational_status eos ON eis.statusid = eos.id\n" +
                "     JOIN equipments e ON ei.equipmentid = e.id\n" +
                "     JOIN equipment_cold_chain_equipments cce ON cce.equipmentid = e.id"+
                "     JOIN equipment_types et ON e.equipmenttypeid = et.id\n" +
                "     JOIN facilities f ON f.id = ei.facilityid\n" +
                "     JOIN facility_types ft ON ft.id = f.typeid\n" +
                "     JOIN vw_districts vwd ON vwd.district_id=f.geographiczoneid\n" +
                "     LEFT JOIN (Select eis.inventoryid as id ,count(eis.id) as break_down from equipment_inventory_statuses eis\n" +
                "     LEFT JOIN equipment_operational_status eos on eos.id=eis.statusid\n" +
                "     WHERE eos.name='Not Functional'\n" +
                "     GROUP BY eis.inventoryid) as brkd ON brkd.id=ei.id\n" +
                "     LEFT JOIN equipment_energy_types eet ON e.energytypeid = eet.id\n" +
                "     WHERE et.iscoldchain IS TRUE \n" +
                "     AND f.id IN (select facility_id from vw_user_facilities where user_id="+userId+")\n";

        String program=" AND ei.programid="+filter.getProgramId();
        String facilityId=" AND f.id="+filter.getFacilityId();
        String status="\nAND LOWER(eos.name) IN('"+workingStatus+"')\n";
        String facilityType="\nAND f.typeid="+filter.getFacilityTypeId()+"\n";

        sql=equipmentSql;
        if (filter.getProgramId() != 0) {
            sql=sql+program;
        }
        if(filter.getFacilityTypeId() !=0)
        {
            sql=sql+facilityType;
        }
        if(filter.getFacilityId() !=0)
        {
            sql=sql+facilityId;
        }
        if(workingStatus !=null && workingStatus !="")
        {
            sql=sql+status;
        }

        return sql;
    }
}
