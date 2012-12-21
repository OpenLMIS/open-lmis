package org.openlmis.UiUtils;

import java.sql.*;
import java.util.Properties;
import java.io.*;

public class DBWrapper {

    String baseUrl, dbUrl, dbUser, dbPassword;

    public DBWrapper() throws IOException {

        final Properties props = new Properties();

        System.out.println(System.getProperty("user.dir") + "/src/main/resources/config.properties");
        props.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties"));
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

    public void insertUser(String userName, String password) throws SQLException, IOException {
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
                "  (id, userName, password, role, facilityId) VALUES\n" +
                "  (200, '"+userName+"', '"+password+"','USER', null);", "alter");

    }



    public void allocateFacilityToUser() throws IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("update users set facilityId = (Select id from facilities order by modifiedDate DESC limit 1) where id=200;", "alter");

    }

    public void deleteProgramRnrColumns() throws IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("delete from program_rnr_columns;", "alter");
    }

    public void deleteUser() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("delete from role_rights;", "alter");
        dbwrapper.dbConnection("delete from role_assignments;", "alter");
        dbwrapper.dbConnection("delete from roles;", "alter");
        dbwrapper.dbConnection("delete from facility_approved_products;", "alter");
        dbwrapper.dbConnection("delete from program_products;", "alter");
        dbwrapper.dbConnection("DELETE FROM requisition_line_items;", "alter");
        dbwrapper.dbConnection("delete from products;", "alter");
        dbwrapper.dbConnection("delete from users where userName like('User%');", "alter");
        dbwrapper.dbConnection("delete from requisition;", "alter");
        dbwrapper.dbConnection("delete from programs_supported;", "alter");
        dbwrapper.dbConnection("delete from supervisory_nodes;", "alter");
        dbwrapper.dbConnection("delete from facilities;", "alter");
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
                "(id, name, description) VALUES\n" +
                "(1, 'store in-charge', ''),\n" +
                "(2, 'district pharmacist', '');", "alter");

    }

    public void insertRoleRights() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select roleId from role_rights;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_rights;", "alter");
            dbwrapper.dbConnection("delete from role_assignments;", "alter");
            dbwrapper.dbConnection("delete from roles;", "alter");

        }

        dbwrapper.dbConnection("INSERT INTO role_rights (roleId, rightId) VALUES (1, 'VIEW_REQUISITION'), (1, 'CREATE_REQUISITION'),(2, 'VIEW_REQUISITION'),(2, 'UPLOADS'),(2, 'MANAGE_FACILITY'),(2, 'CONFIGURE_RNR');", "alter");
    }

    public void insertRoleAssignment() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select userId from role_assignments;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_assignments;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO role_assignments (userId, roleId, programId) VALUES (1, 2, 1), (200, 1, 1);", "alter");
    }

    public void insertProducts() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select id from products;", "select");

        if (rs.next()) {
//
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
