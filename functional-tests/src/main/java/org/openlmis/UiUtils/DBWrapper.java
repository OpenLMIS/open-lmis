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

}
