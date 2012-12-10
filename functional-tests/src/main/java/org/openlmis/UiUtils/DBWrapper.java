package org.openlmis.UiUtils;

import java.sql.*;

public class DBWrapper {

    public ResultSet dbConnection(String Query, String indicator)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;


        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe){
            System.out.println("Could not find the JDBC driver!");
            System.exit(1);
        }

        String url = "jdbc:postgresql://localhost:5432/open_lmis";
        String user = "postgres";
        String password = "p@ssw0rd";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            if(indicator.equalsIgnoreCase("select"))
            {
                rs=st.executeQuery(Query);

                if (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }else
            {
                st.executeUpdate(Query);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {

            return rs;

        }
    }

    public void insertUser() throws SQLException
    {
        boolean flag=false;
        DBWrapper dbwrapper=new DBWrapper();
        ResultSet rs=dbwrapper.dbConnection("Select user_name from users;","select");
        if (rs.next()) {
            if(rs.getString(1).contains("User"))
            {
                flag=true;
            }
        }
        if(flag)
        {
            dbwrapper.dbConnection("delete from users where user_name like('User%');","alter");
        }
        dbwrapper.dbConnection("INSERT INTO users\n" +
                "  (id, user_name, password, role, facility_id) VALUES\n" +
                "  (2, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', null);","alter");

        rs.close();
    }

    public void insertUserAndAllocateFacility()
    {
        DBWrapper dbwrapper=new DBWrapper();
        dbwrapper.dbConnection("INSERT INTO users\n" +
                "  (id, user_name, password, role, facility_id) VALUES\n" +
                "  (2, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', (Select id from facility order by modified_date DESC limit 1');","alter");

    }

    public void deleteUser() throws SQLException
    {
        DBWrapper dbwrapper=new DBWrapper();
        dbwrapper.dbConnection("delete from facility;","alter");
        dbwrapper.dbConnection("delete from users where user_name like('User%');","alter");

    }


    public void insertRoles() throws SQLException
    {
        DBWrapper dbwrapper=new DBWrapper();
        dbwrapper.dbConnection("INSERT INTO roles\n" +
                "(id, name, description) VALUES\n" +
                "(1, 'store in-charge', ''),\n" +
                "(2, 'district pharmacist', '');", "alter");

    }

    public void insertRoleRights() throws SQLException
    {
        DBWrapper dbwrapper=new DBWrapper();
        dbwrapper.dbConnection("INSERT INTO role_rights (role_id, right_id) VALUES (1, 1),(1, 2),(2, 1),(2, 2),(2, 3);", "alter");
    }

    public void insertRoleAssignment() throws SQLException
    {
        DBWrapper dbwrapper=new DBWrapper();
        dbwrapper.dbConnection("INSERT INTO role_assignments (user_id, role_id, program_id) VALUES (2, 2, 'HIV');", "alter");
    }
}
