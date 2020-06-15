package tech.akhtar.chattabox.mysql;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class Database {

    /***
     * Get the MySQL JDBC Connection object.
     *
     * @return MySQL Connection Object
     */
    public static Connection getMySqlConnection() throws Exception {
        final File file = new MySQLFile().getFile();
        final String url = "jdbc:mysql://"+ Chattabox.mysqlMap.get("mysql-server").toString() +":"+Chattabox.mysqlMap.get("mysql-port").
                toString()+"/" + Chattabox.mysqlMap.get("mysql-database").toString();
        final String username = Chattabox.mysqlMap.get("mysql-user").toString();
        final String password = Chattabox.mysqlMap.get("mysql-password").toString();
        Connection connection = DriverManager.getConnection(url + "?allowMultiQueries=true", username, password);
        return connection;
    }

    /***
     * Setup the MySQL Database by creating all necessary tables.
     */
    public static void setup(){
        try(Connection connection = getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `moderators` (`id` INT NOT NULL AUTO_INCREMENT,`username` VARCHAR(255),`two_factor_auth_code` VARCHAR(255), PRIMARY KEY (id));CREATE TABLE IF NOT EXISTS `administrators` (`id` INT NOT NULL AUTO_INCREMENT,`username` VARCHAR(255),`two_factor_auth_code` VARCHAR(255), PRIMARY KEY (id));");
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
