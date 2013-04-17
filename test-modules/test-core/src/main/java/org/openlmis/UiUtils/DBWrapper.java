/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    connection.createStatement().executeUpdate(sql);
  }

  private ResultSet query(String sql) throws SQLException {
    try (Connection con = getConnection()) {
      return con.createStatement().executeQuery(sql);
    }
  }

  public void insertUser(String userId, String userName, String password, String facilityCode, String email) throws SQLException, IOException {
    update("delete from users where userName like('" + userName + "');");

    update("INSERT INTO users\n" +
      "  (id, userName, password,vendorId, facilityId, firstName, lastName, email, active) VALUES\n" +
      "  ('" + userId + "', '" + userName + "', '" + password + "',(SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = '" + facilityCode + "'), 'Fatima', 'Doe', '" + email + "','true');\n");


  }

  public void updateUser(String password, String email) throws SQLException, IOException {
    update("DELETE FROM user_password_reset_tokens;");
    update("update users set password='" + password + "', active=TRUE  where email='" + email + "';");
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
      "('" + facility1 + "','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,2,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),\n" +
      "('" + facility2 + "','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');\n");

    update("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility1 + "'), 2, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 1, '11/11/12', true, 1),\n" +
      "((SELECT id FROM facilities WHERE code = '" + facility2 + "'), 2, '11/11/12', true, 1);");

  }

  public void allocateFacilityToUser(String userId, String facilityCode) throws IOException, SQLException {
    update("update users set facilityId = (Select id from facilities where code='" + facilityCode + "') where id='" + userId + "';");

  }


  public void deleteData() throws SQLException, IOException {
    update("delete from role_rights where roleid not in(1);");
    update("delete from role_assignments where userid not in (1);");
    update("delete from roles where name not in ('Admin');");
    update("delete from facility_approved_products;");
    update("delete from program_product_price_history;");
    update("delete from program_products;");
    update("DELETE FROM requisition_status_changes;");
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("delete from products;");
    update("delete from user_password_reset_tokens ;");
    update("delete from comments;");
    update("delete from users where userName not like('Admin%');");
    update("DELETE FROM requisition_line_item_losses_adjustments;");
    update("DELETE FROM requisition_line_items;");
    update("DELETE FROM requisitions;");
    update("delete from supply_lines;");
    update("delete from supervisory_nodes;");
    update("delete from programs_supported;");
    update("delete from requisition_group_members;");
    update("delete from facilities;");
    update("delete from geographic_zones where code not in ('Root','Arusha','Dodoma', 'Ngorongoro');");
    update("delete from programs_supported;");
    update("delete from program_rnr_columns;");
    update("delete from requisition_group_program_schedules ;");
    update("delete from requisition_groups;");
    update("delete from requisition_group_members;");
    update("delete from processing_periods;");
    update("delete from processing_schedules;");

  }


  public void insertRole(String role, String adminrole, String description) throws SQLException, IOException {
    ResultSet rs = query("Select id from roles;");

    update("INSERT INTO roles\n" +
      " (name,adminrole, description) VALUES\n" +
      " ('" + role + "', '" + adminrole + "', '" + description + "');");

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

  public void updateRoleAssignment(String userID, String supervisoryNode) throws SQLException, IOException {
    update("update role_assignments set supervisorynodeid=(select id from supervisory_nodes where code='" + supervisoryNode + "') where userid='" + userID + "';");
  }

  public void updateRoleGroupMember(String facilityCode) throws SQLException, IOException {
    update("update requisition_group_members set facilityid=(select id from facilities where code ='" + facilityCode + "') where requisitiongroupid=(select id from requisition_groups where code='RG2');");
    update("update requisition_group_members set facilityid=(select id from facilities where code ='F11') where requisitiongroupid=(select id from requisition_groups where code='RG1');");
  }

  public void alterUserID(String userName, String userId) throws SQLException, IOException {
    update("delete from user_password_reset_tokens;");
    update("delete from users where id='"+userId+"' ;");
    update(" update users set id='" + userId + "' where username='"+userName+"'");
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
        update("INSERT INTO processing_schedules(code, name, description) values('"+scheduleCode+"', '"+scheduleName+"', '"+scheduleDesc+"');");
    }

    public void insertProcessingPeriod(String periodName, String periodDesc, String periodStartDate, String periodEndDate, Integer numberOfMonths, String scheduleId) throws SQLException, IOException {
        update("INSERT INTO processing_periods\n" +
                "(name, description, startDate, endDate, numberofmonths, scheduleId, modifiedBy) VALUES\n" +
                "('"+periodName+"', '"+periodDesc+"', '"+periodStartDate+"', '"+periodEndDate+"', "+numberOfMonths+", (SELECT id FROM processing_schedules WHERE code = '"+scheduleId+"'), (SELECT id FROM users LIMIT 1));");
    }


  public void configureTemplate(String program) throws SQLException, IOException {
    update("INSERT INTO program_rnr_columns\n" +
      "(masterColumnId, programId, visible, source, position, label) VALUES\n" +
      "(1, (select id from programs where code = '" + program + "'),  true, 'R', 1,  'Product Code'),\n" +
      "(2, (select id from programs where code = '" + program + "'),  true, 'R', 2,  'Product'),\n" +
      "(3, (select id from programs where code = '" + program + "'),  true, 'R', 3,  'Unit/Unit of Issue'),\n" +
      "(4, (select id from programs where code = '" + program + "'),  true, 'U', 4,  'Beginning Balance'),\n" +
      "(5, (select id from programs where code = '" + program + "'),  true, 'U', 5,  'Total Received Quantity'),\n" +
      "(6, (select id from programs where code = '" + program + "'),  true, 'U', 6,  'Total Consumed Quantity'),\n" +
      "(7, (select id from programs where code = '" + program + "'),  true, 'U', 7,  'Total Losses / Adjustments'),\n" +
      "(8, (select id from programs where code = '" + program + "'),  true, 'C', 8,  'Stock on Hand'),\n" +
      "(9, (select id from programs where code = '" + program + "'),  true, 'U', 9, 'New Patients'),\n" +
      "(10, (select id from programs where code = '" + program + "'), true, 'U', 10, 'Total Stockout days'),\n" +
      "(11, (select id from programs where code = '" + program + "'), true, 'C', 11, 'Adjusted Total Consumption'),\n" +
      "(12, (select id from programs where code = '" + program + "'), true, 'C', 12, 'Average Monthly Consumption(AMC)'),\n" +
      "(13, (select id from programs where code = '" + program + "'), true, 'C', 13, 'Maximum Stock Quantity'),\n" +
      "(14, (select id from programs where code = '" + program + "'), true, 'C', 14, 'Calculated Order Quantity'),\n" +
      "(15, (select id from programs where code = '" + program + "'), true, 'U', 15, 'Requested quantity'),\n" +
      "(16, (select id from programs where code = '" + program + "'), true, 'U', 16, 'Requested quantity explanation'),\n" +
      "(17, (select id from programs where code = '" + program + "'), true, 'U', 17, 'Approved Quantity'),\n" +
      "(18, (select id from programs where code = '" + program + "'), true, 'C', 18, 'Packs to Ship'),\n" +
      "(19, (select id from programs where code = '" + program + "'), true, 'R', 19, 'Price per pack'),\n" +
      "(20, (select id from programs where code = '" + program + "'), true, 'C', 20, 'Total cost'),\n" +
      "(21, (select id from programs where code = '" + program + "'), true, 'U', 21, 'Remarks');");
  }

  public String getFacilityIDDB() throws IOException, SQLException {
    String id = null;
    ResultSet rs = query("select id from facilities order by modifiedDate DESC limit 1");

    if (rs.next()) {
      id = rs.getString("id");
    }
    return id;
  }

  public String getFacilityID(String facilityCode) throws IOException, SQLException {
    String id = null;
    ResultSet rs = query("select id from facilities where code='" + facilityCode + "';");

    if (rs.next()) {
      id = rs.getString("id");
    }
    return id;
  }


  public String getPeriodID(String periodName) throws IOException, SQLException {
    String id = null;
    ResultSet rs = query("select id from processing_periods where name='" + periodName + "';");

    if (rs.next()) {
      id = rs.getString("id");
    }
    return id;
  }

  public String getRequisitionId() throws IOException, SQLException {
    String id = null;
    ResultSet rs = query("select id from requisitions;");

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

  public void updateRequisition(String facilityCode) throws IOException, SQLException {
    update("update requisitions set supplyingfacilityid=(select id from facilities where code='" + facilityCode + "');");

  }

  public void insertValuesInRequisition() throws IOException, SQLException {
    update("update requisition_line_items set beginningbalance=1,  quantityreceived=1, quantitydispensed=1, newpatientcount=1, stockoutdays=1, quantityrequested=10, reasonforrequestedquantity='bad climate';");

  }

  public void insertApprovedQuantity() throws IOException, SQLException {
    update("update requisition_line_items set quantityapproved='20';");

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

    public void setupMultipleProducts(String program, String facilityType, int numberOfProductsOfEachType,boolean defaultDisplayOrder) throws SQLException, IOException {

        update("delete from facility_approved_products;");
        update("delete from program_products;");
        update("delete from products;");
        update("delete from product_categories;");

        String iniProductCodeNonFullSupply="NF";

        String iniProductCodeFullSupply="F";

        update("INSERT INTO product_categories (code, name, displayOrder) values ('C1', 'Antibiotics', 1);");
        ResultSet rs = query("Select id from product_categories where code='C1';");

        int categoryId=0;
        if (rs.next()) {
            categoryId = rs.getInt("id");
        }

        String insertSql;

        insertSql="INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

        for (int i=0;i<numberOfProductsOfEachType ;i++)
        {
            if (defaultDisplayOrder)
            {
                insertSql=insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    1, " + categoryId + "),\n";
                insertSql=insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    1, " + categoryId + "),\n";
            }
            else
            {
                insertSql=insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
                insertSql=insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,       TRUE,         1,                FALSE,      TRUE,    " + i + ", " + categoryId + "),\n";
             }
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";\n";

        update(insertSql);

        insertSql= "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

        for (int i=0;i<numberOfProductsOfEachType ;i++)
        {
            insertSql=insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply +i +"'), 30, 12.5, true),\n";
            insertSql=insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply +i +"'), 30, 12.5, true),\n";
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";";


        update(insertSql);


        insertSql= "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

        for (int i=0;i<numberOfProductsOfEachType ;i++)
        {
            insertSql=insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
            insertSql=insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";";

        update(insertSql);

    }

    public void setupMultipleCategoryProducts(String program, String facilityType, int numberOfCategories,boolean defaultDisplayOrder) throws SQLException, IOException {

        update("delete from facility_approved_products;");
        update("delete from program_products;");
        update("delete from products;");
        update("delete from product_categories;");

        String insertSql="";
        String iniProductCodeNonFullSupply="NF";
        String iniProductCodeFullSupply="F";

        insertSql="INSERT INTO product_categories (code, name, displayOrder) values\n";
        for (int i=0;i<numberOfCategories ;i++)
        {
            if (defaultDisplayOrder)
            {
                insertSql=insertSql + "('C" + i + "',  'Antibiotics" + i + "',1),\n";
                }
            else
            {
                insertSql=insertSql + "('C" + i + "',  'Antibiotics" + i +"'," + i + "),\n";
            }
        }

        insertSql=insertSql.substring(0,insertSql   .length()-2) +  ";";
        update(insertSql);



        insertSql="INSERT INTO products (code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived, displayOrder, categoryId) values\n";

        for (int i=0;i<11 ;i++)
        {
            insertSql=insertSql + "('" + iniProductCodeFullSupply + i + "',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
            insertSql=insertSql + "('" + iniProductCodeNonFullSupply + i + "',  'a',             'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     FALSE,      TRUE,         1,                 FALSE,      TRUE,    1, (select id from product_categories where code='C" + i + "')),\n";
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";\n";

        update(insertSql);

        insertSql= "INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n";

        for (int i=0;i<11 ;i++)
        {
            insertSql=insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeFullSupply +i +"'), 30, 12.5, true),\n";
            insertSql=insertSql + "((SELECT ID from programs where code='" + program + "'), (SELECT id from products WHERE code = '" + iniProductCodeNonFullSupply +i +"'), 30, 12.5, true),\n";
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";";
        update(insertSql);

        insertSql= "INSERT INTO facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) VALUES\n";

        for (int i=0;i<11 ;i++)
        {
            insertSql=insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeFullSupply + i + "')), 3),\n";
            insertSql=insertSql + "((select id from facility_types where name='" + facilityType + "'), (SELECT id FROM program_products WHERE programId=(SELECT ID from programs where code='" + program + "') AND productId=(SELECT id FROM products WHERE  code='" + iniProductCodeNonFullSupply + i + "')), 3),\n";
        }

        insertSql=insertSql.substring(0,insertSql.length()-2) +  ";";

        update(insertSql);

    }

    public void assignRight(String roleName, String roleRight) throws SQLException, IOException {
        update("INSERT INTO role_rights\n" +
                "  (roleId, rightName) VALUES\n" +
                "  ((select id from roles where name='"+roleName+"'), '"+roleRight+"');");
    }

    public void updateRoleRight(String previousRight, String newRight) throws SQLException {
        update("update role_rights set rightName='"+newRight+"' where rightName='"+previousRight+"';");
    }
}

