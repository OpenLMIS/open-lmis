/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.UiUtils;

import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.getProperty;

public class DBWrapper {

  public static final int DEFAULT_MAX_MONTH_OF_STOCK = 3;
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";
  public static final String DEFAULT_DB_USERNAME = "postgres";
  public static final String DEFAULT_DB_PASSWORD = "p@ssw0rd";

  Connection connection;

  public DBWrapper() throws SQLException {
    String dbUser = getProperty("dbUser", DEFAULT_DB_USERNAME);
    String dbPassword = getProperty("dbPassword", DEFAULT_DB_PASSWORD);
    String dbUrl = getProperty("dbUrl", DEFAULT_DB_URL);

    loadDriver();

    connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
  }

  public void closeConnection() throws SQLException {
    if (connection != null) connection.close();
  }

  private void loadDriver() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      System.exit(1);
    }
  }

  private void update(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(sql);
    }
  }

  private void update(String sql, Object... params) throws SQLException {
    update(format(sql, params));
  }

  private ResultSet query(String sql) throws SQLException {
    return connection.createStatement().executeQuery(sql);
  }

  private List<Map<String, String>> select(String sql, Object... params) throws SQLException {
    ResultSet rs = query(sql, params);
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    List<Map<String, String>> list = new ArrayList<>();
    while (rs.next()) {
      Map<String, String> row = new HashMap<>();
      for (int i = 1; i <= columns; ++i) {
        row.put(md.getColumnName(i), rs.getString(i));
      }
      list.add(row);
    }
    return list;
  }

  private ResultSet query(String sql, Object... params) throws SQLException {
    return query(format(sql, params));
  }

  public void insertUser(String userName, String password, String facilityCode, String email) throws SQLException {
    update("delete from users where userName like('%s')", userName);

    update("INSERT INTO users(userName, password, facilityId, firstName, lastName, email, active, verified) " +
        "VALUES ('%s', '%s', (SELECT id FROM facilities WHERE code = '%s'), 'Fatima', 'Doe', '%s','true','true')",
      userName, password, facilityCode, email
    );
  }

  public List<String> getProductDetailsForProgram(String programCode) throws SQLException {
    List<String> prodDetails = new ArrayList<>();

    ResultSet rs = query("select programs.code as programCode, programs.name as programName, " +
      "products.code as productCode, products.primaryName as productName, products.description as desc, " +
      "products.dosesPerDispensingUnit as unit, PG.name as pgName " +
      "from products, programs, program_products PP, product_categories PG " +
      "where programs.id = PP.programId and PP.productId=products.id and " +
      "PG.id = pp.productCategoryId and programs.code='" + programCode + "' " +
      "and products.active='true' and PP.active='true'");

    while (rs.next()) {
      String programName = rs.getString(2);
      String productCode = rs.getString(3);
      String productName = rs.getString(4);
      String desc = rs.getString(5);
      String unit = rs.getString(6);
      String pcName = rs.getString(7);
      prodDetails.add(programName + "," + productCode + "," + productName + "," + desc + "," + unit + "," + pcName);
    }
    return prodDetails;
  }

  public List<String> getProductDetailsForProgramAndFacilityType(String programCode, String facilityCode) throws SQLException {
    List<String> prodDetails = new ArrayList<>();

    ResultSet rs = query("select program.code as programCode, program.name as programName, product.code as productCode, " +
      "product.primaryName as productName, product.description as desc, product.dosesPerDispensingUnit as unit, " +
      "pg.name as pgName from products product, programs program, program_products pp, product_categories pg, " +
      "facility_approved_products fap, facility_types ft where program.id=pp.programId and pp.productId=product.id and " +
      "pg.id = pp.productCategoryId and fap. programProductId = pp.id and ft.id=fap.facilityTypeId and program.code='" +
      programCode + "' and ft.code='" + facilityCode + "' " + "and product.active='true' and pp.active='true'");

    while (rs.next()) {
      String programName = rs.getString(2);
      String productCode = rs.getString(3);
      String productName = rs.getString(4);
      String desc = rs.getString(5);
      String unit = rs.getString(6);
      String pgName = rs.getString(7);
      prodDetails.add(programName + "," + productCode + "," + productName + "," + desc + "," + unit + "," + pgName);
    }
    return prodDetails;
  }

  public void updateActiveStatusOfProgramProduct(String productCode, String programCode, String active) throws SQLException {
    update("update program_products set active='%s' WHERE programId = (select id from programs where code='%s')  AND" +
      " productId = (select id from products where code='%s')", active, programCode, productCode);
  }

  public List<String> getFacilityCodeNameForDeliveryZoneAndProgram(String deliveryZoneName, String program, boolean active) throws SQLException {
    List<String> codeName = new ArrayList<>();
    ResultSet rs = query(
      "select f.code, f.name from facilities f, programs p, programs_supported ps, delivery_zone_members dzm, delivery_zones dz where " +
        "dzm.DeliveryZoneId=dz.id and f.active='" + active + "' and p.id= ps.programId and p.code='" + program + "' and " +
        "dz.id = dzm.DeliveryZoneId and dz.name='" + deliveryZoneName + "' and dzm.facilityId = f.id and ps.facilityId = f.id;"
    );

    while (rs.next()) {
      String code = rs.getString("code");
      String name = rs.getString("name");
      codeName.add(code + " - " + name);
    }
    return codeName;
  }

  public void deleteDeliveryZoneMembers(String facilityCode) throws SQLException {
    update("delete from delivery_zone_members where facilityId in (select id from facilities where code ='%s')", facilityCode);
  }

  public void updateUser(String password, String email) throws SQLException {
    update("DELETE FROM user_password_reset_tokens");
    update("update users set password = '%s', active = TRUE, verified = TRUE  where email = '%s'", password, email);
  }

  public void updateRestrictLogin(String userName, boolean status) throws SQLException {
    update("update users set restrictLogin = '%s' where userName = '%s'", status, userName);
  }

  public void insertRequisitions(int numberOfRequisitions, String program, boolean withSupplyLine, String periodStartDate,
                                 String periodEndDate, String facilityCode, boolean emergency) throws SQLException {
    int numberOfRequisitionsAlreadyPresent = 0;
    boolean flag = true;
    ResultSet rs = query("select count(*) from requisitions");
    if (rs.next()) {
      numberOfRequisitionsAlreadyPresent = Integer.parseInt(rs.getString(1));
    }

    for (int i = numberOfRequisitionsAlreadyPresent + 1; i <= numberOfRequisitions + numberOfRequisitionsAlreadyPresent; i++) {
      insertProcessingPeriod("PeriodName" + i, "PeriodDesc" + i, periodStartDate, periodEndDate, 1, "M");
      update("insert into requisitions (facilityId, programId, periodId, status, emergency, " +
        "fullSupplyItemsSubmittedCost, nonFullSupplyItemsSubmittedCost, supervisoryNodeId) " +
        "values ((Select id from facilities where code='" + facilityCode + "'),(Select id from programs where code='" + program + "')," +
        "(Select id from processing_periods where name='PeriodName" + i + "'), 'APPROVED', '" + emergency + "', 50.0000, 0.0000, " +
        "(select id from supervisory_nodes where code='N1'))");

      update("INSERT INTO requisition_line_items " +
        "(rnrId, productCode,product,productDisplayOrder,productCategory,productCategoryDisplayOrder, beginningBalance, quantityReceived, quantityDispensed, stockInHand, " +
        "dispensingUnit, maxMonthsOfStock, dosesPerMonth, dosesPerDispensingUnit, packSize,fullSupply,totalLossesAndAdjustments,newPatientCount,stockOutDays,price,roundToZero,packRoundingThreshold) VALUES" +
        "((SELECT max(id) FROM requisitions), 'P10','antibiotic Capsule 300/200/600 mg',1,'Antibiotics',1, '0', '11' , '1', '10' ,'Strip','3', '30', '10', '10','t',0,0,0,12.5000,'f',1);");
    }
    if (withSupplyLine) {
      ResultSet rs1 = query("select * from supply_lines where supervisoryNodeId = " +
        "(select id from supervisory_nodes where code = 'N1') and programId = " +
        "(select id from programs where code='" + program + "') and supplyingFacilityId = " +
        "(select id from facilities where code = 'F10')");
      if (rs1.next()) {
        flag = false;
      }
    }
    if (withSupplyLine) {
      if (flag) {
        insertSupplyLines("N1", program, "F10", true);
      }
    }
  }

  public void insertFulfilmentRoleAssignment(String userName, String roleName, String facilityCode) throws SQLException {
    update("insert into fulfillment_role_assignments(userId, roleId, facilityId) values " +
      "((select id from users where username='" + userName + "'), (select id from roles where name='" + roleName + "')," +
      "(select id from facilities where code='" + facilityCode + "'))");
  }

  public String getDeliveryZoneNameAssignedToUser(String user) throws SQLException {
    String deliveryZoneName = "";
    ResultSet rs = query(
      "select name from delivery_zones where id in(select deliveryZoneId from role_assignments where userId=" +
        "(select id from users where username='" + user + "'))"
    );

    if (rs.next()) {
      deliveryZoneName = rs.getString("name");
    }
    return deliveryZoneName;
  }

  public String getRoleNameAssignedToUser(String user) throws SQLException {
    String userName = "";
    ResultSet rs = query("select name from roles where id in(select roleId from role_assignments where " +
      "userId=(select id from users where username='" + user + "'))");

    if (rs.next()) {
      userName = rs.getString("name");
    }
    return userName;
  }

  public void insertFacilities(String facility1, String facility2) throws SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',5,2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE', 'FALSE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',5,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'FALSE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES" +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1)");
  }

  public void insertFacilitiesWithFacilityTypeIDAndGeoZoneId(String facility1, String facility2, int facilityType, int geoZoneId) throws SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B','" + geoZoneId + "','" + facilityType + "',333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE', 'FALSE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B','" + geoZoneId + "','" + facilityType + "',333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'FALSE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES" +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1)");
  }

  public void insertVirtualFacility(String facilityCode, String parentFacilityCode) throws SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, " +
      "longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, " +
      "online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, virtualFacility," +
      "parentFacilityId) values\n" +
      "('" + facilityCode + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',5,2,333,22.1,1.2,3.3,2,9.9,6.6," +
      "'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'TRUE'," +
      "(SELECT id FROM facilities WHERE code = '" + parentFacilityCode + "'))");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES" +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 1, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 2, '11/11/12', true, 1)," +
      " ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 5, '11/11/12', true, 1)");

    update("insert into requisition_group_members (requisitionGroupId, facilityId, createdDate, modifiedDate) values " +
      "((select requisitionGroupId from requisition_group_members where facilityId=(SELECT id FROM facilities WHERE code = '"
      + parentFacilityCode + "'))," +
      "(SELECT id FROM facilities WHERE code = '" + facilityCode + "'),NOW(),NOW())");
  }

  public void deleteRowFromTable(String tableName, String queryParam, String queryValue) throws SQLException {
    update("delete from " + tableName + " where " + queryParam + "='" + queryValue + "';");
  }

  public void insertFacilitiesWithDifferentGeoZones(String facility1, String facility2, String geoZone1, String geoZone2) throws SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, " +
      "latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, " +
      "hasElectricity, online, hasElectronicSCC, hasElectronicDAR, active, goLiveDate, goDownDate, satellite, comment, enabled, " +
      "virtualFacility) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B'," +
      "(select id from geographic_zones where code='" + geoZone1 + "'),2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE'," +
      "'TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE', 'FALSE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B'," +
      "(select id from geographic_zones where code='" + geoZone2 + "'),2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE'," +
      "'TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE', 'FALSE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1);");
  }

  public void insertGeographicZone(String code, String name, String parentName) throws SQLException {
    update("insert into geographic_zones (code, name, levelId, parentId) values ('%s','%s',(select max(levelId) from geographic_zones)," +
      "(select id from geographic_zones where code='%s'))", code, name, parentName);
  }

  public void allocateFacilityToUser(String userName, String facilityCode) throws SQLException {
    update("update users set facilityId = (Select id from facilities where code = '%s') where username = '%s'", facilityCode, userName);
  }

  public void updateSourceOfAProgramTemplate(String program, String label, String source) throws SQLException {
    update("update program_rnr_columns set source = '%s'" + " where programId = (select id from programs where code = '%s') " +
      "and label = '%s'", source, program, label);
  }

  public void deleteData() throws SQLException {
    update("delete from budget_line_items");
    update("delete from budget_file_info");
    update("delete from role_rights where roleId not in(1)");
    update("delete from role_assignments where userId not in (1)");
    update("delete from fulfillment_role_assignments");
    update("delete from roles where name not in ('Admin')");
    update("delete from facility_approved_products");
    update("delete from program_product_price_history");
    update("delete from pod_line_items");
    update("delete from pod");
    update("delete from shipment_line_items");
    update("delete from orders");
    update("delete from requisition_status_changes");
    update("delete from user_password_reset_tokens");
    update("delete from comments");
    update("delete from epi_use_line_items");
    update("delete from epi_inventory_line_items");
    update("delete from refrigerator_problems");
    update("delete from refrigerator_readings");
    update("delete from full_coverages");
    update("delete from vaccination_child_coverage_line_items;");
    update("delete from vaccination_adult_coverage_line_items;");
    update("delete from coverage_target_group_products;");
    update("delete from child_coverage_opened_vial_line_items;");
    update("delete from adult_coverage_opened_vial_line_items;");
    update("delete from facility_visits");
    update("delete from distributions");
    update("delete from refrigerators");
    update("delete from users where userName not like('Admin%')");
    deleteRnrData();

    update("delete from program_product_isa");
    update("delete from facility_approved_products");
    update("delete from facility_program_products");
    update("delete from program_products");
    update("delete from coverage_target_group_products");
    update("delete from coverage_product_vials");
    update("delete from products");
    update("delete from product_categories");
    update("delete from product_groups");

    update("delete from supply_lines");
    update("delete from programs_supported");
    update("delete from requisition_group_members");
    update("delete from program_rnr_columns");
    update("delete from requisition_group_program_schedules");
    update("delete from requisition_groups");
    update("delete from requisition_group_members");
    update("delete from delivery_zone_program_schedules");
    update("delete from delivery_zone_warehouses");
    update("delete from delivery_zone_members");
    update("delete from role_assignments where deliveryZoneId in (select id from delivery_zones where code in('DZ1','DZ2'))");
    update("delete from delivery_zones");

    update("delete from supervisory_nodes");
    update("delete from facility_ftp_details");
    update("delete from facilities");
    update("delete from geographic_zones where code not in ('Root','Arusha','Dodoma', 'Ngorongoro')");
    update("delete from processing_periods");
    update("delete from processing_schedules");
    update("delete from atomfeed.event_records");
    update("delete from regimens");
    update("delete from program_regimen_columns");
  }

  public void deleteRnrData() throws SQLException {
    update("delete from requisition_line_item_losses_adjustments");
    update("delete from requisition_line_items");
    update("delete from regimen_line_items");
    update("delete from requisitions");
  }

  public void insertRole(String role, String description) throws SQLException {
    ResultSet rs = query("Select id from roles where name='%s'", role);

    if (!rs.next()) {
      update("INSERT INTO roles(name, description) VALUES('%s', '%s')", role, description);
    }
  }

  public void deleteSupervisoryNodes() throws SQLException {
    update("delete from supervisory_nodes");
  }

  public void insertSupervisoryNode(String facilityCode, String supervisoryNodeCode, String supervisoryNodeName,
                                    String supervisoryNodeParentCode) throws SQLException {
    update("INSERT INTO supervisory_nodes" +
        "  (parentId, facilityId, name, code) VALUES" +
        "  ((select id from  supervisory_nodes where code ='%s'), (SELECT id FROM facilities WHERE code = '%s'), '%s', '%s')",
      supervisoryNodeParentCode, facilityCode, supervisoryNodeName, supervisoryNodeCode
    );
  }

  public void insertRequisitionGroups(String code1, String code2, String supervisoryNodeCode1, String supervisoryNodeCode2) throws SQLException {
    ResultSet rs = query("Select id from requisition_groups;");

    if (rs.next()) {
      update("delete from requisition_groups;");
    }
    update("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
      "('" + code2 + "','Requisition Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='"
      + supervisoryNodeCode2 + "')),\n" +
      "('" + code1 + "','Requisition Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='"
      + supervisoryNodeCode1 + "'))");
  }

  public void insertRequisitionGroup(String requisitionGroupCode, String requisitionGroupName, String supervisoryNodeCode) throws SQLException {
    ResultSet rs = query("Select id from requisition_groups;");

    if (rs.next()) {
      update("delete from requisition_groups;");
    }
    update("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
      "('" + requisitionGroupCode + "','" + requisitionGroupName + "','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='"
      + supervisoryNodeCode + "'));");
  }

  public void insertRequisitionGroupWithoutDelete(String requisitionGroupCode, String requisitionGroupName, String supervisoryNodeCode) throws SQLException {
    update("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
      "('" + requisitionGroupCode + "','" + requisitionGroupName + "','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='"
      + supervisoryNodeCode + "'));");
  }

  public void insertRequisitionGroupMembers(String RG1facility, String RG2facility) throws SQLException {
    ResultSet rs = query("Select requisitionGroupId from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_members;");
    }
    update("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
      "((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='" + RG1facility + "')),\n" +
      "((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='" + RG2facility + "'));");
  }

  public void insertRequisitionGroupMember(String RGCode, String RGFacility) throws SQLException {
    update("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
      "((select id from  requisition_groups where code ='" + RGCode + "'),(select id from  facilities where code ='" + RGFacility + "'));");
  }

  public void insertRequisitionGroupProgramSchedule() throws SQLException {
    ResultSet rs = query("Select requisitionGroupId from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_program_schedules;");
    }
    update(
      "insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values\n" +
        "((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS')," +
        "(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
        "((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA')," +
        "(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
        "((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV')," +
        "(select id from processing_schedules where code='M'),TRUE),\n" +
        "((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS')," +
        "(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
        "((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA')," +
        "(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
        "((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV')," +
        "(select id from processing_schedules where code='M'),TRUE);\n"
    );
  }

  public void insertRoleAssignment(String userName, String roleName) throws SQLException {
    update("delete from role_assignments where userId = (SELECT id FROM users WHERE username = '" + userName + "');");

    update(" INSERT INTO role_assignments (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ((SELECT id FROM users WHERE username = '" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ((SELECT id FROM users WHERE username = '" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, " +
      "(SELECT id from supervisory_nodes WHERE code = 'N1'));");
  }

  public void insertRoleAssignmentForSupervisoryNodeForProgramId(String userName, String roleName, String supervisoryNode) throws SQLException {
    update("delete from role_assignments where userId= (SELECT id FROM users WHERE username = '" + userName + "');");

    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ((SELECT id FROM users WHERE username = '" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ((SELECT id FROM users WHERE username = '" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, " +
      "(SELECT id from supervisory_nodes WHERE code = '" + supervisoryNode + "'));");
  }

  public void updateRoleGroupMember(String facilityCode) throws SQLException {
    update("update requisition_group_members set facilityId = (select id from facilities where code ='" + facilityCode + "') where " +
      "requisitionGroupId=(select id from requisition_groups where code='RG2');");
    update("update requisition_group_members set facilityId =  (select id from facilities where code ='F11') where requisitionGroupId = " +
      "(select id from requisition_groups where code='RG1');");
  }

  public void alterUserID(String userName, String userId) throws SQLException {
    update("delete from user_password_reset_tokens;");
    update("delete from users where id='" + userId + "' ;");
    update(" update users set id='" + userId + "' where username='" + userName + "'");
  }

  public void insertProducts(String product1, String product2) throws SQLException {
    update("delete from facility_approved_products;");
    update("delete from epi_inventory_line_items;");
    update("delete from program_products;");
    update("delete from products;");
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
      "('" + product1 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE  ),\n" +
      "('" + product2 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE );\n");
  }

  public void deleteDescriptionFromProducts() throws SQLException {
    update("UPDATE products SET description = null");
  }

  public void insertProductCategory(String categoryCode, String categoryName) throws SQLException {
    update("INSERT INTO product_categories (code, name, displayOrder) values ('" + categoryCode + "', '" + categoryName + "', 1);");
  }

  public void insertProductCategoryWithDisplayOrder(String categoryCode, String categoryName, int displayOrder) throws SQLException {
    update("INSERT INTO product_categories (code, name, displayOrder) values ('" + categoryCode + "', '" + categoryName + "', " + displayOrder + ");");
  }

  public void insertProduct(String product, String productName) throws SQLException {
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
      "('" + product + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    '" + productName + "', '" + productName + "',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE);\n");
  }

  public void insertProductGroup(String group) throws SQLException {
    update("INSERT INTO product_groups (code, name) values ('" + group + "', '" + group + "-Name');");
  }

  public void insertProductWithGroup(String product, String productName, String group, boolean status) throws SQLException {
    update("INSERT INTO products\n" +
      "(code, alternateItemCode, manufacturer, manufacturerCode, manufacturerBarcode, mohBarcode, gtin, type, primaryName, fullName," +
      " genericName, alternateName, description, strength, formId, dosageUnitId, dispensingUnit, dosesPerDispensingUnit, packSize, alternatePackSize," +
      " storeRefrigerated, storeRoomTemperature, hazardous, flammable, controlledSubstance, lightSensitive, approvedByWho, contraceptiveCyp, packLength, " +
      "packWidth, packHeight, packWeight, packsPerCarton, cartonLength, cartonWidth, cartonHeight, cartonsPerPallet, expectedShelfLife, specialStorageInstructions, " +
      "specialTransportInstructions, active, fullSupply, tracer, packRoundingThreshold, roundToZero, archived, productGroupId) values" +
      "('" + product + "', 'a', 'Glaxo and Smith', 'a', 'a', 'a', 'a', '" + productName + "', '" + productName +
      "', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', '300/200/600', 2, 1, 'Strip', 10, 10, 30, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE," +
      " 1, 2.2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 'a', 'a', " + status + ",TRUE, TRUE, 1, FALSE, TRUE, (Select id from product_groups where code='" + group + "'));");
  }

  public void updateProgramToAPushType(String program, boolean flag) throws SQLException {
    update("update programs set push='" + flag + "' where code='" + program + "';");
  }

  public void insertProgramProducts(String product1, String product2, String program) throws SQLException {
    update("delete from product_categories;");
    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    ResultSet rs = query("Select id from program_products;");

    if (rs.next()) {
      update("delete from facility_approved_products;");
      update("delete from program_products;");
    }

    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId, fullSupply) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product1 + "'), 30, 12.5, true, 1, (select id from product_categories where code = 'C1'), true),\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product2 + "'), 30, 12.5, true, 5, (select id from product_categories where code = 'C1'), true);");
  }

  public void insertProgramProductsWithoutDeleting(String product1, String product2, String program) throws SQLException {
    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId, fullSupply) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product1 + "'), 30, 12.5, true, 1, (select id from product_categories where code = 'C1'), true),\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product2 + "'), 30, 12.5, true, 5, (select id from product_categories where code = 'C1'), true);");
  }

  public void insertProgramProduct(String product, String program, String doses, String active) throws SQLException {
    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId, fullSupply) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product + "'), '" + doses + "', 12.5, '" + active + "', 1, (Select id from product_categories where code='C1'), true);");
  }

  public void insertProgramProductsWithCategory(String product, String program, String category, Integer displayOrder) throws SQLException {
    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId, fullSupply) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product + "'), 30, 12.5, true," + displayOrder + ", (Select id from product_categories where code='" + category + "'), true);");
  }

  public void insertProgramProductISA(String program, String product, String whoRatio, String dosesPerYear, String wastageFactor,
                                      String bufferPercentage, String minimumValue, String maximumValue, String adjustmentValue) throws SQLException {
    update(
      "INSERT INTO program_product_isa(programProductId, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue, adjustmentValue) VALUES\n" +
        "((SELECT ID from program_products where programId = " +
        "(SELECT ID from programs where code='" + program + "') and productId = " +
        "(SELECT id from products WHERE code = '" + product + "'))," + whoRatio + "," + dosesPerYear + "," + wastageFactor + ","
        + bufferPercentage + "," + minimumValue + "," + maximumValue + "," + adjustmentValue + ");"
    );
  }

  public void insertFacilityApprovedProduct(String productCode, String programCode, String facilityTypeCode) throws SQLException {
    String facilityTypeIdQuery = format("(SELECT id FROM facility_types WHERE code = '%s')", facilityTypeCode);
    String productIdQuery = format("(SELECT id FROM products WHERE  code = '%s')", productCode);
    String programIdQuery = format("(SELECT ID from programs where code = '%s' )", programCode);
    String programProductIdQuery = format("(SELECT id FROM program_products WHERE programId = %s AND productId = %s)", programIdQuery, productIdQuery);

    update(format(
      "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES (%s,%s,%d)",
      facilityTypeIdQuery, programProductIdQuery, DEFAULT_MAX_MONTH_OF_STOCK));
  }

  public String fetchNonFullSupplyData(String productCode, String facilityTypeID, String programID) throws SQLException {
    ResultSet rs = query("Select p.code, pp.displayOrder, p.primaryName, pf.code as ProductForm," +
      "p.strength as Strength, du.code as DosageUnit from facility_approved_products fap, " +
      "program_products pp, products p, product_forms pf , dosage_units du where fap. facilityTypeId='" + facilityTypeID + "' " +
      "and p. fullSupply = false and p.active=true and pp.programId='" + programID + "' and p. code='" + productCode + "' " +
      "and pp.productId=p.id and fap. programProductId=pp.id and pp.active=true and pf.id=p.formId " +
      "and du.id = p.dosageUnitId order by pp. displayOrder asc;");
    String nonFullSupplyValues = null;
    if (rs.next()) {
      nonFullSupplyValues = rs.getString("primaryName") + " " + rs.getString("productForm") + " " + rs.getString("strength") + " " + rs.getString("dosageUnit");
    }
    return nonFullSupplyValues;
  }

  public void insertSchedule(String scheduleCode, String scheduleName, String scheduleDesc) throws SQLException {
    update("INSERT INTO processing_schedules(code, name, description) values('" + scheduleCode + "', '" + scheduleName + "', '" + scheduleDesc + "');");
  }

  public void insertProcessingPeriod(String periodName, String periodDesc, String periodStartDate,
                                     String periodEndDate, Integer numberOfMonths, String scheduleCode) throws SQLException {
    update("INSERT INTO processing_periods (name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES\n" +
      "('" + periodName + "', '" + periodDesc + "', '" + periodStartDate + " 00:00:00', '" + periodEndDate + " 23:59:59', "
      + numberOfMonths + ", (SELECT id FROM processing_schedules WHERE code = '" + scheduleCode + "'), (SELECT id FROM users LIMIT 1));");
  }

  public void insertCurrentPeriod(String periodName, String periodDesc, Integer numberOfMonths, String scheduleCode) throws SQLException {
    update("INSERT INTO processing_periods\n" +
      "(name, description, startDate, endDate, numberOfMonths, scheduleId, modifiedBy) VALUES\n" +
      "('" + periodName + "', '" + periodDesc + "', NOW() - interval '5' day, NOW() + interval '5' day, " + numberOfMonths + ", " +
      "(SELECT id FROM processing_schedules WHERE code = '" + scheduleCode + "'), (SELECT id FROM users LIMIT 1));");
  }

  public void configureTemplate(String program) throws SQLException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, formulaValidationRequired, position, label, rnrOptionId) VALUES\n" +
      "(1, (select id from programs where code = '" + program + "'),  true, 'U', false,1,  'Skip', null),\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', false,2,  'Product Code', null),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', false,3,  'Product', null),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'R', false,4,  'Unit/Unit of Issue', null),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', false,5,  'Beginning Balance', null),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'U', false,6,  'Total Received Quantity', null),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'C', false,7,  'Total', null),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'U', false,8,  'Total Consumed Quantity', null),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', false,9,  'Total Losses / Adjustments', null),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'C', true,10,  'Stock on Hand', null),\n" +
      "(11, (select id from programs where code = '" + program + "'),  true, 'U', false,11, 'New Patients', 1),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'U', false,12, 'Total StockOut days', null),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', false,13, 'Adjusted Total Consumption', null),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', false,14, 'Average Monthly Consumption(AMC)', null),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', false,15, 'Maximum Stock Quantity', null),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'C', false,16, 'Calculated Order Quantity', null),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', false,17, 'Requested quantity', null),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', false,18, 'Requested quantity explanation', null),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'U', false,19, 'Approved Quantity', null),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'C', false,20, 'Packs to Ship', null),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'R', false,21, 'Price per pack', null),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'C', false,22, 'Total cost', null),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', false,23, 'Expiration Date', null),\n" +
      "(24, (select id from programs where code = '" + program + "'), true, 'U', false,24, 'Remarks', null),\n" +
      "(25, (select id from programs where code = '" + program + "'), true, 'C', false,25, 'Period Normalized Consumption', null)");
  }

  public void configureTemplateForCommTrack(String program) throws SQLException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, position, label, rnrOptionId) VALUES\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', 1,  'Product Code', null),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', 2,  'Product', null),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'R', 3,  'Unit/Unit of Issue', null),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', 4,  'Beginning Balance', null),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'U', 5,  'Total Received Quantity', null),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'C', 6,  'Total', null),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'C', 7,  'Total Consumed Quantity', null),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', 8,  'Total Losses / Adjustments', null),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'U', 9,  'Stock on Hand', null),\n" +
      "(11, (select id from programs where code = '" + program + "'),  true, 'U', 10, 'New Patients', 1),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'U', 11, 'Total StockOut days', null),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', 12, 'Adjusted Total Consumption', null),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', 13, 'Average Monthly Consumption(AMC)', null),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', 14, 'Maximum Stock Quantity', null),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'C', 15, 'Calculated Order Quantity', null),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', 16, 'Requested quantity', null),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', 17, 'Requested quantity explanation', null),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'U', 18, 'Approved Quantity', null),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'C', 19, 'Packs to Ship', null),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'R', 20, 'Price per pack', null),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'C', 21, 'Total cost', null),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', 22, 'Expiration Date', null),\n" +
      "(24, (select id from programs where code = '" + program + "'), true, 'U', 23, 'Remarks', null);");
  }

  public void InsertOverriddenIsa(String facilityCode, String program, String product, int overriddenIsa) throws SQLException {
    update("INSERT INTO facility_program_products (facilityId, programProductId,overriddenIsa) VALUES  (" +
      getAttributeFromTable("facilities", "id", "code", facilityCode) + ", (select id from program_products where programId='" +
      getAttributeFromTable("programs", "id", "code", program) + "' and productId='" +
      getAttributeFromTable("products", "id", "code", product) + "')," + overriddenIsa + ");");
  }

  public void updateOverriddenIsa(String facilityCode, String program, String product, String overriddenIsa) throws SQLException {
    update("Update facility_program_products set overriddenIsa=" + overriddenIsa + " where facilityId='" +
      getAttributeFromTable("facilities", "id", "code", facilityCode) + "' and programProductId = (select id from program_products where programId='" +
      getAttributeFromTable("programs", "id", "code", program) + "' and productId='" +
      getAttributeFromTable("products", "id", "code", product) + "');");
  }

  public void insertSupplyLines(String supervisoryNode, String programCode, String facilityCode, boolean exportOrders) throws SQLException {
    update("insert into supply_lines (description, supervisoryNodeId, programId, supplyingFacilityId,exportOrders) values" +
      "('supplying node for " + programCode + "', " +
      "(select id from supervisory_nodes where code = '" + supervisoryNode + "')," +
      "(select id from programs where code='" + programCode + "')," +
      "(select id from facilities where code = '" + facilityCode + "')," + exportOrders + ");");
  }

  public void updateSupplyLines(String previousFacilityCode, String newFacilityCode) throws SQLException {
    update("update supply_lines SET supplyingFacilityId=(select id from facilities where code = '" + newFacilityCode + "') " +
      "where supplyingFacilityId=(select id from facilities where code = '" + previousFacilityCode + "');");
  }

  public void setupDataForGeoZones() throws SQLException {
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District1','district1',2,null,900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District2','District2',1,null,900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District3','District3',3,(select id from geographic_zones where code = 'District2'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District4','district4',4,(select id from geographic_zones where code = 'District3'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('district5','district5',4,(select id from geographic_zones where code = 'District3'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('Area6','district6',2,(select id from geographic_zones where code = 'District2'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District7','District7',3,(select id from geographic_zones where code = 'District1'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District8','district8',4,(select id from geographic_zones where code = 'District2'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District9','District9',2,(select id from geographic_zones where code = 'District2'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District10','District10',2,(select id from geographic_zones where code = 'District1'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District11','District11',3,(select id from geographic_zones where code = 'District9'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District12','District12',4,(select id from geographic_zones where code = 'District7'),900,9.90,9.90);");
    update("insert into geographic_zones (code, name, levelId, parentId, catchmentPopulation, latitude, longitude) values" +
      "('District13','District13',2,null,900,9.90,9.90);");
  }

  public void insertValuesInRequisition(boolean emergencyRequisitionRequired) throws SQLException {
    update("update requisition_line_items set beginningBalance=1,  quantityReceived=1, quantityDispensed = 1, " +
      "newPatientCount = 1, stockOutDays = 1, quantityRequested = 10, reasonForRequestedQuantity = 'bad climate', " +
      "normalizedConsumption = 10, packsToShip = 1");
    update("update requisitions set fullSupplyItemsSubmittedCost = 12.5000, nonFullSupplyItemsSubmittedCost = 0.0000");

    if (emergencyRequisitionRequired) {
      update("update requisitions set emergency='true'");
    }
  }

  public void insertValuesInRegimenLineItems(String patientsOnTreatment, String patientsToInitiateTreatment, String patientsStoppedTreatment, String remarks) throws SQLException {
    update("update regimen_line_items set patientsOnTreatment='" + patientsOnTreatment + "', patientsToInitiateTreatment='" + patientsToInitiateTreatment +
      "', patientsStoppedTreatment='" + patientsStoppedTreatment + "',remarks='" + remarks + "';");
  }

  public void updateFieldValue(String tableName, String fieldName, Integer quantity) throws SQLException {
    update("update " + tableName + " set " + fieldName + "=" + quantity + ";");
  }

  public void updateFieldValue(String tableName, String fieldName, String value, String queryParam, String queryValue) throws SQLException {
    if (queryParam == null)
      update("update " + tableName + " set " + fieldName + "='" + value + "';");
    else
      update("update " + tableName + " set " + fieldName + "='" + value + "' where " + queryParam + "='" + queryValue + "';");
  }

  public void updateFieldValue(String tableName, String fieldName, boolean value) throws SQLException {
    update("update " + tableName + " set " + fieldName + "=" + value + ";");
  }

  public void updateRequisitionStatus(String status, String username, String program) throws SQLException {
    update("update requisitions set status='" + status + "';");
    ResultSet rs = query("select id from requisitions where programId = (select id from programs where code='" + program + "');");
    while (rs.next()) {
      update("insert into requisition_status_changes(rnrId, status, createdBy, modifiedBy) values(" + rs.getString("id") + ", '" + status + "', " +
        "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'));");
    }
    update("update requisitions set supervisoryNodeId = (select id from supervisory_nodes where code='N1');");
    update("update requisitions set createdBy= (select id from users where username = '" + username + "') , modifiedBy= (select id from users where username = '" + username + "');");
  }

  public void updateRequisitionStatusByRnrId(String status, String username, int rnrId) throws SQLException {
    update("update requisitions set status='" + status + "' where id=" + rnrId + ";");
    update("insert into requisition_status_changes(rnrId, status, createdBy, modifiedBy) values(" + rnrId + ", '" + status + "', " +
      "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'));");

    update("update requisitions set supervisoryNodeId = (select id from supervisory_nodes where code='N1');");
    update("update requisitions set createdBy= (select id from users where username = '" + username + "') , modifiedBy= (select id from users where username = '" + username + "');");
  }

  public void updateRequisitionStatus(String status) throws SQLException {
    update("update requisitions set status='" + status + "';");
    ResultSet rs = query("select id from requisitions ;");
    while (rs.next()) {
      update("insert into requisition_status_changes(rnrId, status, createdBy, modifiedBy) values(" + rs.getString("id") + ", '" + status + "', " +
        "(select id from users where username = 'Admin123'), (select id from users where username = 'Admin123'));");
    }
    update("update requisitions set supervisorynodeid=(select id from supervisory_nodes where code='N1');");
    update("update requisitions set createdBy= (select id from users where username = 'Admin123') , modifiedBy= (select id from users where username = 'Admin123');");
  }


  public String getSupplyFacilityName(String supervisoryNode, String programCode) throws SQLException {
    String facilityName = null;
    ResultSet rs = query("select name from facilities where id=" +
      "(select supplyingFacilityId from supply_lines where supervisoryNodeId=" +
      "(select id from supervisory_nodes where code='" + supervisoryNode + "') and programId = " +
      "(select id from programs where code='" + programCode + "'));");

    if (rs.next()) {
      facilityName = rs.getString("name");
    }
    return facilityName;
  }

  public int getMaxRnrID() throws SQLException {
    int rnrId = 0;
    ResultSet rs = query("select max(id) from requisitions");
    if (rs.next()) {
      rnrId = Integer.parseInt(rs.getString("max"));
    }
    return rnrId;
  }

  public void setupMultipleProducts(String program, String facilityType, int numberOfProductsOfEachType, boolean defaultDisplayOrder) throws SQLException {
    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String productCodeFullSupply = "F";
    String productCodeNonFullSupply = "NF";

    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    ResultSet rs = query("Select id from product_categories where code='C1';");

    int categoryId = 0;
    if (rs.next()) {
      categoryId = rs.getInt("id");
    }

    String insertSql;
    insertSql = "INSERT INTO products (code, alternateItemCode, manufacturer, manufacturerCode, manufacturerBarcode, mohBarcode, gtin, " +
      "type, primaryName, fullName, genericName, alternateName, description, strength, formId, dosageUnitId, dispensingUnit, " +
      "dosesPerDispensingUnit, packSize, alternatePackSize, storeRefrigerated, storeRoomTemperature, hazardous, flammable, " +
      "controlledSubstance, lightSensitive, approvedByWho, contraceptiveCyp, packLength, packWidth, packHeight, packWeight, " +
      "packsPerCarton, cartonLength, cartonWidth, cartonHeight, cartonsPerPallet, expectedShelfLife, specialStorageInstructions, " +
      "specialTransportInstructions, active, fullSupply, tracer, packRoundingThreshold, roundToZero, archived) " +
      "values";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {

      insertSql = insertSql + "('" + productCodeFullSupply + i + "', 'a', 'Glaxo and Smith', 'a', 'a', 'a', 'a', 'antibiotic', 'antibiotic', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', '300/200/600', 2, 1, 'Strip', 10, 10, 30, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 1, 2.2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 'a', 'a', TRUE, TRUE, TRUE, 1, FALSE, TRUE),\n";
      insertSql = insertSql + "('" + productCodeNonFullSupply + i + "', 'a', 'Glaxo and Smith', 'a', 'a', 'a', 'a', 'antibiotic', 'antibiotic', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', 'TDF/FTC/EFV', '300/200/600', 2, 1, 'Strip', 10, 10, 30, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 1, 2.2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 'a', 'a', TRUE, FALSE, TRUE, 1, FALSE, TRUE),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";
    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeFullSupply + i + "'), 30, 12.5, true, 1," + categoryId + "),\n";
        insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeNonFullSupply + i + "'), 30, 12.5, true, 1," + categoryId + "),\n";
      } else {
        insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeFullSupply + i + "'), 30, 12.5, true, " + i + "," + categoryId + "),\n";
        insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeNonFullSupply + i + "'), 30, 12.5, true, " + i + "," + categoryId + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);

    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + productCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + productCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);
  }

  public void setupMultipleCategoryProducts(String program, String facilityType, int numberOfCategories, boolean defaultDisplayOrder) throws SQLException {
    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String productCodeFullSupply = "F";
    String productCodeNonFullSupply = "NF";

    String insertSql = "INSERT INTO product_categories (code, name, displayOrder) values\n";
    for (int i = 0; i < numberOfCategories; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "',1),\n";
      } else {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "'," + i + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);

    insertSql = "INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "('" + productCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                 FALSE,      TRUE),\n";
      insertSql = insertSql + "('" + productCodeNonFullSupply + i + "',  'a',             'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,      TRUE,         1,                 FALSE,      TRUE),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";
    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active, displayOrder, productCategoryId) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeFullSupply + i + "'), 30, 12.5, true, 1, (select id from product_categories where code='C" + i + "')),\n";
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + productCodeNonFullSupply + i + "'), 30, 12.5, true, 1, (select id from product_categories where code='C" + i + "')),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);

    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + productCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + productCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);
  }

  public void assignRight(String roleName, String roleRight) throws SQLException {
    update("INSERT INTO role_rights (roleId, rightName) VALUES" +
      " ((select id from roles where name='" + roleName + "'), '" + roleRight + "');");
  }

  public void removeAllExistingRights(String roleName) throws SQLException {
    update("delete from role_rights where roleId = ((select id from roles where name='" + roleName + "'));");
  }

  public void updatePacksToShip(String packsToShip) throws SQLException {
    update("update requisition_line_items set packsToShip='" + packsToShip + "';");
  }

  public void insertPastPeriodRequisitionAndLineItems(String facilityCode, String program, String periodName, String product) throws SQLException {
    deleteRnrData();
    update("INSERT INTO requisitions " +
      "(facilityId, programId, periodId, status) VALUES " +
      "((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), (SELECT ID from programs where code='" + program + "'), (select id from processing_periods where name='" + periodName + "'), 'RELEASED');");

    update("INSERT INTO requisition_line_items " +
      "(rnrId, productCode, beginningBalance, quantityReceived, quantityDispensed, stockInHand, normalizedConsumption, " +
      "dispensingUnit, maxMonthsOfStock, dosesPerMonth, dosesPerDispensingUnit, packSize,fullSupply) VALUES" +
      "((SELECT id FROM requisitions), '" + product + "', '0', '11' , '1', '10', '1' ,'Strip','0', '0', '0', '10','t');");
  }

  public void insertRoleAssignmentForDistribution(String userName, String roleName, String deliveryZoneCode) throws SQLException {
    update("INSERT INTO role_assignments\n" +
      "  (userId, roleId, deliveryZoneId) VALUES\n" +
      "  ((SELECT id FROM USERS WHERE username='" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'),\n" +
      "  (SELECT id FROM delivery_zones WHERE code='" + deliveryZoneCode + "'));");
  }

  public void deleteDeliveryZoneToFacilityMapping(String deliveryZoneName) throws SQLException {
    update("delete from delivery_zone_members where deliveryZoneId in (select id from delivery_zones where name='" + deliveryZoneName + "');");
  }

  public void deleteProgramToFacilityMapping(String programCode) throws SQLException {
    update("delete from programs_supported where programId in (select id from programs where code='" + programCode + "');");
  }

  public void insertDeliveryZone(String code, String name) throws SQLException {
    update("INSERT INTO delivery_zones ( code ,name)values\n" +
      "('" + code + "','" + name + "');");
  }

  public void insertWarehouseIntoSupplyLinesTable(String facilityCodeFirst, String programFirst, String supervisoryNode, boolean exportOrders) throws SQLException {
    update("INSERT INTO supply_lines (supplyingFacilityId, programId, supervisoryNodeId, description, exportOrders, createdBy, modifiedBy) values " +
      "(" + "(select id from facilities where code = '" + facilityCodeFirst + "')," + "(select id from programs where name ='" + programFirst + "')," + "(select id from supervisory_nodes where code='" + supervisoryNode + "'),'warehouse', " + exportOrders + ", '1', '1');");
  }

  public void insertDeliveryZoneMembers(String code, String facility) throws SQLException {
    update("INSERT INTO delivery_zone_members ( deliveryZoneId ,facilityId )values\n" +
      "((select id from  delivery_zones where code ='" + code + "'),(select id from  facilities where code ='" + facility + "'));");
  }

  public void insertDeliveryZoneProgramSchedule(String code, String program, String scheduleCode) throws SQLException {
    update("INSERT INTO delivery_zone_program_schedules\n" +
      "(deliveryZoneId, programId, scheduleId ) values(\n" +
      "(select id from delivery_zones where code='" + code + "'),\n" +
      "(select id from programs where code='" + program + "'),\n" +
      "(select id from processing_schedules where code='" + scheduleCode + "')\n" +
      ");");
  }

  public void insertProcessingPeriodForDistribution(int numberOfPeriodsRequired, String schedule) throws SQLException {
    for (int counter = 1; counter <= numberOfPeriodsRequired; counter++) {
      String startDate = "2013-01-0" + counter;
      String endDate = "2013-01-0" + counter;
      insertProcessingPeriod("Period" + counter, "PeriodDecs" + counter, startDate, endDate, 1, schedule);
    }
  }

  public void insertRegimenTemplateColumnsForProgram(String programName) throws SQLException {
    update("INSERT INTO program_regimen_columns(name, programId, label, visible, dataType) values\n" +
      "('code',(SELECT id FROM programs WHERE name='" + programName + "'), 'header.code',true,'regimen.reporting.dataType.text'),\n" +
      "('name',(SELECT id FROM programs WHERE name='" + programName + "'),'header.name',true,'regimen.reporting.dataType.text'),\n" +
      "('patientsOnTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients on treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('patientsToInitiateTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients to be initiated treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('patientsStoppedTreatment',(SELECT id FROM programs WHERE name='" + programName + "'),'Number of patients stopped treatment',true,'regimen.reporting.dataType.numeric'),\n" +
      "('remarks',(SELECT id FROM programs WHERE name='" + programName + "'),'Remarks',true,'regimen.reporting.dataType.text');");
  }

  public void insertRegimenTemplateConfiguredForProgram(String programName, String categoryCode, String code, String name, boolean active) throws SQLException {
    update("update programs set regimenTemplateConfigured='true' where name='" + programName + "';");
    update("INSERT INTO regimens\n" +
      "  (programId, categoryId, code, name, active,displayOrder) VALUES\n" +
      "  ((SELECT id FROM programs WHERE name='" + programName + "'), (SELECT id FROM regimen_categories WHERE code = '" + categoryCode + "'),\n" +
      "  '" + code + "','" + name + "','" + active + "',1);");
  }

  public String getAllActivePrograms() throws SQLException {
    String programsString = "";
    ResultSet rs = query("select * from programs where active=true;");

    while (rs.next()) {
      programsString = programsString + rs.getString("name");
    }
    return programsString;
  }

  public void updateProgramRegimenColumns(String programName, String regimenColumnName, boolean visible) throws SQLException {
    update("update program_regimen_columns set visible=" + visible + " where name ='" + regimenColumnName +
      "'and programId=(SELECT id FROM programs WHERE name='" + programName + "');");
  }

  public void setupOrderFileConfiguration(String filePrefix, String headerInFile) throws SQLException {
    update("DELETE FROM order_configuration;");
    update("INSERT INTO order_configuration \n" +
      "  (filePrefix, headerInFile) VALUES\n" +
      "  ('" + filePrefix + "', '" + headerInFile + "');");
  }

  public void setupOrderNumberConfiguration(String orderNumberPrefix, boolean includeOrderNumberPrefix, boolean includeProgramCode, boolean includeSequenceCode, boolean includeRnrTypeSuffix) throws SQLException {
    update("DELETE FROM order_number_configuration;");
    update("INSERT INTO order_number_configuration \n" +
      "  (orderNumberPrefix, includeOrderNumberPrefix, includeProgramCode, includeSequenceCode, includeRnrTypeSuffix) VALUES\n" +
      "  ('" + orderNumberPrefix + "', '" + includeOrderNumberPrefix + "', '" + includeProgramCode + "', '" + includeSequenceCode + "', '" + includeRnrTypeSuffix + "');");
  }

  public void setupShipmentFileConfiguration(String headerInFile) throws SQLException {
    update("DELETE FROM shipment_configuration;");
    update("INSERT INTO shipment_configuration(headerInFile) VALUES('" + headerInFile + "')");
  }

  public void setupOrderFileOpenLMISColumns(String dataFieldLabel, String includeInOrderFile, String columnLabel, int position, String Format) throws SQLException {
    update("UPDATE order_file_columns SET " +
      "includeInOrderFile='" + includeInOrderFile + "', columnLabel='" + columnLabel + "', position=" + position + ", format='" + Format + "' where dataFieldLabel='" + dataFieldLabel + "'");
  }

  public void setupOrderFileNonOpenLMISColumns(String dataFieldLabel, String includeInOrderFile, String columnLabel, int position) throws SQLException {
    update("INSERT INTO order_file_columns (dataFieldLabel, includeInOrderFile, columnLabel, position, openLMISField) " +
      "VALUES ('" + dataFieldLabel + "','" + includeInOrderFile + "','" + columnLabel + "'," + position + ", FALSE)");
  }

  public void updateProductToHaveGroup(String product, String productGroup) throws SQLException {
    update("UPDATE products set productGroupId = (SELECT id from product_groups where code = '" + productGroup + "') where code = '" + product + "'");
  }

  public void updateFieldValueToNull(String tableName, String fieldName, String queryParam, String queryValue) throws SQLException {
    if (queryParam == null)
      update("update " + tableName + " set " + fieldName + "= null;");
    else
      update("update " + tableName + " set " + fieldName + "= " + null + " where " + queryParam + "='" + queryValue + "';");
  }

  public void insertOrders(String status, String username, String program) throws SQLException {
    ResultSet rs = query("select id from requisitions where programId=(select id from programs where code='" + program + "');");
    while (rs.next()) {
      update("update requisitions set status='RELEASED' where id =" + rs.getString("id"));
      update("insert into orders(Id, status,supplyLineId, createdBy, modifiedBy, orderNumber) values(" + rs.getString("id") + ", '" + status + "', (select id from supply_lines where supplyingFacilityId = " +
        "(select facilityId from fulfillment_role_assignments where userId = " +
        "(select id from users where username = '" + username + "')) limit 1) ," +
        "(select id from users where username = '" + username + "'), (select id from users where username = '" + username + "'), '" + rs.getString("id") + "');");
    }
  }

  public void setupUserForFulfillmentRole(String username, String roleName, String facilityCode) throws SQLException {
    update("insert into fulfillment_role_assignments(userId, roleId,facilityId) values((select id from users where userName = '" + username +
      "'),(select id from roles where name = '" + roleName + "')," +
      "(select id from facilities where code = '" + facilityCode + "'));");
  }

  public Map<String, String> getFacilityVisitDetails(String facilityCode) throws SQLException {
    Map<String, String> facilityVisitsDetails = new HashMap<>();
    try (ResultSet rs = query("SELECT observations,confirmedByName,confirmedByTitle,verifiedByName,verifiedByTitle, visited, synced, " +
      "visitDate, vehicleId, reasonForNotVisiting, otherReasonDescription from facility_visits WHERE facilityId = (SELECT id FROM facilities WHERE code = '" + facilityCode + "');")) {
      while (rs.next()) {
        facilityVisitsDetails.put("observations", rs.getString("observations"));
        facilityVisitsDetails.put("confirmedByName", rs.getString("confirmedByName"));
        facilityVisitsDetails.put("confirmedByTitle", rs.getString("confirmedByTitle"));
        facilityVisitsDetails.put("verifiedByName", rs.getString("verifiedByName"));
        facilityVisitsDetails.put("verifiedByTitle", rs.getString("verifiedByTitle"));
        facilityVisitsDetails.put("visited", rs.getString("visited"));
        facilityVisitsDetails.put("visitDate", rs.getString("visitDate"));
        facilityVisitsDetails.put("vehicleId", rs.getString("vehicleId"));
        facilityVisitsDetails.put("synced", rs.getString("synced"));
        facilityVisitsDetails.put("reasonForNotVisiting", rs.getString("reasonForNotVisiting"));
        facilityVisitsDetails.put("otherReasonDescription", rs.getString("otherReasonDescription"));
      }
      return facilityVisitsDetails;
    }
  }

  public int getPODLineItemQuantityReceived(long orderId, String productCode) throws SQLException {
    try (ResultSet rs1 = query("SELECT id FROM pod WHERE OrderId = %d", orderId)) {
      if (rs1.next()) {
        int podId = rs1.getInt("id");
        ResultSet rs2 = query("SELECT quantityReceived FROM pod_line_items WHERE podId = %d and productCode='%s'", podId, productCode);
        if (rs2.next()) {
          return rs2.getInt("quantityReceived");
        }
      }
    }
    return -1;
  }

  public void setExportOrdersFlagInSupplyLinesTable(boolean flag, String facilityCode) throws SQLException {
    update("UPDATE supply_lines SET exportOrders='" + flag + "' WHERE supplyingFacilityId=(select id from facilities where code='" + facilityCode + "');");
  }

  public void enterValidDetailsInFacilityFtpDetailsTable(String facilityCode) throws SQLException {
    update(
      "INSERT INTO facility_ftp_details(facilityId, serverHost, serverPort, username, password, localFolderPath) VALUES" + "((SELECT id FROM facilities WHERE code = '%s' ), '192.168.34.1', 21, 'openlmis', 'openlmis', '/ftp');",
      facilityCode);
  }

  public List<Integer> getAllProgramsOfFacility(String facilityCode) throws SQLException {
    List<Integer> l1 = new ArrayList<>();
    ResultSet rs = query(
      "SELECT programId FROM programs_supported WHERE facilityId = (SELECT id FROM facilities WHERE code ='%s')",
      facilityCode);
    while (rs.next()) {
      l1.add(rs.getInt(1));
    }
    return l1;
  }

  public String getProgramFieldForProgramIdAndFacilityCode(int programId, String facilityCode, String field) throws SQLException {
    String res = null;
    ResultSet rs = query(
      "SELECT %s FROM programs_supported WHERE programId = %d AND facilityId = (SELECT id FROM facilities WHERE code = '%s')",
      field,
      programId,
      facilityCode);

    if (rs.next()) {
      res = rs.getString(1);
    }
    return res;
  }

  public Date getProgramStartDateForProgramIdAndFacilityCode(int programId, String facilityCode) throws SQLException {
    Date date = null;
    ResultSet rs = query(
      "SELECT startDate FROM programs_supported WHERE programId = %d AND facilityId = (SELECT id FROM facilities WHERE code ='%s')",
      programId,
      facilityCode);

    if (rs.next()) {
      date = rs.getDate(1);
    }
    return date;
  }

  public void deleteCurrentPeriod() throws SQLException {
    update("delete from processing_periods where endDate>=NOW()");
  }

  public void updateProgramsSupportedByField(String field, String newValue, String facilityCode) throws SQLException {
    update("Update programs_supported set " + field + "='" + newValue + "' where facilityId=(Select id from facilities where code ='" + facilityCode + "');");
  }

  public void deleteSupervisoryRoleFromRoleAssignment() throws SQLException {
    update("delete from role_assignments where supervisoryNodeId is not null;");
  }

  public void deleteProductAvailableAtFacility(String productCode, String programCode, String facilityCode) throws SQLException {
    update("delete from facility_approved_products where facilityTypeId=(select typeId from facilities where code='" + facilityCode + "') " +
      "and programProductId=(select id from program_products where programId=(select id from programs where code='" + programCode + "')" +
      "and productId=(select id from products where code='" + productCode + "'));");
  }

  public void updateConfigureTemplateValidationFlag(String programCode, String flag) throws SQLException {
    update("UPDATE program_rnr_columns set formulaValidationRequired ='" + flag + "' WHERE programId=" +
      "(SELECT id from programs where code='" + programCode + "');");
  }

  public void updateConfigureTemplate(String programCode, String fieldName, String fieldValue, String visibilityFlag, String rnrColumnName) throws SQLException {
    update("UPDATE program_rnr_columns SET visible ='" + visibilityFlag + "', " + fieldName + "='" + fieldValue + "' WHERE programId=" +
      "(SELECT id from programs where code='" + programCode + "')" +
      "AND masterColumnId =(SELECT id from master_rnr_columns WHERE name = '" + rnrColumnName + "') ;");
  }

  public void deleteConfigureTemplate(String program) throws SQLException {
    update("DELETE FROM program_rnr_columns where programId=(select id from programs where code = '" + program + "');");
  }

  public String getRequisitionLineItemFieldValue(Long requisitionId, String field, String productCode) throws SQLException {
    String value = null;
    ResultSet rs = query("SELECT %s FROM requisition_line_items WHERE rnrId = %d AND productCode = '%s'", field, requisitionId, productCode);
    if (rs.next()) {
      value = rs.getString(field);
    }
    return value;
  }

  public void insertRoleAssignmentForSupervisoryNode(String userName, String roleName, String supervisoryNode, String programCode) throws SQLException {
    update(" INSERT INTO role_assignments (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ((SELECT id FROM users WHERE username = '" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "')," +
      " (SELECT id from programs WHERE code='" + programCode + "'), (SELECT id from supervisory_nodes WHERE code = '" + supervisoryNode + "'));");
  }

  public void insertRequisitionGroupProgramScheduleForProgramAfterDelete(String requisitionGroupCode, String programCode, String scheduleCode) throws SQLException {
    update("delete from requisition_group_program_schedules where programId=(select id from programs where code='" + programCode + "');");
    update(
      "insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values\n" +
        "((select id from requisition_groups where code='" + requisitionGroupCode + "'),(select id from programs where code='" + programCode + "')," +
        "(select id from processing_schedules where code='" + scheduleCode + "'),TRUE);"
    );
  }

  public void insertRequisitionGroupProgramScheduleForProgramWithoutDelete(String requisitionGroupCode, String programCode, String scheduleCode) throws SQLException {
    update(
      "insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values\n" +
        "((select id from requisition_groups where code='" + requisitionGroupCode + "'),(select id from programs where code='" + programCode + "')," +
        "(select id from processing_schedules where code='" + scheduleCode + "'),TRUE);"
    );
  }

  public void updateCreatedDateInRequisitionStatusChanges(String newDate, Long rnrId) throws SQLException {
    update("update requisition_status_changes SET createdDate= '" + newDate + "' WHERE rnrId=" + rnrId + ";");
  }

  public void updateSupervisoryNodeForRequisitionGroup(String requisitionGroup,
                                                       String supervisoryNodeCode) throws SQLException {
    update("update requisition_groups set supervisoryNodeId=(select id from supervisory_nodes where code='" + supervisoryNodeCode +
      "') where code='" + requisitionGroup + "';");
  }

  public void deleteSupplyLine() throws SQLException {
    update("delete from supply_lines where description='supplying node for MALARIA'");
  }

  public Map<String, String> getEpiUseDetails(String productGroupCode, String facilityCode) throws SQLException {
    return select("SELECT * FROM epi_use_line_items WHERE productGroupName = " +
      "(SELECT name FROM product_groups where code = '%s') AND facilityVisitId=(Select id from facility_visits where facilityId=" +
      "(Select id from facilities where code ='%s'));", productGroupCode, facilityCode).get(0);
  }

  public ResultSet getEpiInventoryDetails(String productCode, String facilityCode) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM epi_inventory_line_items WHERE productCode = '%s'" +
      "AND facilityVisitId=(Select id from facility_visits where facilityId=" +
      "(Select id from facilities where code ='%s'));", productCode, facilityCode);
    resultSet.next();
    return resultSet;
  }

  public Map<String, String> getFullCoveragesDetails(String facilityCode) throws SQLException {
    return select("SELECT * FROM full_coverages WHERE facilityVisitId=(Select id from facility_visits where facilityId=" +
      "(Select id from facilities where code ='%s'));", facilityCode).get(0);
  }

  public void insertBudgetData() throws SQLException {
    update("INSERT INTO budget_file_info VALUES (1,'abc.csv','f',200,'12/12/13',200,'12/12/13');");
    update(
      "INSERT INTO budget_line_items VALUES (1,(select id from processing_periods where name='current Period'),1,'01/01/2013',200,'hjhj',200,'12/12/2013',200,'12/12/2013',(select id from facilities where code='F10'),1);");

  }

  public void addRefrigeratorToFacility(String brand, String model, String serialNumber, String facilityCode) throws SQLException {
    update("INSERT INTO refrigerators(brand, model, serialNumber, facilityId, createdBy , modifiedBy) VALUES" +
      "('" + brand + "','" + model + "','" + serialNumber + "',(SELECT id FROM facilities WHERE code = '" + facilityCode + "'),1,1);");
  }

  public ResultSet getRefrigeratorReadings(String refrigeratorSerialNumber, String facilityCode) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM refrigerator_readings WHERE refrigeratorId = " +
      "(SELECT id FROM refrigerators WHERE serialNumber = '%s' AND facilityId = " +
      "(SELECT id FROM facilities WHERE code = '%s'));", refrigeratorSerialNumber, facilityCode);
    resultSet.next();
    return resultSet;
  }

  public ResultSet getRefrigeratorProblems(Long readingId) throws SQLException {
    return query("SELECT * FROM refrigerator_problems WHERE readingId = %d", readingId);
  }

  public void updateProcessingPeriodByField(String field, String fieldValue, String periodName, String scheduleCode) throws SQLException {
    update("update processing_periods set " + field + "=" + fieldValue +
      " where name='" + periodName + "'" +
      " and scheduleId =" +
      "(Select id from processing_schedules where code = '" + scheduleCode + "');");
  }

  public ResultSet getRefrigeratorsData(String refrigeratorSerialNumber, String facilityCode) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM refrigerators WHERE serialNumber = '" + refrigeratorSerialNumber
      + "' AND facilityId = " + getAttributeFromTable("facilities", "id", "code", facilityCode));
    resultSet.next();
    return resultSet;
  }

  public String getAttributeFromTable(String tableName, String attribute, String queryColumn, String queryParam) throws SQLException {
    String returnValue = null;
    ResultSet resultSet = query("select * from %s where %s in ('%s');", tableName, queryColumn, queryParam);

    if (resultSet.next()) {
      returnValue = resultSet.getString(attribute);
    }
    return returnValue;
  }

  public String getAttributeFromTable(String tableName, String attribute, Map<String, String> queryMap) throws SQLException {
    String returnValue = null;
    String query = "select * from " + tableName + " where ";
    for (Object o : queryMap.entrySet()) {
      Map.Entry mapEntry = (Map.Entry) o;
      query = query + mapEntry.getKey() + " in ('" + mapEntry.getValue() + "') and  ";
    }
    query = query.substring(0, query.length() - 6) + ";";
    ResultSet resultSet = query(query);
    if (resultSet.next()) {
      returnValue = resultSet.getString(attribute);
    }
    return returnValue;
  }

  public String getRowsCountFromDB(String tableName) throws SQLException {
    String rowCount = null;
    ResultSet rs = query("SELECT count(*) as count from " + tableName + "");

    if (rs.next()) {
      rowCount = rs.getString("count");
    }
    return rowCount;
  }

  public String getCreatedDate(String tableName, String dateFormat) throws SQLException {
    String createdDate = null;
    ResultSet rs = query("SELECT to_char(createdDate, '" + dateFormat + "' ) from " + tableName);

    if (rs.next()) {
      createdDate = rs.getString(1);
    }
    return createdDate;
  }

  public Integer getRequisitionGroupId(String facilityCode) throws SQLException {
    return Integer.parseInt(getAttributeFromTable("requisition_group_members", "requisitionGroupId", "facilityId", getAttributeFromTable("facilities", "id", "code", facilityCode)));
  }

  public void insertOneProduct(String product) throws SQLException {
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
      "('" + product + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE);\n");
  }

  public void deleteAllProducts() throws SQLException {
    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
  }

  public void insertProductsForChildCoverage() throws SQLException {
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
      "('Measles',   'a',            'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'Measles',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),\n" +
      "('BCG',       'a',            'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'BCG',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE),\n" +
      "('polio10dose','a',           'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'polio10dose',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),\n" +
      "('polio20dose','a',           'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'polio20dose',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE),\n" +
      "('penta1',     'a',           'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'penta1',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),\n" +
      "('penta10',    'a',           'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'penta10',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE);\n");

  }

  public void insertProductsForAdultCoverage() throws SQLException {
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
      "('tetanus',    'a',           'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'penta10',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE);\n");
    insertProgramProduct("tetanus", "VACCINES", "10", "true");
  }

  public ResultSet getChildCoverageDetails(String vaccination, String facilityVisitId) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM vaccination_child_coverage_line_items WHERE vaccination = '%s' " +
      "AND facilityVisitId = %s;", vaccination, facilityVisitId);
    resultSet.next();
    return resultSet;
  }

  public void insertRequisitionWithMultipleLineItems(int numberOfLineItems, String program, boolean withSupplyLine, String facilityCode,
                                                     boolean emergency) throws SQLException {
    boolean flag = true;
    update("insert into requisitions (facilityId, programId, periodId, status, emergency, fullSupplyItemsSubmittedCost, " +
      "nonFullSupplyItemsSubmittedCost, supervisoryNodeId) " +
      "values ((Select id from facilities where code='" + facilityCode + "'),(Select id from programs where code='" + program + "')," +
      "(Select id from processing_periods where name='Period1'), 'APPROVED', '" + emergency + "', 50.0000, 0.0000, " +
      "(select id from supervisory_nodes where code='N1'))");

    String insertSql = "INSERT INTO requisition_line_items (rnrId, productCode,product,productDisplayOrder,productCategory," +
      "productCategoryDisplayOrder, beginningBalance, quantityReceived, quantityDispensed, stockInHand, dispensingUnit, maxMonthsOfStock, " +
      "dosesPerMonth, dosesPerDispensingUnit, packSize, fullSupply,totalLossesAndAdjustments,newPatientCount,stockOutDays,price," +
      "roundToZero,packRoundingThreshold,packsToShip) VALUES";

    for (int i = 0; i < numberOfLineItems; i++) {
      String programProductId = null;
      ResultSet rs = (query("select id from program_products where programId= (Select id from programs " +
        "where code='" + program + "') and productId = (Select id from products where code='F" + i + "');"));
      if (rs.next())
        programProductId = rs.getString("id");

      String productDisplayOrder = getAttributeFromTable("program_products", "displayOrder", "id", programProductId);
      String categoryId = getAttributeFromTable("program_products", "productCategoryId", "id", programProductId);
      String categoryCode = getAttributeFromTable("product_categories", "code", "id", categoryId);
      String categoryDisplayOrder = getAttributeFromTable("product_categories", "displayOrder", "id", categoryId);

      update(insertSql + "((SELECT max(id) FROM requisitions), 'F" + i + "','antibiotic Capsule 300/200/600 mg', %s, '%s', %s, '0', '11' , " +
        "'1', '10' ,'Strip','3', '30', '10', '10','t',0,0,0,12.5000,'f',1,5);", productDisplayOrder, categoryCode, categoryDisplayOrder);

      update(insertSql + "((SELECT max(id) FROM requisitions), 'NF" + i + "','antibiotic Capsule 300/200/600 mg', %s, '%s', %s, '0', '11' ," +
        " '1', '10' ,'Strip','3', '30', '10', '10','f',0,0,0,12.5000,'f',1,50);", productDisplayOrder, categoryCode, categoryDisplayOrder);
    }
    if (withSupplyLine) {
      ResultSet rs1 = query("select * from supply_lines where supervisoryNodeId = " +
        "(select id from supervisory_nodes where code = 'N1') and programId = " +
        "(select id from programs where code='" + program + "') and supplyingFacilityId = " +
        "(select id from facilities where code = 'F10')");

      if (rs1.next()) {
        flag = false;
      }
    }
    if (withSupplyLine) {
      if (flag) {
        insertSupplyLines("N1", program, "F10", true);
      }
    }
  }

  public void convertRequisitionToOrder(int maxRnrID, String orderStatus, String userName) throws SQLException {
    update("update requisitions set status = 'RELEASED' where id = %d", maxRnrID);
    String supervisoryNodeId = getAttributeFromTable("supervisory_nodes", "id", "code", "N1");
    Integer supplyingLineId = Integer.valueOf(getAttributeFromTable("supply_lines", "id", "supervisoryNodeId", supervisoryNodeId));
    Integer userId = Integer.valueOf(getAttributeFromTable("users", "id", "username", userName));
    update("INSERT INTO orders(id, status, ftpComment, supplyLineId, createdBy, modifiedBy, orderNumber) VALUES " +
      "(%d, '%s', %s, %d, %d, %d, %s)", maxRnrID, orderStatus, null, supplyingLineId, userId, userId, "'" +
      getOrderNumber("O", "MALARIA", "R") + "'");
  }

  private String getOrderNumber(String prefix, String program, String type) throws SQLException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = getMaxRnrID();
    return prefix + program.substring(0, Math.min(program.length(), 35)) + numberFormat.format(id) + type.substring(0, 1);
  }

  public void deleteTable(String tableName) throws SQLException {
    update("delete from " + tableName);
  }

  public void updatePopulationOfFacility(String facility, String population) throws SQLException {
    update("update facilities set catchmentPopulation=" + population + " where code='" + facility + "';");
  }

  public void insertShipmentData(int orderID, String productCode, Integer quantityShipped, Integer packsToShip, Boolean fullSupplyFlag) throws SQLException {
    String programId = getAttributeFromTable("requisitions", "programId", "id", String.valueOf(orderID));
    String programCode = getAttributeFromTable("programs", "code", "id", String.valueOf(programId));
    String programProductId;
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    Integer categoryDisplayOrder = null;
    Integer productDisplayOrder = null;
    String categoryName;
    String orderNumber = "O" + programCode.substring(0, Math.min(programCode.length(), 35)) + numberFormat.format(orderID) + "R";
    ResultSet rs = (query("select id from program_products where programId=" + programId + " and productId = (Select id from products where code='" + productCode + "');"));
    if (rs.next()) {
      programProductId = rs.getString("id");


      if (getAttributeFromTable("program_products", "displayOrder", "id", programProductId) != null) {
        productDisplayOrder = Integer.parseInt(getAttributeFromTable("program_products", "displayOrder", "id", programProductId));
      }

      String categoryId = getAttributeFromTable("program_products", "productCategoryId", "id", programProductId);
      categoryName = getAttributeFromTable("product_categories", "name", "id", categoryId);

      if (getAttributeFromTable("product_categories", "displayOrder", "id", categoryId) != null) {
        categoryDisplayOrder = Integer.parseInt(getAttributeFromTable("product_categories", "displayOrder", "id", categoryId));
      }

      update("INSERT INTO shipment_line_items(orderNumber,productCode,quantityShipped,productName,dispensingUnit,productCategory," +
          "productDisplayOrder,productCategoryDisplayOrder,packsToShip,fullSupply,orderId) VALUES ('%s', '%s', %d, %s, %s, '%s',%d ," +
          "%d, %d, %b, %d)", orderNumber, productCode, quantityShipped, "'antibiotic Capsule 300/200/600 mg'", "'Strip'", categoryName,
        productDisplayOrder, categoryDisplayOrder, packsToShip, fullSupplyFlag, orderID
      );
    } else {
      update("INSERT INTO shipment_line_items(orderNumber,productCode,quantityShipped,productName,dispensingUnit,packsToShip,fullSupply," +
          "orderId) VALUES ('%s', '%s', %d, %s, %s, %d, %b, %d)", orderNumber, productCode, quantityShipped, "'antibiotic Capsule 300/200/600 mg'",
        "'Strip'", packsToShip, fullSupplyFlag, orderID
      );
    }
  }

  public void insertShipmentDataWithReplacedProduct(int orderID, String productCode, Integer quantityShipped,
                                                    String replacedProductCode, Integer packsToShip, Boolean fullSupplyFlag) throws SQLException {
    String programId = getAttributeFromTable("requisitions", "programId", "id", String.valueOf(orderID));
    String programProductId = null;
    ResultSet rs = (query("select id from program_products where programId=" + programId + " and productId = (Select id from products where code='" + productCode + "');"));
    if (rs.next())
      programProductId = rs.getString("id");

    Integer productDisplayOrder = Integer.parseInt(getAttributeFromTable("program_products", "displayOrder", "id", programProductId));
    String categoryId = getAttributeFromTable("program_products", "productCategoryId", "id", programProductId);
    String categoryName = getAttributeFromTable("product_categories", "name", "id", categoryId);
    Integer categoryDisplayOrder = Integer.parseInt(getAttributeFromTable("product_categories", "displayOrder", "id", categoryId));
    update("INSERT INTO shipment_line_items(orderId,productCode,quantityShipped,productName,dispensingUnit,productCategory,productDisplayOrder,productCategoryDisplayOrder,replacedProductCode,packsToShip,fullSupply,orderNumber) VALUES (%d, '%s', %d, %s, %s, '%s',%d ,%d, '%s', %d, %b, %s)", orderID, productCode, quantityShipped, "'antibiotic Capsule 300/200/600 mg'", "'Strip'", categoryName, productDisplayOrder, categoryDisplayOrder, replacedProductCode, packsToShip, fullSupplyFlag, "'ORD'");
  }

  public Integer getProductId(String productCode) throws SQLException {
    ResultSet rs = query("select id from products where code = '" + productCode + "';");
    rs.next();
    return rs.getInt("id");
  }

  public Map<String, String> getPodLineItemFor(Integer orderId, String productCode) throws SQLException {

    return select("select * from pod_line_items where productCode = '%s' AND podId =(Select id from pod where orderId= %d )", productCode, orderId).get(0);
  }

  public void insertTargetGroupEntityAndProductsInMappingTable(String targetGroupEntity, String productCode, boolean isChildCoverageMapping) throws SQLException {
    update("INSERT INTO coverage_target_group_products (targetGroupEntity, productCode, childCoverage) values ('%s' ,'%s' , %s)", targetGroupEntity, productCode, isChildCoverageMapping);
  }

  public Map<String, String> getDistributionDetails(String deliveryZoneName, String programName, String periodName) throws SQLException {
    return select("select * from distributions where deliveryZoneId =(Select id from delivery_zones where name= '%s')AND programId = (Select id from programs where name = '%s') AND periodId=(Select id from processing_periods where name= '%s' )", deliveryZoneName, programName, periodName).get(0);
  }

  public void updateOrderStatus(String orderStatus) throws SQLException {
    update("UPDATE orders set status = '" + orderStatus + "' WHERE id = " + getMaxRnrID());
  }

  public void insertChildCoverageProductVial(String vial, String productCode) throws SQLException {
    update("INSERT INTO coverage_product_vials (vial, productCode, childCoverage) values ('%s' ,'%s', TRUE)", vial, productCode);
  }

  public ResultSet getChildOpenedVialLineItem(String productVialName, String facilityVisitId) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM child_coverage_opened_vial_line_items WHERE productVialName = '%s' " +
      "AND facilityVisitId = %s;", productVialName, facilityVisitId);
    resultSet.next();
    return resultSet;
  }

  public void insertAdultCoverageOpenedVialMapping(String productCode) throws SQLException {
    update("INSERT INTO coverage_product_vials (vial, productCode, childCoverage) values ('Tetanus' ,'%s', FALSE)", productCode);
  }

  public Map<String, String> getPodData(Integer orderId) throws SQLException {
    return select("select * from pod where orderId = %d ", orderId).get(0);
  }

  public ResultSet getAdultCoverageDetails(String demographicGroup, String facilityVisitId) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM vaccination_adult_coverage_line_items WHERE demographicGroup = '%s' " +
      "AND facilityVisitId = %s;", demographicGroup, facilityVisitId);
    resultSet.next();
    return resultSet;
  }

  public ResultSet getAdultOpenedVialLineItem(String facilityVisitId) throws SQLException {
    ResultSet resultSet = query("SELECT * FROM adult_coverage_opened_vial_line_items WHERE facilityVisitId = %s;", facilityVisitId);
    resultSet.next();
    return resultSet;
  }

  public void updateProgramProducts(String productCode, String programCode, String field, String value) throws SQLException {
    String productId = getAttributeFromTable("products", "id", "code", productCode);
    String programId = getAttributeFromTable("programs", "id", "code", programCode);
    update("UPDATE program_products SET %s = %s WHERE programId = %s and productId = %s;", field, value, programId, productId);
  }

  public void insertRequisitionGroupMembersTestData() throws SQLException {
    insertRequisitionGroupMember("RG1", "F10");
    insertRequisitionGroupMember("RG1", "F11");
    insertRequisitionGroupMember("RG2", "F10");
    insertRequisitionGroupMember("RG2", "F11");
    insertRequisitionGroupMember("RG3", "F10");
    insertRequisitionGroupMember("RG4", "F10");
    insertRequisitionGroupMember("RG5", "F11");
    insertRequisitionGroupMember("RG6", "F11");
    insertRequisitionGroupMember("RG7", "F10");
    insertRequisitionGroupMember("RG7", "F11");
    insertRequisitionGroupMember("RG8", "F10");
    insertRequisitionGroupMember("RG9", "F10");
    insertRequisitionGroupMember("RG10", "F10");
    insertRequisitionGroupMember("RG11", "F10");
    insertRequisitionGroupMember("RG11", "F11");
    insertRequisitionGroupMember("RG12", "F10");
    insertRequisitionGroupMember("RG12", "F11");
    insertRequisitionGroupMember("RG13", "F10");
    insertRequisitionGroupMember("RG14", "F10");
    insertRequisitionGroupMember("RG15", "F10");
    insertRequisitionGroupMember("RG16", "F10");
    insertRequisitionGroupMember("RG17", "F11");
    insertRequisitionGroupMember("RG18", "F10");
    insertRequisitionGroupMember("RG18", "F11");
    insertRequisitionGroupMember("RG19", "F10");
    insertRequisitionGroupMember("RG20", "F10");
  }

  public void insertAllAdminRightsAsSeedData() throws SQLException {
    update("INSERT INTO role_rights (roleId, rightName) VALUES" +
      " ((select id from roles where name='Admin'), 'UPLOADS')," +
      " ((select id from roles where name='Admin'), 'MANAGE_FACILITY')," +
      " ((select id from roles where name='Admin'), 'MANAGE_ROLE')," +
      " ((select id from roles where name='Admin'), 'MANAGE_PROGRAM_PRODUCT')," +
      " ((select id from roles where name='Admin'), 'MANAGE_SCHEDULE')," +
      " ((select id from roles where name='Admin'), 'CONFIGURE_RNR')," +
      " ((select id from roles where name='Admin'), 'MANAGE_USER')," +
      " ((select id from roles where name='Admin'), 'MANAGE_REPORT')," +
      " ((select id from roles where name='Admin'), 'SYSTEM_SETTINGS')," +
      " ((select id from roles where name='Admin'), 'MANAGE_REGIMEN_TEMPLATE');");
  }

  public void insertConsistencyReportsViewRights(String roleName) throws SQLException {
    update("INSERT INTO role_rights (roleId, rightName) VALUES" +
      " ((select id from roles where name='" + roleName + "'), 'Facilities Missing Supporting Requisition Group')," +
      " ((select id from roles where name='" + roleName + "'), 'Facilities Missing Create Requisition Role')," +
      " ((select id from roles where name='" + roleName + "'), 'Facilities Missing Authorize Requisition Role')," +
      " ((select id from roles where name='" + roleName + "'), 'Supervisory Nodes Missing Approve Requisition Role')," +
      " ((select id from roles where name='" + roleName + "'), 'Requisition Groups Missing Supply Line')," +
      " ((select id from roles where name='" + roleName + "'), 'Order Routing Inconsistencies')," +
      " ((select id from roles where name='" + roleName + "'), 'Delivery Zones Missing Manage Distribution Role');");
  }

  public List<String> getListOfRightsForRole(String roleName) throws SQLException {
    ResultSet rs = query("SELECT rightName FROM role_rights WHERE roleId = (select id from roles where name = '%s');", roleName);
    List<String> rights = new ArrayList<>();
    while (rs.next()) {
      rights.add(rs.getString(1));
    }
    return rights;
  }
}