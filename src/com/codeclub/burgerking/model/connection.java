package com.codeclub.burgerking.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class connection {
    public static Connection getConnection() throws IOException, SQLException {
        FileReader fileReader = new FileReader("src\\com\\codeclub\\burgerking\\model\\credentials.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        Properties properties = new Properties();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split("=");
            properties.setProperty(parts[0], parts[1]);
        }
        bufferedReader.close();


        String url = "jdbc:mysql://" + properties.getProperty("host") + ":" + properties.getProperty("port") + "/" + properties.getProperty("bbdd");
        String user = properties.getProperty("usuari");
        String password = properties.getProperty("contrasenya");

        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try {
            Connection connection = getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
