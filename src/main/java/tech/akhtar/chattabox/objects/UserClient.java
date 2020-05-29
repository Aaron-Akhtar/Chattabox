package tech.akhtar.chattabox.objects;

import com.sun.istack.internal.Nullable;
import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.exceptions.ChattaboxException;
import tech.akhtar.chattabox.mysql.Database;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class UserClient {

    public static UserClient getOnlineUser(String username){
        for (UserClient userClient : Chattabox.userClientThreadMap.keySet()){
            if (userClient.getUsername().equalsIgnoreCase(username)){
                return userClient;
            }
        }
        return null;
    }

    public static String getTwoFacAuthForUser(String username){
        UserClient userClient = new UserClient(username);
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(((userClient.isAdministrator()) ? "SELECT * FROM administrators WHERE username = ?" :
                                                                                            "SELECT * FROM moderators WHERE username = ?"));
            preparedStatement.setString(1, username);
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()){
                return set.getString("two_factor_auth_code");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String username;
    private Socket clientSocket;
    private UserClientSettings userClientSettings;
    private MRSClientHandler mrsClientHandler;

    public UserClient(String username, Socket clientSocket) {
        this.username = username;
        this.clientSocket = clientSocket;
        userClientSettings = new UserClientSettings();
    }

    public UserClient(String username) {
        this.username = username;
        this.clientSocket = null;
        userClientSettings = new UserClientSettings();
    }

    public MRSClientHandler getMrsClientHandler() {
        return mrsClientHandler;
    }

    public void setMrsClientHandler(MRSClientHandler mrsClientHandler) {
        this.mrsClientHandler = mrsClientHandler;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setUserClientSettings(UserClientSettings userClientSettings) {
        this.userClientSettings = userClientSettings;
    }

    public UserClientSettings getUserClientSettings() {
        return userClientSettings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public boolean isModerator(){
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM moderators WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAdministrator(){
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM administrators WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBanned(){
        return Chattabox.BANNED_USERS.containsKey(username.toLowerCase());
    }

    public void kick(String reason){
        MRSClientHandler mrsClientHandler = getMrsClientHandler();
        mrsClientHandler.kicked = true;
        mrsClientHandler.kick_reason =  reason;
    }

    public Role getRole(){
        if (isAdministrator()) return Role.ADMINISTRATOR;
        if (isModerator()) return Role.MODERATOR;
        return Role.NORMAL;
    }

    @SuppressWarnings("Duplicates")
    public void demoteUser() throws ChattaboxException{
        if (!isAdministrator() || !isModerator()){
            throw new ChattaboxException(username + " is not a staff member, please promote them to gain access to this...");
        }
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(((isAdministrator()) ? "DELETE FROM administrators WHERE username = ?;" : "DELETE FROM moderators WHERE username = ?;"));
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("Duplicates")
    public void demoteUser(BufferedWriter writer){
        if (!isAdministrator() && !isModerator()){
            try{
                writer.write(username + " is not a staff member, please promote them to gain access to this... \r\n");
                writer.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(((isAdministrator()) ? "DELETE FROM administrators WHERE username = ?;" : "DELETE FROM moderators WHERE username = ?;"));
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            writer.write(Colour.GREEN.get() + username + " successfully demoted!");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @SuppressWarnings("Duplicates")
    public void makeModerator(BufferedWriter writer) {
        if (isModerator() || isAdministrator()){
            try{
                writer.write(Colour.RED.get() + username + " is already a staff member, please demote them to continue... \r\n");
                writer.flush();
                return;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO moderators (username, two_factor_auth_code) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, UUID.randomUUID().toString());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("Duplicates")
    public void makeModerator() throws ChattaboxException{
        if (isModerator() || isAdministrator()){
            throw new ChattaboxException(username + " is already a staff member, please demote them to continue...");
        }
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO moderators (username, two_factor_auth_code) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, UUID.randomUUID().toString());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("Duplicates")
    public void makeAdministrator(BufferedWriter writer) {
        if (isModerator() || isAdministrator()){
            try{
                writer.write(Colour.RED.get() + username + " is already a staff member, please demote them to continue... \r\n");
                writer.flush();
                return;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO administrators (username, two_factor_auth_code) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, UUID.randomUUID().toString());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("Duplicates")
    public void makeAdministrator() throws ChattaboxException{
        if (isModerator() || isAdministrator()) {
            throw new ChattaboxException(username + " is already a staff member, please demote them to continue...");
        }
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO administrators (username, two_factor_auth_code) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, UUID.randomUUID().toString());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
