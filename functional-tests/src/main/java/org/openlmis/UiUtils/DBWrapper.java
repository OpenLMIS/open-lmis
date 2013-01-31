package org.openlmis.UiUtils;

import java.io.IOException;
import java.sql.*;

public class DBWrapper {

    String baseUrl, dbUrl, dbUser, dbPassword;

    public DBWrapper() throws IOException {

        baseUrl = "http://localhost:9091/";
        dbUrl = "jdbc:postgresql://localhost:5432/open_lmis";
        dbUser = "postgres";
        dbPassword = "p@ssw0rd";
    }

    public ResultSet dbConnection(String Query, String indicator) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;


        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Could not find the JDBC driver!");
            System.exit(1);
        }

        String url = dbUrl;
        String user = dbUser;
        String password = dbPassword;

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            if (indicator.equalsIgnoreCase("select")) {
                rs = st.executeQuery(Query);

            } else {
                st.executeUpdate(Query);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {

            return rs;

        }
    }

    public void insertUser(String userId, String userName, String password, String facilityCode, String email) throws SQLException, IOException {
        boolean flag = false;
        DBWrapper dbwrapper = new DBWrapper();

            dbwrapper.dbConnection("delete from users where userName like('"+userName+"');", "alter");

        dbwrapper.dbConnection("INSERT INTO users\n" +
                "  (id, userName, password, facilityId, firstName, lastName, email) VALUES\n" +
                "  ('"+userId+"', '"+userName+"', '"+password+"', (SELECT id FROM facilities WHERE code = '"+facilityCode+"'), 'Jane', 'Doe', '"+email+"');\n", "alter");



    }

    public void deleteFacilities() throws IOException , SQLException
    {

        DBWrapper dbWrapper=new DBWrapper();

            dbWrapper.dbConnection("DELETE FROM requisition_line_item_losses_adjustments;", "alter");
            dbWrapper.dbConnection("DELETE FROM requisition_line_items;", "alter");
            dbWrapper.dbConnection("DELETE FROM requisitions;", "alter");
        dbWrapper.dbConnection("DELETE FROM programs_supported;", "alter");

            dbWrapper.dbConnection("delete from facilities;", "alter");

        }

    public void insertFacility() throws IOException , SQLException
    {

        DBWrapper dbWrapper=new DBWrapper();
        ResultSet rs = dbWrapper.dbConnection("Select code from facilities;", "select");

        if (rs.next()) {

            dbWrapper.dbConnection("delete from facilities;", "alter");


        }
        dbWrapper.dbConnection("INSERT INTO facilities\n" +
                "(code, name, description, gln, mainPhone, fax, address1, address2, geographicZoneId, typeId, catchmentPopulation, latitude, longitude, altitude, operatedById, coldStorageGrossCapacity, coldStorageNetCapacity, suppliesOthers, sdp, hasElectricity, online, hasElectronicScc, hasElectronicDar, active, goLiveDate, goDownDate, satellite, comment, dataReportable) values\n" +
                "('F10','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),\n" +
                "('F11','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');\n", "alter");

        dbWrapper.dbConnection("insert into programs_supported(facilityId, programId, startDate, active, modifiedBy) VALUES\n" +
            "((SELECT id FROM facilities WHERE code = 'F10'), 1, '11/11/12', true, 'Admin123'),\n" +
            "((SELECT id FROM facilities WHERE code = 'F10'), 2, '11/11/12', true, 'Admin123'),\n" +
            "((SELECT id FROM facilities WHERE code = 'F11'), 1, '11/11/12', true, 'Admin123'),\n" +
            "((SELECT id FROM facilities WHERE code = 'F11'), 2, '11/11/12', true, 'Admin123');","alter");

    }


    public void allocateFacilityToUser(String userID) throws IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("update users set facilityId = (Select id from facilities order by modifiedDate DESC limit 1) where id='"+userID+"';", "alter");

    }


    public void deleteData() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("delete from role_rights where roleid not in(1);", "alter");
        dbwrapper.dbConnection("delete from role_assignments where userid not in (1);", "alter");
        dbwrapper.dbConnection("delete from roles where name not in ('Admin');", "alter");
        dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
        dbwrapper.dbConnection("delete from program_product_price_history;", "alter");
        dbwrapper.dbConnection("delete from program_products;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_item_losses_adjustments;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_items;", "alter");
        dbwrapper.dbConnection("delete from products;", "alter");
        dbwrapper.dbConnection("delete from users where userName like('User%');", "alter");
        dbwrapper.dbConnection("delete from users where id=200;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_item_losses_adjustments;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_items;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisitions;", "alter");
        dbwrapper.dbConnection("delete from supply_lines;", "alter");
        dbwrapper.dbConnection("delete from supervisory_nodes;", "alter");
        dbwrapper.dbConnection("delete from programs_supported;", "alter");
        dbwrapper.dbConnection("delete from requisition_group_members;", "alter");
        dbwrapper.dbConnection("delete from facilities;", "alter");
        dbwrapper.dbConnection("delete from programs_supported;", "alter");
        dbwrapper.dbConnection("delete from program_rnr_columns;", "alter");
        dbwrapper.dbConnection("delete from requisition_group_program_schedules ;", "alter");
        dbwrapper.dbConnection("delete from requisition_groups;", "alter");
        dbwrapper.dbConnection("delete from requisition_group_members;", "alter");
        dbwrapper.dbConnection("delete from processing_periods;", "alter");
        dbwrapper.dbConnection("delete from processing_schedules;", "alter");

    }


    public void insertRoles() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from roles;", "select");

        if (rs.next()) {
            dbwrapper.dbConnection("delete from role_rights;", "alter");
            dbwrapper.dbConnection("delete from role_assignments;", "alter");
            dbwrapper.dbConnection("delete from roles;", "alter");
        }
        dbwrapper.dbConnection("INSERT INTO roles\n" +
                " (name, description) VALUES\n" +
                " ('store in-charge', ''),\n" +
                " ('district pharmacist', '');", "alter");

    }

    public void insertRoleRights() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select roleId from role_rights;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_rights;", "alter");
            dbwrapper.dbConnection("delete from role_assignments;", "alter");
            dbwrapper.dbConnection("delete from roles;", "alter");

        }

        dbwrapper.dbConnection("INSERT INTO role_rights\n" +
                "  (roleId, rightName) VALUES\n" +
                "  ((select id from roles where name='store in-charge'), 'CREATE_REQUISITION'),\n" +
                "  ((select id from roles where name='district pharmacist'), 'UPLOADS'),\n" +
                "  ((select id from roles where name='district pharmacist'), 'MANAGE_FACILITY'),\n" +
                "  ((select id from roles where name='district pharmacist'), 'CONFIGURE_RNR');", "alter");
    }

    public void insertSupervisoryNodes(String facilityCode) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select facilityId from supervisory_nodes;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from supervisory_nodes;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO supervisory_nodes\n" +
                "  (parentId, facilityId, name, code) VALUES\n" +
                "  (null, (SELECT id FROM facilities WHERE code = '"+facilityCode+"'), 'Node 1', 'N1');", "alter");
    }

    public void insertSupervisoryNodesSecond(String facilityCode) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();

        dbwrapper.dbConnection("INSERT INTO supervisory_nodes\n" +
                "  (parentId, facilityId, name, code) VALUES\n" +
                "  ((select id from  supervisory_nodes where code ='N1'), (SELECT id FROM facilities WHERE code = '"+facilityCode+"'), 'Node 1', 'N2');", "alter");
    }

    public void insertRequisitionGroup() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from requisition_groups;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from requisition_groups;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
                "('RG2','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='N2')),\n" +
                "('RG1','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N1'));", "alter");
    }

    public void insertRequisitionGroupMembers(String RG1facility, String RG2facility) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select requisitiongroupid from requisition_group_members;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from requisition_group_members;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
                "((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='"+RG1facility+"')),\n" +
                "((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='"+RG2facility+"'));", "alter");
    }

    public void insertRequisitionGroupProgramSchedule() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select requisitiongroupid from requisition_group_members;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from requisition_group_program_schedules;", "alter");

        }
        dbwrapper.dbConnection("insert into requisition_group_program_schedules ( requisitiongroupid , programid , scheduleid , directdelivery ) values\n" +
                "((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
                "((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
                "((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),\n" +
                "((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
                "((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),\n" +
                "((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE);\n", "alter");

    }

    public void insertRoleAssignment(String userID, String userName) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();


        dbwrapper.dbConnection("delete from role_assignments where userId='"+userID+"';", "alter");

        dbwrapper.dbConnection(" INSERT INTO role_assignments\n" +
                "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
                "    ('"+userID+"', (SELECT id FROM roles WHERE name = '"+userName+"'), 1, null),\n" +
                "    ('"+userID+"', (SELECT id FROM roles WHERE name = '"+userName+"'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));", "alter");
    }

    public void updateRoleAssignment(String userID) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();


        dbwrapper.dbConnection("delete from role_assignments where userid='"+userID+"' and supervisorynodeid is null;", "alter");

        dbwrapper.dbConnection("update role_assignments set supervisorynodeid=(select id from supervisory_nodes where code='N2') where userid='"+userID+"';", "alter");
    }

    public void updateRoleGroupMember(String facilityCode) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();

        dbwrapper.dbConnection("update requisition_group_members set facilityid=(select id from facilities where code ='"+facilityCode+"') where requisitiongroupid=(select id from requisition_groups where code='RG2');", "alter");
        dbwrapper.dbConnection("update requisition_group_members set facilityid=(select id from facilities where code ='F11') where requisitiongroupid=(select id from requisition_groups where code='RG1');", "alter");
    }

    public void alterUserID(String userId) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();


        dbwrapper.dbConnection("delete from users where id=200;", "alter");

        dbwrapper.dbConnection(" update users set id="+userId+" where username='User123'", "alter");
    }



    public void insertProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from products;", "select");

        if (rs.next()) {
            dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
            dbwrapper.dbConnection("delete from program_products;", "alter");
            dbwrapper.dbConnection("delete from products;", "alter");

        }
        dbwrapper.dbConnection("insert into products\n" +
                "(code,    alternateItemCode,  manufacturer,       manufacturerCode,  manufacturerBarcode,   mohBarcode,   gtin,   type,         primaryName,    fullName,       genericName,    alternateName,    description,      strength,    formId,  dosageUnitId, dispensingUnit,  dosesPerDispensingUnit,  packSize,  alternatePackSize,  storeRefrigerated,   storeRoomTemperature,   hazardous,  flammable,   controlledSubstance,  lightSensitive,  approvedByWho,  contraceptiveCyp,  packLength,  packWidth, packHeight,  packWeight,  packsPerCarton, cartonLength,  cartonWidth,   cartonHeight, cartonsPerPallet,  expectedShelfLife,  specialStorageInstructions, specialTransportInstructions, active,  fullSupply, tracer,   packRoundingThreshold,  roundToZero,  archived) values\n" +
                "('P100',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),\n" +
                "('P101',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE),\n" +
                "('P102',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     10,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    TRUE,      TRUE);\n","alter");
    }

    public void insertProgramProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from program_products;", "select");

        if (rs.next()) {
            dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
            dbwrapper.dbConnection("delete from program_products;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO program_products(programId, productId, dosesPerMonth, currentPrice, active) VALUES\n" +
                "(1, (SELECT id from products WHERE code = 'P100'), 30, 12, true),\n" +
                "(1, (SELECT id from products WHERE code = 'P101'), 30, 50, true),\n" +
                "(1, (SELECT id from products WHERE code = 'P102'), 30, 0, true);", "alter");
    }

    public void insertFacilityApprovedProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();

            dbwrapper.dbConnection("delete from facility_approved_products;", "alter");

        dbwrapper.dbConnection("insert into facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) values\n" +
                "((select id from facility_types where name='Lvl3 Hospital'), (select id from program_products where programId=(Select id from programs order by modifiedDate DESC limit 1) and productId=(Select id from products order by modifiedDate DESC limit 1)), 3);", "alter");

    }

    public void insertSchedules() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from processing_schedules;", "select");

        if (rs.next()) {
            dbwrapper.dbConnection("delete from processing_periods;", "alter");
            dbwrapper.dbConnection("delete from processing_schedules;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO processing_schedules(code, name, description) values('Q1stM', 'QuarterMonthly', 'QuarterMonth');", "alter");
        dbwrapper.dbConnection("INSERT INTO processing_schedules(code, name, description) values('M', 'Monthly', 'Month');", "alter");
    }

    public void insertProcessingPeriods() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from processing_periods;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from processing_periods;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO processing_periods\n" +
                "(name, description, startDate, endDate, scheduleId, modifiedBy) VALUES\n" +
                "('Period1', 'first period',  '2012-12-01', '2013-01-15', (SELECT id FROM processing_schedules WHERE code = 'Q1stM'), (SELECT id FROM users LIMIT 1)),\n" +
                "('Period2', 'second period', '2013-01-16', '2013-04-30', (SELECT id FROM processing_schedules WHERE code = 'M'), (SELECT id FROM users LIMIT 1));", "alter");

    }



    public void configureTemplate() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();

        dbwrapper.dbConnection("INSERT INTO program_rnr_columns\n" +
                "(masterColumnId, programId, visible, source, position, label) VALUES\n" +
                "(1, (select id from programs where code = 'HIV'),  true, 'R', 1,  'Product Code'),\n" +
        "(2, (select id from programs where code = 'HIV'),  true, 'R', 2,  'Product'),\n" +
        "(3, (select id from programs where code = 'HIV'),  true, 'R', 3,  'Unit/Unit of Issue'),\n" +
        "(4, (select id from programs where code = 'HIV'),  true, 'U', 4,  'Beginning Balance'),\n" +
        "(5, (select id from programs where code = 'HIV'),  true, 'U', 5,  'Total Received Quantity'),\n" +
        "(6, (select id from programs where code = 'HIV'),  true, 'U', 6,  'Total Consumed Quantity'),\n" +
        "(7, (select id from programs where code = 'HIV'),  true, 'U', 7,  'Total Losses / Adjustments'),\n" +
        "(8, (select id from programs where code = 'HIV'),  true, 'C', 8,  'Stock on Hand'),\n" +
        "(9, (select id from programs where code = 'HIV'),  true, 'U', 9, 'New Patients'),\n" +
        "(10, (select id from programs where code = 'HIV'), true, 'U', 10, 'Total Stockout days'),\n" +
        "(11, (select id from programs where code = 'HIV'), true, 'C', 11, 'Adjusted Total Consumption'),\n" +
        "(12, (select id from programs where code = 'HIV'), true, 'C', 12, 'Average Monthly Consumption(AMC)'),\n" +
        "(13, (select id from programs where code = 'HIV'), true, 'C', 13, 'Maximum Stock Quantity'),\n" +
        "(14, (select id from programs where code = 'HIV'), true, 'C', 14, 'Calculated Order Quantity'),\n" +
        "(15, (select id from programs where code = 'HIV'), true, 'U', 15, 'Requested Quantity'),\n" +
        "(16, (select id from programs where code = 'HIV'), true, 'U', 16, 'Requested Quantity Explanation'),\n" +
        "(17, (select id from programs where code = 'HIV'), true, 'U', 17, 'Approved Quantity'),\n" +
        "(18, (select id from programs where code = 'HIV'), true, 'C', 18, 'Packs to Ship'),\n" +
        "(19, (select id from programs where code = 'HIV'), true, 'R', 19, 'Price per pack'),\n" +
        "(20, (select id from programs where code = 'HIV'), true, 'C', 20, 'Total cost'),\n" +
        "(21, (select id from programs where code = 'HIV'), true, 'U', 21, 'Remarks');", "alter");
    }

    public String getFacilityIDDB() throws IOException , SQLException
    {

        DBWrapper dbWrapper=new DBWrapper();
         String id=null;
        ResultSet rs=dbWrapper.dbConnection("select id from facilities order by modifiedDate DESC limit 1", "select");

        if (rs.next()) {
         id=rs.getString("id");
        }
        return id;

    }

    public String getFacilityFieldBYID(String field, String id) throws IOException , SQLException
    {

        DBWrapper dbWrapper=new DBWrapper();
        String facilityField=null;
        ResultSet rs=dbWrapper.dbConnection("select "+field+" from facilities where id="+id+";", "select");

        if (rs.next()) {
            facilityField=rs.getString(1);
        }
        return facilityField;

    }

}
