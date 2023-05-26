package com.codeclub.burgerking.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.codeclub.burgerking.model.connection.getConnection;

public class proves {
    public static void main(String[] args) {
        try
        {
            // create our mysql database connection
            Connection conn = getConnection();

            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String query = "SELECT id_menjar, nom, preu FROM menjar";

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next())
            {
                int id = rs.getInt("id_menjar");
                String nom = rs.getString("nom");
                int preu = rs.getInt("preu");

                // print the results
                System.out.println(id);
                System.out.println(nom);
                System.out.println(preu);
            }
            st.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }
}
