package tech.akhtar.chattabox.mysql;

import tech.akhtar.chattabox.file.manager.files.MySQLFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Database {

    public static Connection getMySqlConnection() throws Exception {
        final String url = "jdbc:mysql://"+ MySQLFile.getDatabaseInfo().get("mysql-server").toString() +":"+MySQLFile.getDatabaseInfo().get("mysql-port").
                toString()+"/" + MySQLFile.getDatabaseInfo().get("mysql-database").toString();
        final String username = MySQLFile.getDatabaseInfo().get("mysql-user").toString();
        final String password = "";//MySQLFile.getDatabaseInfo().get("mysql-password").toString();
        Connection connection = DriverManager.getConnection(url + "?allowMultiQueries=true", username, password);
        return connection;
    }

    public static void setup(){
        /*try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return;
        }*/
        try(Connection connection = getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `moderators` (`id` INT NOT NULL AUTO_INCREMENT,`username` VARCHAR(255),`two_factor_auth_code` VARCHAR(255), PRIMARY KEY (id));CREATE TABLE IF NOT EXISTS `administrators` (`id` INT NOT NULL AUTO_INCREMENT,`username` VARCHAR(255),`two_factor_auth_code` VARCHAR(255), PRIMARY KEY (id));");
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
