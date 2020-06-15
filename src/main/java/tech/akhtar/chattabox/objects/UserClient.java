package tech.akhtar.chattabox.objects;

import com.sun.istack.internal.Nullable;
import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
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

    /***
     * Get a online users UserClient object.
     *
     * @param username Target users username
     * @return UserClient object, if user is not online it will return null
     */
    public static UserClient getOnlineUser(String username){
        for (UserClient userClient : Chattabox.userClientThreadMap.keySet()){
            if (userClient.getUsername().equalsIgnoreCase(username)){
                return userClient;
            }
        }
        return null;
    }

    /***
     * Get the Two Factor Authentication code for a user who is a moderator or administrator.
     *
     * @param username Target users username
     * @return Two Factor Authentication code, if user is not a administrator or moderator it will return null
     */
    public static String getTwoFacAuthForUser(String username){
        UserClient userClient = new UserClient(username);
        if (userClient.isModerator() || userClient.isAdministrator()) {
            try (Connection connection = Database.getMySqlConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(((userClient.isAdministrator()) ? "SELECT * FROM administrators WHERE username = ?" :
                        "SELECT * FROM moderators WHERE username = ?"));
                preparedStatement.setString(1, username);
                ResultSet set = preparedStatement.executeQuery();
                while (set.next()) {
                    return set.getString("two_factor_auth_code");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /***
     * Get the Client / Socket Handler for the target user.
     *
     * @return Client / Socket Handler Object
     */
    public MRSClientHandler getMrsClientHandler() {
        return mrsClientHandler;
    }

    /***
     * Set the target users Client / Socket Handler.
     *
     * @param mrsClientHandler The Client / Socket Handler you want to set for the target user
     */
    public void setMrsClientHandler(MRSClientHandler mrsClientHandler) {
        this.mrsClientHandler = mrsClientHandler;
    }

    /***
     * Set the socket for the target user.
     *
     * @param clientSocket The Socket you want to set for the target user
     */
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /***
     * Set the UserClientSettings for the target user.
     *
     * @param userClientSettings UserClientHandler object you want to set for the target user
     */
    public void setUserClientSettings(UserClientSettings userClientSettings) {
        this.userClientSettings = userClientSettings;
    }

    /***
     * Get the UserClientSettings object for the target user.
     *
     * @return UserClientSettings object
     */
    public UserClientSettings getUserClientSettings() {
        return userClientSettings;
    }

    /***
     * Get the target users username.
     *
      * @return Target users username
     */
    public String getUsername() {
        return username;
    }

    /***
     * Get the target users socket.
     *
     * @return Target users Socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /***
     * Checks if target user is a moderator.
     *
     * @return boolean value based on whether or not the target user is a moderator
     */
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

    /***
     * Checks if target user is a administrator.
     *
     * @return boolean value based on whether or not the target user is a administrator
     */
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

    /***
     * Checks if target user is currently a banned user.
     *
     * @return boolean value based on whether or not the target user is banned
     */
    public boolean isBanned(){
        return Chattabox.BANNED_USERS.containsKey(username.toLowerCase());
    }

    /***
     * Kick the target user (closes the socket).
     *
     * @param reason The reason you are kicking the user
     */
    public void kick(String reason){
        MRSClientHandler mrsClientHandler = getMrsClientHandler();
        mrsClientHandler.kicked = true;
        mrsClientHandler.kick_reason =  reason;
    }

    /***
     * Get target users role (Refer to tech.akhtar.chattabox.Role.java).
     *
     * @return The target users Role
     */
    public Role getRole(){
        if (isAdministrator()) return Role.ADMINISTRATOR;
        if (isModerator()) return Role.MODERATOR;
        return Role.NORMAL;
    }

    /***
     * Demote target user to a normal user.
     */
    public void demoteUser(){
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(((isAdministrator()) ? "DELETE FROM administrators WHERE username = ?;" : "DELETE FROM moderators WHERE username = ?;"));
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /***
     * Promote target user to a moderator.
     */
    public void makeModerator() {
        try(Connection connection = Database.getMySqlConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO moderators (username, two_factor_auth_code) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, UUID.randomUUID().toString());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /***
     * Promote target user to a administrator.
     */
    public void makeAdministrator(){
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
