package org.openlmis.UiUtils;

import java.sql.*;
import java.util.Properties;
import java.io.*;

public class DBWrapper {

    String baseUrl, dbUrl, dbUser, dbPassword;

    public DBWrapper() throws IOException {

        final Properties props = new Properties();

        System.out.println(System.getProperty("user.dir") + "/src/main/resources/config.properties");
        props.load(new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties"));
        baseUrl = props.getProperty("baseUrl");
        dbUrl = props.getProperty("dbUrl");
        dbUser = props.getProperty("dbUser");
        dbPassword = props.getProperty("dbPassword");
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

                if (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            } else {
                st.executeUpdate(Query);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {

            return rs;

        }
    }

    public void insertUser(String userId, String userName, String password) throws SQLException, IOException {
        boolean flag = false;
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select userName from users;", "select");
        if (rs.next()) {
            if (rs.getString(1).contains(userName)) {
                flag = true;
            }
        }
        if (flag) {
            dbwrapper.dbConnection("delete from users where userName like('"+userName+"');", "alter");
        }
        dbwrapper.dbConnection("INSERT INTO users\n" +
                "  (id, userName, password, facilityId) VALUES\n" +
                "  ('"+userId+"', '"+userName+"', '"+password+"', null);", "alter");

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
                "('F1756','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),\n" +
                "('F1757','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');\n", "alter");

    }


    public void allocateFacilityToUser() throws IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("update users set facilityId = (Select id from facilities order by modifiedDate DESC limit 1) where id=200;", "alter");

    }


    //delete from role_assignments where userid not in (1);

    public void deleteData() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("delete from role_rights where roleid not in(1);", "alter");
        dbwrapper.dbConnection("delete from role_assignments where userid not in (1);", "alter");
        dbwrapper.dbConnection("delete from roles where name not in ('Admin');", "alter");
        dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
        dbwrapper.dbConnection("delete from program_products;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_items;", "alter");
        dbwrapper.dbConnection("delete from products;", "alter");
        dbwrapper.dbConnection("delete from users where userName like('User%');", "alter");
        dbwrapper.dbConnection("delete from requisition;", "alter");
        dbwrapper.dbConnection("delete from programs_supported;", "alter");
        dbwrapper.dbConnection("delete from supervisory_nodes;", "alter");
        dbwrapper.dbConnection("delete from requisition_group_members;", "alter");
        dbwrapper.dbConnection("delete from facilities;", "alter");
        dbwrapper.dbConnection("delete from program_rnr_columns;", "alter");
        dbwrapper.dbConnection("delete from requisition_groups;", "alter");
        dbwrapper.dbConnection("delete from requisition_group_members;", "alter");
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

    public void insertSupervisoryNodes() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select facilityId from supervisory_nodes;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from supervisory_nodes;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO supervisory_nodes\n" +
                "  (parentId, facilityId, name, code) VALUES\n" +
                "  (null, (SELECT id FROM facilities WHERE code = 'F1756'), 'Node 1', 'N1');", "alter");
    }

    public void insertSupervisoryNodesSecond() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();

        dbwrapper.dbConnection("INSERT INTO supervisory_nodes\n" +
                "  (parentId, facilityId, name, code) VALUES\n" +
                "  ((select id from  supervisory_nodes where code ='N1'), (SELECT id FROM facilities WHERE code = 'F1757'), 'Node 1', 'N2');", "alter");
    }

    public void insertRequisitionGroup() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from requisition_groups;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from requisition_groups;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values\n" +
                "('RG2','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='N1')),\n" +
                "('RG1','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N2'));", "alter");
    }

    public void insertRequisitionGroupMembers() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select requisitiongroupid from requisition_group_members;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from requisition_group_members;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values\n" +
                "((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='F1756')),\n" +
                "((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='F1757'));", "alter");
    }

    public void insertRoleAssignment(String userId) throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select userId from role_assignments;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_assignments where userId=200;", "alter");

        }
        dbwrapper.dbConnection(" INSERT INTO role_assignments\n" +
                "            (userId, roleId, programId, supervisoryNodeId) VALUES \n" +
                "    (200, (SELECT id FROM roles WHERE name = '"+userId+"'), 1, null),\n" +
                "    (200, (SELECT id FROM roles WHERE name = '"+userId+"'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));", "alter");
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
                "('P100',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                     1,        30,                   TRUE,                  TRUE,                TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,          2.2,            2,          2,            2,            2,            2,              2,              2,              2,                    2,                    'a',                          'a',          TRUE,     TRUE,       TRUE,         1,                    FALSE,      TRUE)\n;", "alter");
    }

    public void insertProgramProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from program_products;", "select");

        if (rs.next()) {
            dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
            dbwrapper.dbConnection("delete from program_products;", "alter");

        }
        dbwrapper.dbConnection("insert into program_products(programId, productId, dosesPerMonth, active) values\n" +
                "(1, (Select id from products order by modifiedDate DESC limit 1), 30, true);", "alter");
    }

    public void insertFacilityApprovedProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from facility_approved_products;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from facility_approved_products;", "alter");

        }
        dbwrapper.dbConnection("insert into facility_approved_products(facilityTypeId, programProductId, maxMonthsOfStock) values\n" +
                "((select id from facility_types where name='Lvl3 Hospital'), (select id from program_products where programId=(Select id from programs order by modifiedDate DESC limit 1) and productId=(Select id from products order by modifiedDate DESC limit 1)), 3);", "alter");
    }
}
