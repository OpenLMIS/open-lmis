package org.openlmis.UiUtils;

import java.sql.*;
import java.util.Properties;
import java.io.*;

public class DBWrapper {

    String baseUrl, dbUrl, dbUser, dbPassword;

    public DBWrapper() throws IOException {

        final Properties props = new Properties();

        System.out.println(System.getProperty("user.dir")+"/src/main/resources/config.properties");
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

    public void insertUser() throws SQLException, IOException {
        boolean flag = false;
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select user_name from users;", "select");
        if (rs.next()) {
            if (rs.getString(1).contains("User")) {
                flag = true;
            }
        }
        if (flag) {
            dbwrapper.dbConnection("delete from users where user_name like('User%');", "alter");
        }
        dbwrapper.dbConnection("INSERT INTO users\n" +
                "  (id, user_name, password, role, facility_id) VALUES\n" +
                "  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', null);", "alter");

        //rs.close();
    }

//    public void insertUserAndAllocateFacility() throws IOException {
//        DBWrapper dbwrapper = new DBWrapper();
//        dbwrapper.dbConnection("INSERT INTO users\n" +
//                "  (id, user_name, password, role, facility_id) VALUES\n" +
//                "  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', (Select id from facility order by modified_date DESC limit 1));", "alter");
//
//    }

    public void insertUserAndAllocateFacility() throws IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("update users set facility_id = (Select id from facility order by modified_date DESC limit 1) where id=200;", "alter");

    }

    public void deleteUser() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        dbwrapper.dbConnection("delete from role_rights;", "alter");
        dbwrapper.dbConnection("delete from role_assignments;", "alter");
        dbwrapper.dbConnection("delete from roles;", "alter");
        dbwrapper.dbConnection("delete from programs_supported;", "alter");
        dbwrapper.dbConnection("delete from users where user_name like('User%');", "alter");
        dbwrapper.dbConnection("delete from requisition;", "alter");
        dbwrapper.dbConnection("delete from facility;", "alter");
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
        ResultSet rs = dbwrapper.dbConnection("Select role_id from role_rights;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_rights;", "alter");
            dbwrapper.dbConnection("delete from role_assignments;", "alter");
            dbwrapper.dbConnection("delete from roles;", "alter");

        }

        dbwrapper.dbConnection("INSERT INTO role_rights (role_id, right_id) VALUES (1, 'VIEW_REQUISITION'), (1, 'CREATE_REQUISITION'),(2, 'VIEW_REQUISITION'),(2, 'UPLOADS'),(2, 'MANAGE_FACILITY'),(2, 'CONFIGURE_RNR');", "alter");
    }

    public void insertRoleAssignment() throws SQLException, IOException {
        DBWrapper dbwrapper = new DBWrapper();
        ResultSet rs = dbwrapper.dbConnection("Select user_id from role_assignments;", "select");

        if (rs.next()) {

            dbwrapper.dbConnection("delete from role_assignments;", "alter");

        }
        dbwrapper.dbConnection("INSERT INTO role_assignments (user_id, role_id, program_id) VALUES (100, 2, 1), (200, 1, 1);", "alter");
    }
}
