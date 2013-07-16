/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;

import java.io.IOException;
import java.sql.*;

public class DBWrapper {

  String baseUrl, dbUrl, dbUser, dbPassword;
  Connection connection;

  public DBWrapper(String baseUrl, String dbUrl) throws IOException, SQLException {
    this.baseUrl = baseUrl;
    this.dbUrl = dbUrl;
    dbUser = "postgres";
    dbPassword = "p@ssw0rd";
    connection = getConnection();
    loadDriver();
  }

  public void closeConnection() throws SQLException {
    if (connection != null) connection.close();
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
  }

  private void loadDriver() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException cnfe) {
      System.exit(1);

    }
  }

  private void update(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(sql);
    }
  }

  private ResultSet query(String sql) throws SQLException {
    return connection.createStatement().executeQuery(sql);
  }


  public void insertUser(String userId, String userName, String password, String facilityCode, String email, String vendorName) throws SQLException, IOException {
    update("delete from users where userName like('" + userName + "');");

    update("INSERT INTO users\n" +
      "  (id, userName, password,vendorId, facilityId, firstName, lastName, email, active) VALUES\n" +
      "  ('" + userId + "', '" + userName + "', '" + password + "',(SELECT id FROM vendors WHERE name = '" + vendorName + "'), (SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 'Fatima', 'Doe', '" + email + "','true');\n");


  }

  public void updateUser(String password, String email) throws SQLException, IOException {
    update("DELETE FROM user_password_reset_tokens;");
    update("update users set password='" + password + "', active=TRUE  where email='" + email + "';");
  }

  public String getDeliveryZoneNameAssignedToUser(String user) throws SQLException, IOException {
    String deliveryZoneName = "";
    ResultSet rs = query("select name from delivery_zones where id in(select deliveryzoneid from role_assignments where " +
      "userid=(select id from users where username='" + user + "'));\n");

    if (rs.next()) {
      deliveryZoneName = rs.getString("name");
    }
    return deliveryZoneName;
  }

  public String getRoleNameAssignedToUser(String user) throws SQLException, IOException {
    String userName = "";
    ResultSet rs = query("select name from roles where id in(select roleid from role_assignments where " +
      "userid=(select id from users where username='" + user + "'));\n");

    if (rs.next()) {
      userName = rs.getString("name");
    }
    return userName;
  }


  public void deleteFacilities() throws IOException, SQLException {
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("DELETE FROM requisitions;");
    update("DELETE FROM programs_supported;");
    update("delete from facilities;");
  }

  public void insertFacilities(String facility1, String facility2) throws IOException, SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',5,2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',5,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1);");

  }

  public void insertFacilitiesWithDifferentGeoZones(String facility1, String facility2, String geoZone1, String geoZone2) throws IOException, SQLException {
    update("INSERT INTO facilities\n" +
      "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable) values\n" +
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',(select id from geographic_zones where code='" + geoZone1 + "'),2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',(select id from geographic_zones where code='" + geoZone2 + "'),2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 5, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1);");

  }

  public void insertGeographicZone(String code, String name, String parentName) throws IOException, SQLException {
    update("insert into geographic_zones (code, name,levelid, parentid) values ('" + code + "','" + name + "',(select max(levelid) from geographic_zones)," +
      "(select id from geographic_zones where code='" + parentName + "'));");
  }

  public void allocateFacilityToUser(String userId, String facilityCode) throws IOException, SQLException {
    update("update users set facilityId = (Select id from facilities where code='" + facilityCode + "') where id='" + userId + "';");

  }

  public String getGeoLevelOfGeoZone(String geoZone) throws IOException, SQLException {
    String geoLevel = null;
    ResultSet rs = query("select name from geographic_levels where levelnumber = (select levelid from geographic_zones where code='" + geoZone + "');");

    if (rs.next()) {
      geoLevel = rs.getString("name");
    }
    return geoLevel;
  }


  public void deleteData() throws SQLException, IOException {
    update("delete from role_rights where roleid not in(1);");
    update("delete from role_assignments where userid not in (1);");
    update("delete from roles where name not in ('Admin');");
    update("delete from facility_approved_products;");
    update("delete from program_product_price_history;");

    update("delete from orders;");
    update("DELETE FROM requisition_status_changes;");

    update("delete from user_password_reset_tokens ;");
    update("delete from comments;");
    update("delete from users where userName not like('Admin%');");
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("DELETE FROM regimen_line_items;");
    update("DELETE FROM requisitions;");

    update("delete from program_product_isa;");
    update("delete from facility_approved_products;");
    update("delete from facility_program_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    update("delete from supply_lines;");
    update("delete from programs_supported;");
    update("delete from requisition_group_members;");
    update("delete from program_rnr_columns;");
    update("delete from requisition_group_program_schedules ;");
    update("delete from requisition_groups;");
    update("delete from requisition_group_members;");
    update("delete from delivery_zone_program_schedules ;");
    update("delete from delivery_zone_warehouses ;");
    update("delete from delivery_zone_members;");
    update("delete from role_assignments where deliveryzoneid in (select id from delivery_zones where code in('DZ1','DZ2'));");
    update("delete from delivery_zones;");

    update("delete from supervisory_nodes;");
    update("delete from facilities;");
    update("delete from geographic_zones where code not in ('Root','Arusha','Dodoma', 'Ngorongoro');");
    update("delete from processing_periods;");
    update("delete from processing_schedules;");
    update("delete from atomfeed.event_records;");
    update("delete from regimens;");
    update("delete from program_regimen_columns;");
  }


  public void insertRole(String role, String type, String description) throws SQLException, IOException {
    ResultSet rs = query("Select id from roles;");

    update("INSERT INTO roles\n" +
      " (name,type, description) VALUES\n" +
      " ('" + role + "', '" + type + "', '" + description + "');");

  }

  public void insertRoleRights() throws SQLException, IOException {


    update("INSERT INTO role_rights\n" +
      "  (roleId, rightName) VALUES\n" +
      "  ((select id from roles where name='store in-charge'), 'CREATE_REQUISITION'),\n" +
      "  ((select id from roles where name='store in-charge'), 'VIEW_REQUISITION'),\n" +
      "  ((select id from roles where name='store in-charge'), 'AUTHORIZE_REQUISITION'),\n" +
      "  ((select id from roles where name='store in-charge'), 'APPROVE_REQUISITION'),\n" +
      "  ((select id from roles where name='store in-charge'), 'CONVERT_TO_ORDER'),\n" +
      "  ((select id from roles where name='district pharmacist'), 'UPLOADS'),\n" +
      "  ((select id from roles where name='district pharmacist'), 'MANAGE_FACILITY'),\n" +
      "  ((select id from roles where name='district pharmacist'), 'CONFIGURE_RNR');");
  }

  public void insertSupervisoryNode(String facilityCode, String supervisoryNodeCode, String supervisoryNodeName, String supervisoryNodeParentCode) throws SQLException, IOException {
    ResultSet rs = query("Select facilityId from supervisory_nodes;");

    if (rs.next()) {
      update("delete from supervisory_nodes;");
    }
    update("INSERT INTO supervisory_nodes\n" +
      "  (parentId, facilityId, name, code) VALUES\n" +
      "  (" + supervisoryNodeParentCode + ", (SELECT id FROM facilities WHERE code = '" + facilityCode + "'), '" + supervisoryNodeName + "', '" + supervisoryNodeCode + "');");
  }

  public void insertSupervisoryNodeSecond(String facilityCode, String supervisoryNodeCode, String supervisoryNodeName, String supervisoryNodeParentCode) throws SQLException, IOException {
    update("INSERT INTO supervisory_nodes\n" +
      "  (parentId, facilityId, name, code) VALUES\n" +
      "  ((select id from  supervisory_nodes where code ='" + supervisoryNodeParentCode + "'), (SELECT id FROM facilities WHERE code = '" + facilityCode + "'), '" + supervisoryNodeName + "', '" + supervisoryNodeCode + "');");
  }

  public void insertRequisitionGroups(String code1, String code2, String supervisoryNodeCode1, String supervisoryNodeCode2) throws SQLException, IOException {
    ResultSet rs = query("Select id from requisition_groups;");

    if (rs.next()) {
      update("delete from requisition_groups;");
    }
    update("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
      "('" + code2 + "','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='" + supervisoryNodeCode2 + "')),\n" +
      "('" + code1 + "','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='" + supervisoryNodeCode1 + "'));");
  }

  public void insertRequisitionGroupMembers(String RG1facility, String RG2facility) throws SQLException, IOException {
    ResultSet rs = query("Select requisitiongroupid from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_members;");

    }
    update("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
      "((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='" + RG1facility + "')),\n" +
      "((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='" + RG2facility + "'));");
  }

  public void insertRequisitionGroupProgramSchedule() throws SQLException, IOException {
    ResultSet rs = query("Select requisitiongroupid from requisition_group_members;");

    if (rs.next()) {
      update("delete from requisition_group_program_schedules;");
    }
    update("insert into requisition_group_program_schedules ( requisitiongroupid , programid , scheduleid , directdelivery ) values\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
      "((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE);\n");
  }

  public void insertRoleAssignment(String userID, String roleName) throws SQLException, IOException {
    update("delete from role_assignments where userId='" + userID + "';");

    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));");
  }

  public void insertRoleAssignmentforSupervisoryNode(String userID, String roleName, String supervisoryNode) throws SQLException, IOException {
    update("delete from role_assignments where userId='" + userID + "';");

    update(" INSERT INTO role_assignments\n" +
      "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, null),\n" +
      "    ('" + userID + "', (SELECT id FROM roles WHERE name = '" + roleName + "'), 1, (SELECT id from supervisory_nodes WHERE code = '" + supervisoryNode + "'));");
  }

  public void updateRoleAssignment(String userID, String supervisoryNode) throws SQLException, IOException {
    update("update role_assignments set supervisorynodeid=(select id from supervisory_nodes where code='" + supervisoryNode + "') where userid='" + userID + "';");
  }

  public void updateRoleGroupMember(String facilityCode) throws SQLException, IOException {
    update("update requisition_group_members set facilityid=(select id from facilities where code ='" + facilityCode + "') where requisitiongroupid=(select id from requisition_groups where code='RG2');");
    update("update requisition_group_members set facilityid=(select id from facilities where code ='F11') where requisitiongroupid=(select id from requisition_groups where code='RG1');");
  }

  public void alterUserID(String userName, String userId) throws SQLException, IOException {
    update("delete from user_password_reset_tokens;");
    update("delete from users where id='" + userId + "' ;");
    update(" update users set id='" + userId + "' where username='" + userName + "'");
  }


  public void insertProducts(String product1, String product2) throws SQLException, IOException {

    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");


    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n" +
      "('" + product1 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, (Select id from product_categories where code='C1')),\n" +
      "('" + product2 + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                    FALSE,      TRUE,   5, (Select id from product_categories where code='C1'));\n");
  }

  public void insertProductWithCategory(String product, String productName, String category) throws SQLException, IOException {

    update("INSERT INTO product_categories (code, name, displayOrder) values ('" + category + "', '" + productName + "', 1);");
    update("INSERT INTO products\n" +
      "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n" +
      "('" + product + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    '" + productName + "', '" + productName + "',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, (Select id from product_categories where code='C1'));\n");

  }

  public void updateProgramToAPushType(String program, boolean flag) throws SQLException {
    update("update programs set push='" + flag + "' where code='" + program + "';");
  }


  public void insertProgramProducts(String product1, String product2, String program) throws SQLException, IOException {
    ResultSet rs = query("Select id from program_products;");

    if (rs.next()) {
      update("delete from facility_approved_products;");
      update("delete from program_products;");
    }

    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product1 + "'), 30, 12.5, true),\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product2 + "'), 30, 0, true);");
  }

  public void insertProgramProductsWithCategory(String product, String program) throws SQLException, IOException {


    update("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
      "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + product + "'), 30, 12.5, true);");
  }

  public void insertProgramProductISA(String program, String product, String whoratio, String dosesperyear, String wastageFactor, String bufferpercentage, String minimumvalue, String maximumvalue, String adjustmentvalue) throws SQLException, IOException {
    update("INSERT INTO program_product_isa(programproductid, whoratio, dosesperyear, wastageFactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue) VALUES\n" +
      "((SELECT ID from program_products where programid=(SELECT ID from programs where code='" + program + "') and productid= (SELECT id from products WHERE code = '" + product + "'))," + whoratio + "," + dosesperyear + "," + wastageFactor + "," + bufferpercentage + "," + minimumvalue + "," + maximumvalue + "," + adjustmentvalue + ");");
  }

  public void insertFacilityApprovedProducts(String product1, String product2, String program, String facilityType) throws SQLException, IOException {
    update("delete from facility_approved_products;");

    update("INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n" +
      "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + product1 + "')), 3),\n" +
      "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + product2 + "')), 3);");
  }

  public String fetchNonFullSupplyData(String productCode, String facilityTypeID, String programID) throws SQLException, IOException {
    ResultSet rs = query("Select p.code, p.displayOrder, p.primaryName, pf.code as ProductForm," +
      "p.strength as Strength, du.code as DosageUnit from facility_approved_products fap, " +
      "program_products pp, products p, product_forms pf , dosage_units du where fap. facilityTypeId='" + facilityTypeID + "' " +
      "and p. fullsupply=false and p.active=true and pp.programId='" + programID + "' and p. code='" + productCode + "' " +
      "and pp.productId=p.id and fap. Programproductid=pp.id and pp.active=true and pf.id=p.formid " +
      "and du.id=p.dosageunitid order by p. displayorder asc;");
    String nonFullSupplyValues = null;
    if (rs.next()) {
      nonFullSupplyValues = rs.getString("primaryname") + " " + rs.getString("productform") + " " + rs.getString("strength") + " " + rs.getString("dosageunit");
    }
    return nonFullSupplyValues;
  }

  public void insertSchedule(String scheduleCode, String scheduleName, String scheduleDesc) throws SQLException, IOException {
    update("INSERT INTO processing_schedules(code, name, description) values('" + scheduleCode + "', '" + scheduleName + "', '" + scheduleDesc + "');");
  }

  public void insertProcessingPeriod(String periodName, String periodDesc, String periodStartDate, String periodEndDate, Integer numberOfMonths, String scheduleId) throws SQLException, IOException {
    update("INSERT INTO processing_periods\n" +
      "(name, description, startDate, endDate, numberofmonths, scheduleId, modifiedBy) VALUES\n" +
      "('" + periodName + "', '" + periodDesc + "', '" + periodStartDate + "', '" + periodEndDate + "', " + numberOfMonths + ", (SELECT id FROM processing_schedules WHERE code = '" + scheduleId + "'), (SELECT id FROM users LIMIT 1));");
  }


  public void configureTemplate(String program) throws SQLException, IOException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, position, label) VALUES\n" +
      "(1, (select id from programs where code = '" + program + "'),  true, 'R', 1,  'Product Code'),\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', 2,  'Product'),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', 3,  'Unit/Unit of Issue'),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'U', 4,  'Beginning Balance'),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', 5,  'Total Received Quantity'),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'C', 6,  'Total'),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'U', 7,  'Total Consumed Quantity'),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'U', 8,  'Total Losses / Adjustments'),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'C', 9,  'Stock on Hand'),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'U', 10, 'New Patients'),\n" +
      "(11, (select id from programs where code = '" + program + "'), true, 'U', 11, 'Total Stockout days'),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'C', 12, 'Adjusted Total Consumption'),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', 14, 'Maximum Stock Quantity'),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', 15, 'Calculated Order Quantity'),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'U', 16, 'Requested quantity'),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', 17, 'Requested quantity explanation'),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', 18, 'Approved Quantity'),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'C', 19, 'Packs to Ship'),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'R', 20, 'Price per pack'),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'C', 21, 'Total cost'),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'U', 22, 'Expiration Date'),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', 23, 'Remarks');");
  }

  public void configureTemplateForCommTrack(String program) throws SQLException, IOException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, position, label) VALUES\n" +
      "(1, (select id from programs where code = '" + program + "'),  true, 'R', 1,  'Product Code'),\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', 2,  'Product'),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', 3,  'Unit/Unit of Issue'),\n" +
      "(4, (select id from programs where code = '" + program + "'),  false, 'C', 4,  'Beginning Balance'),\n" +
      "(5, (select id from programs where code = '" + program + "'),  false, 'C', 5,  'Total Received Quantity'),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'C', 6,  'Total'),\n" +
      "(7, (select id from programs where code = '" + program + "'),  false, 'C', 7,  'Total Consumed Quantity'),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'U', 8,  'Total Losses / Adjustments'),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', 9,  'Stock on Hand'),\n" +
      "(10, (select id from programs where code = '" + program + "'),  true, 'U', 10, 'New Patients'),\n" +
      "(11, (select id from programs where code = '" + program + "'), true, 'U', 11, 'Total Stockout days'),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'C', 12, 'Adjusted Total Consumption'),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', 13, 'Average Monthly Consumption(AMC)'),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', 14, 'Maximum Stock Quantity'),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'C', 15, 'Calculated Order Quantity'),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'U', 16, 'Requested quantity'),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', 17, 'Requested quantity explanation'),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'U', 18, 'Approved Quantity'),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'C', 19, 'Packs to Ship'),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'R', 20, 'Price per pack'),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'C', 21, 'Total cost'),\n" +
      "(22, (select id from programs where code = '" + program + "'), true, 'U', 22, 'Expiration Date'),\n" +
      "(23, (select id from programs where code = '" + program + "'), true, 'U', 23, 'Remarks');");
  }

  public String getFacilityName(String code) throws IOException, SQLException {
    String name = null;
    ResultSet rs = query("select name from facilities where code = '" + code + "';");

    if (rs.next()) {
      name = rs.getString("name");
    }
    return name;
  }

  public String getFacilityPopulation(String code) throws IOException, SQLException {
    String population = null;
    ResultSet rs = query("select catchmentpopulation from facilities where code = '" + code + "';");

    if (rs.next()) {
      population = rs.getString("catchmentpopulation");
    }
    return population;
  }

  public String getOverridenIsa(String facilityCode, String program, String product) throws IOException, SQLException {
    String overridenIsa = null;
    ResultSet rs = query("select overriddenisa from facility_program_products " +
      "where facilityid = '" + getFacilityID(facilityCode) + "' and programproductid = " +
      "(select id from program_products where programid='" + getProgramID(program) + "' and productid='" + getProductID(product) + "');");

    if (rs.next()) {
      overridenIsa = rs.getString("overriddenisa");
    }
    return overridenIsa;
  }

  public void InsertOverridenIsa(String facilityCode, String program, String product, int overriddenIsa) throws IOException, SQLException {
    update("INSERT INTO facility_program_products (facilityid,programproductid,overriddenisa) VALUES (" + getFacilityID(facilityCode) + "," +
      "(select id from program_products where programid='" + getProgramID(program) + "' and productid='" + getProductID(product) + "')," + overriddenIsa + ");");
  }

  public void updateOverridenIsa(String facilityCode, String program, String product, String overriddenIsa) throws IOException, SQLException {
    update("Update facility_program_products set overriddenisa=" + overriddenIsa + " where facilityid='" + getFacilityID(facilityCode) + "' and programproductid=(select id from program_products where programid='" + getProgramID(program) + "' and productid='" + getProductID(product) + "');");
  }

  public String[] getProgramProductISA(String program, String product) throws IOException, SQLException {
    String isaParams[] = new String[7];
    ;
    ResultSet rs = query("select * from program_product_Isa where " +
      " programproductid = (select id from program_products where programid='" + getProgramID(program) + "' and productid='" + getProductID(product) + "');");

    if (rs.next()) {
      isaParams[0] = rs.getString("whoratio");
      isaParams[1] = rs.getString("dosesperyear");
      isaParams[2] = rs.getString("wastageFactor");
      isaParams[3] = rs.getString("bufferpercentage");
      isaParams[4] = rs.getString("adjustmentvalue");
      isaParams[5] = rs.getString("minimumvalue");
      isaParams[6] = rs.getString("maximumvalue");
    }
    return isaParams;
  }

  public Long getFacilityID(String facilityCode) throws IOException, SQLException {
    Long id = null;
    ResultSet rs = query("select id from facilities where code='" + facilityCode + "';");

    if (rs.next()) {
      id = rs.getLong("id");
    }
    return id;
  }


  public Long getPeriodID(String periodName) throws IOException, SQLException {
    Long id = null;
    ResultSet rs = query("select id from processing_periods where name='" + periodName + "';");

    if (rs.next()) {
      id = rs.getLong("id");
    }
    return id;
  }

  public String getLatestRequisitionId() throws IOException, SQLException {
    String id = null;
    ResultSet rs = query("select id from requisitions order by createddate desc limit 1;");

    if (rs.next()) {
      id = rs.getString("id");
    }
    return id;
  }

  public String getFacilityFieldBYID(String field, String id) throws IOException, SQLException {
    String facilityField = null;
    ResultSet rs = query("select " + field + " from facilities where id=" + id + ";");

    if (rs.next()) {
      facilityField = rs.getString(1);
    }
    return facilityField;
  }

  public void insertSupplyLines(String supervisoryNode, String programCode, String facilityCode) throws IOException, SQLException {
    update("insert into supply_lines (description, supervisoryNodeId, programId, supplyingFacilityId) values\n" +
      "('supplying node for HIV', (select id from supervisory_nodes where code = '" + supervisoryNode + "'), (select id from programs where code='" + programCode + "'),(select id from facilities where code = '" + facilityCode + "'));\n");

  }

  public void updateSupplyingFacilityForRequisition(String facilityCode) throws IOException, SQLException {
    update("update requisitions set supplyingfacilityid=(select id from facilities where code='" + facilityCode + "');");

  }

  public void insertValuesInRequisition() throws IOException, SQLException {
    update("update requisition_line_items set beginningbalance=1,  quantityreceived=1, quantitydispensed=1, newpatientcount=1, stockoutdays=1, quantityrequested=10, reasonforrequestedquantity='bad climate';");

  }

  public void insertValuesInRegimenLineItems(String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks ) throws IOException, SQLException {
    update("update regimen_line_items set patientsontreatment='"+patientsontreatment+"', patientstoinitiatetreatment='"+patientstoinitiatetreatment+"', patientsstoppedtreatment='"+patientsstoppedtreatment+"',remarks='"+remarks+"';");

  }

  public void insertApprovedQuantity(Integer quantity) throws IOException, SQLException {
    update("update requisition_line_items set quantityapproved=" + quantity);

  }

  public void updateRequisitionStatus(String status) throws IOException, SQLException {
    update("update requisitions set status='" + status + "';");
    update("update requisitions set supervisorynodeid=(select id from supervisory_nodes where code='N1');");

  }

  public String getSupplyFacilityName(String supervisoryNode, String programCode) throws IOException, SQLException {
    String facilityName = null;
    ResultSet rs = query("select name from facilities where id=" +
      "(select supplyingfacilityid from supply_lines where supervisorynodeid=" +
      "(select id from supervisory_nodes where code='" + supervisoryNode + "') and programid=(select id from programs where code='" + programCode + "'));");

    if (rs.next()) {
      facilityName = rs.getString("name");
    }
    return facilityName;

  }

  public String getUserID(String userName) throws IOException, SQLException {
    String facilityID = null;
    ResultSet rs = query("select id from users where username='" + userName + "'");
    if (rs.next()) {
      facilityID = rs.getString("id");
    }
    return facilityID;

  }

  public void setupMultipleProducts(String program, String facilityType, int numberOfProductsOfEachType, boolean defaultDisplayOrder) throws SQLException, IOException {


    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String iniProductCodeNonFullSupply = "NF";

    String iniProductCodeFullSupply = "F";

    update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
    ResultSet rs = query("Select id from product_categories where code='C1';");

    int categoryId = 0;
    if (rs.next()) {
      categoryId = rs.getInt("id");
    }

    String insertSql;

    insertSql = "INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, " + categoryId + "),\n";
        insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    1, " + categoryId + "),\n";
      } else {
        insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
        insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";

    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply + i + "'), 30, 12.5, true),\n";
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply + i + "'), 30, 12.5, true),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";


    update(insertSql);


    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < numberOfProductsOfEachType; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";

    update(insertSql);

  }

  public void setupMultipleCategoryProducts(String program, String facilityType, int numberOfCategories, boolean defaultDisplayOrder) throws SQLException, IOException {

    update("delete from facility_approved_products;");
    update("delete from program_products;");
    update("delete from products;");
    update("delete from product_categories;");

    String insertSql = "";
    String iniProductCodeNonFullSupply = "NF";
    String iniProductCodeFullSupply = "F";

    insertSql = "INSERT INTO product_categories (code, name, displayOrder) values\n";
    for (int i = 0; i < numberOfCategories; i++) {
      if (defaultDisplayOrder) {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "',1),\n";
      } else {
        insertSql = insertSql + "('C" + i + "',  'Antibiotics" + i + "'," + i + "),\n";
      }
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);


    insertSql = "INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
      insertSql = insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',             'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,      TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";\n";

    update(insertSql);

    insertSql = "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply + i + "'), 30, 12.5, true),\n";
      insertSql = insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply + i + "'), 30, 12.5, true),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";
    update(insertSql);

    insertSql = "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

    for (int i = 0; i < 11; i++) {
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
      insertSql = insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
    }

    insertSql = insertSql.substring(0, insertSql.length() - 2) + ";";

    update(insertSql);

  }

  public void assignRight(String roleName, String roleRight) throws SQLException, IOException {
    update("INSERT INTO role_rights\n" +
      "  (roleId, rightName) VALUES\n" +
      "  ((select id from roles where name='" + roleName + "'), '" + roleRight + "');");
  }

  public void updateRoleRight(String previousRight, String newRight) throws SQLException {
    update("update role_rights set rightName='" + newRight + "' where rightName='" + previousRight + "';");
  }

  public String getAuthToken(String vendorName) throws IOException, SQLException {
    ResultSet rs = query("select authtoken from vendors where name='" + vendorName + "'");

    if (rs.next()) {
      return rs.getString("authtoken");
    }
    return "";

  }

  public void insertVendor(String vendorName) throws SQLException {
    update("delete from vendors where name='" + vendorName + "';");
    update("INSERT INTO VENDORS (name, active) VALUES ('" + vendorName + "', true);");
  }

  public void updatePacksToShip(String packsToShip) throws SQLException {
    update("update requisition_line_items set packstoship='" + packsToShip + "';");
  }

  public Long getProgramID(String program) throws IOException, SQLException {
    Long programID = null;
    ResultSet rs = query("SELECT ID from programs where code='" + program + "'");

    if (rs.next()) {
      programID = rs.getLong("id");
    }
    return programID;

  }

  public Long getProductID(String product) throws IOException, SQLException {
    Long productID = null;
    ResultSet rs = query("SELECT ID from products where code='" + product + "'");

    if (rs.next()) {
      productID = rs.getLong("id");
    }
    return productID;

  }

  public String getRequisitionStatus(Long requisitionId) throws IOException, SQLException {
    String requisitionStatus = null;
    ResultSet rs = query("SELECT status from requisitions where id=" + requisitionId);

    if (rs.next()) {
      requisitionStatus = rs.getString("status");
    }
    return requisitionStatus;

  }

  public String getOrderId(String requisitionId) throws IOException, SQLException {
    String orderId = null;
    ResultSet rs = query("SELECT id from orders where rnrid='" + requisitionId + "'");

    if (rs.next()) {
      orderId = rs.getString("id");
    }
    return orderId;

  }

  public void insertPastPeriodRequisitionAndLineItems(String facilityCode, String program, String periodName, String product) throws IOException, SQLException {
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("DELETE FROM requisitions;");

    update("INSERT INTO requisitions \n" +
      "  (facilityid, programid, periodid, status) VALUES\n" +
      "  ((SELECT id FROM facilities WHERE code = '" + facilityCode + "'), (SELECT ID from programs where code='" + program + "'), (select id from processing_periods where name='" + periodName + "'), 'RELEASED');");

    update("INSERT INTO requisition_line_items \n" +
      "  (rnrid, productcode, beginningbalance, quantityreceived, quantitydispensed, stockinhand, normalizedconsumption, dispensingunit, maxmonthsofstock, dosespermonth, dosesperdispensingunit,packsize,fullsupply) VALUES\n" +
      "  ((SELECT id FROM requisitions), '" + product + "', '0', '11' , '1', '10', '1' ,'Strip','0', '0', '0', '10','t');");

  }

  public String getRowsCountFromDB(String tableName) throws IOException, SQLException {
    String rowCount = null;
    ResultSet rs = query("SELECT count(*) as count from " + tableName + "");

    if (rs.next()) {
      rowCount = rs.getString("count");
    }
    return rowCount;

  }

  public void insertRoleAssignmentForDistribution(String userName, String roleName, String deliveryZoneCode) throws SQLException, IOException {

    update("INSERT INTO role_assignments\n" +
      "  (userId, roleId, deliveryZoneId) VALUES\n" +
      "  ((SELECT id FROM USERS WHERE username='" + userName + "'), (SELECT id FROM roles WHERE name = '" + roleName + "'),\n" +
      "  (SELECT id FROM delivery_zones WHERE code='" + deliveryZoneCode + "'));");
  }

  public void updatePopulationOfFacility(String facility, String population) throws SQLException, IOException {

    update("update facilities set catchmentpopulation=" + population + " where code='" + facility + "';");
  }

  public void insertDeliveryZone(String code, String name) throws SQLException {
    update("INSERT INTO delivery_zones ( code ,name)values\n" +
      "('" + code + "','" + name + "');");
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


  public void insertProcessingPeriodForDistribution(int numberOfPeriodsRequired, String schedule) throws IOException, SQLException {
    for (int counter = 1; counter <= numberOfPeriodsRequired; counter++) {
      String startDate = "2013-01-0" + counter;
      String endDate = "2013-01-0" + counter;
      insertProcessingPeriod("Period" + counter, "PeriodDecs" + counter, startDate, endDate, 1, schedule);
    }
  }

  public void updateActiveStatusOfProgram(String programCode) throws SQLException {
    update("update programs SET active='true' where code='" + programCode + "';");
  }

  public void setRegimenTemplateConfiguredForProgram(boolean flag, String programName) throws SQLException {
    update("update programs set regimentemplateconfigured='" + flag + "' where name='" + programName + "';");
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
    update("update programs set regimentemplateconfigured='true' where name='" + programName + "';");
    update("INSERT INTO regimens\n" +
      "  (programid, categoryid, code, name, active,displayorder) VALUES\n" +
      "  ((SELECT id FROM programs WHERE name='" + programName + "'), (SELECT id FROM regimen_categories WHERE code = '" + categoryCode + "'),\n" +
      "  '" + code + "','" + name + "','" + active + "',1);");
  }

  public void setRegimenTemplateConfiguredForAllPrograms(boolean flag) throws SQLException {
    update("update programs set regimentemplateconfigured='" + flag + "';");
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
    update("update program_regimen_columns set visible=" + visible + " where name ='" + regimenColumnName + "'and programid=(SELECT id FROM programs WHERE name='" + programName + "');");
  }
}
