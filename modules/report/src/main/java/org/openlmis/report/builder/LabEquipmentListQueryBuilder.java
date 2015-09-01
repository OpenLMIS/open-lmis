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


import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.params.LabEquipmentListReportParam;
import org.openlmis.report.model.report.StockedOutReport;
import org.openlmis.report.util.StringHelper;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class LabEquipmentListQueryBuilder {

    public static String getData(Map params){

        LabEquipmentListReportParam filter = (LabEquipmentListReportParam) params.get("filterCriteria");
        BEGIN();
        SELECT("facility_code AS facilityCode, facility_name AS facilityName, facility_type AS facilityType, disrict as district, zone," +
                " equipment_type AS equipmentType, equipment_model AS model, serial_number AS serialNumber, equipment_name AS equipmentName, equipment_status AS operationalStatus");
        FROM("vw_lab_equipment_status");
        writePredicates(filter);
        ORDER_BY("facilityName, equipmentName");
        return SQL();

    }

    public static String getFunctioningEquipmentWithContract(Map params){

        LabEquipmentListReportParam filter = (LabEquipmentListReportParam) params.get("filterCriteria");
        BEGIN();
        SELECT("facility_code AS facilityCode, facility_name AS facilityName, facility_type AS facilityType, disrict as district, zone," +
                " equipment_type AS equipmentType, equipment_model AS model, serial_number AS serialNumber, equipment_name AS equipmentName, 'Functioning' AS operationalStatus," +
                " case when contract.contractid is null THEN 'NO' else 'YES' END AS serviceContract, contract.name AS vendorName, contract.contractid as contractId");
                //" case when hasservicecontract = 'f' THEN 'NO' when hasservicecontract = 't' THEN 'YES' END AS serviceContract, contract.name AS vendorName, contract.contractid as contractId");

        FROM("vw_lab_equipment_status");
        LEFT_OUTER_JOIN("(SELECT distinct name, vendorid, equipment_service_contracts.id contractid, equipmentid, facilityid FROM equipment_service_contracts JOIN equipment_service_contract_equipments ON\n" +
                " equipment_service_contracts.id = equipment_service_contract_equipments.contractid JOIN\n" +
                " equipment_service_contract_facilities ON equipment_service_contracts.id = equipment_service_contract_facilities.contractid\n" +
                " JOIN equipment_service_vendors ON equipment_service_vendors.id = equipment_service_contracts.vendorid) contract\n" +
                " on vw_lab_equipment_status.equipment_id = contract.equipmentid AND contract.facilityid = vw_lab_equipment_status.facility_id ");
        writePredicatesForServiceContractReports(filter);
        WHERE("equipment_status  = 'Fully Operational'");
        ORDER_BY("facilityName, equipmentName");
        return SQL();
    }

    public static String getNonFunctioningEquipmentWithContract(Map params){

        LabEquipmentListReportParam filter = (LabEquipmentListReportParam) params.get("filterCriteria");
        BEGIN();
        SELECT("facility_code AS facilityCode, facility_name AS facilityName, facility_type AS facilityType, disrict as district, zone," +
                " equipment_type AS equipmentType, equipment_model AS model, serial_number AS serialNumber, equipment_name AS equipmentName, 'Not Functioning' AS operationalStatus," +
                " case when contract.contractid is null THEN 'NO' else 'YES' END AS serviceContract, contract.name AS vendorName, contract.contractid as contractId");
     //        " case when hasservicecontract = 'f' THEN 'NO' when hasservicecontract = 't' THEN 'YES' END AS serviceContract, contract.name AS vendorName, contract.contractid as contractId");
        FROM("vw_lab_equipment_status");
        LEFT_OUTER_JOIN("(SELECT distinct name, vendorid, equipment_service_contracts.id contractid, equipmentid, facilityid FROM equipment_service_contracts JOIN equipment_service_contract_equipments ON\n" +
                " equipment_service_contracts.id = equipment_service_contract_equipments.contractid JOIN\n" +
                " equipment_service_contract_facilities ON equipment_service_contracts.id = equipment_service_contract_facilities.contractid\n" +
                " JOIN equipment_service_vendors ON equipment_service_vendors.id = equipment_service_contracts.vendorid) contract\n" +
                " on vw_lab_equipment_status.equipment_id = contract.equipmentid AND contract.facilityid = vw_lab_equipment_status.facility_id ");
        writePredicatesForServiceContractReports(filter);
        WHERE("equipment_status  = 'Not Operational'");
        ORDER_BY("facilityName, equipmentName");
        return SQL();
    }

    private static void writePredicatesForServiceContractReports(LabEquipmentListReportParam filter) {

        WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
        WHERE("programid  = #{filterCriteria.programId}");

        if (filter != null) {

            if (filter.getFacilityTypeId() != 0) {
                WHERE("ftype_id = #{filterCriteria.facilityTypeId}");
            }

            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                WHERE("(district_id = #{filterCriteria.zoneId} or zone_id = #{filterCriteria.zoneId} or region_id = #{filterCriteria.zoneId} or parent = #{filterCriteria.zoneId})");
            }

            if (filter.getFacilityId() != 0) {
                WHERE("facility_id = #{filterCriteria.facilityId}");
            }

            if (filter.getEquipmentTypeId() != 0) {
                WHERE("equipmenttype_id = #{filterCriteria.equipmentTypeId}");
            }

            if (filter.getEquipmentId() != 0) {
                WHERE("equipment_id = #{filterCriteria.equipmentId}");
            }

            /*if (filter.getServiceContractAvailable() != 0) {
                if(filter.getServiceContractAvailable() == 1)
                    WHERE("hasservicecontract = 'YES'");
                else
                    WHERE("hasservicecontract = 'NO'");
            }*/
            if (filter.getServiceContractAvailable() != 0) {
                // Based on the other interpretation of equipment contract
                if (filter.getServiceContractAvailable() == 1)
                    WHERE("contract.contractid is not null");
                else
                    WHERE("contract.contractid is null");
            }
        }
    }

    private static void writePredicates(LabEquipmentListReportParam filter) {


        WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
        WHERE("programid  = #{filterCriteria.programId}");

        if (filter != null) {

            if (filter.getFacilityTypeId() != 0) {
                WHERE("ftype_id = #{filterCriteria.facilityTypeId}");
            }

            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                WHERE("(district_id = #{filterCriteria.zoneId} or zone_id = #{filterCriteria.zoneId} or region_id = #{filterCriteria.zoneId} or parent = #{filterCriteria.zoneId})");
            }

            if (filter.getFacilityId() != 0) {
                WHERE("facility_id = #{filterCriteria.facilityId}");
            }

            if (filter.getEquipmentTypeId() != 0) {
                WHERE("equipmenttype_id = #{filterCriteria.equipmentTypeId}");
            }

            if (filter.getEquipmentId() != 0) {
                WHERE("equipment_id = #{filterCriteria.equipmentId}");
            }

        }
    }
}
