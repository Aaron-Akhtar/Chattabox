package tech.akhtar.chattabox.server.runnables;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Socks;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.crypt.Aes;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.MOTDFile;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.file.manager.files.PropertiesFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.utils.Colour;
import tech.akhtar.chattabox.utils.WindowUtils;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class MRSClientHandler implements Runnable{
    /**User related variables // Socket related**/
    public String prefix = "";
    public boolean shouldClose = false;
    public boolean kicked = false;
    public String kick_reason = "";

    /***
     * Sets the users prefix - if user is a staff member they will be given the appropriate prefix.
     *
     * @param userClient Target User
     * @return The target users prefix
     */
    public static String getPrefix(UserClient userClient){
        if (Chattabox.isMySQLEnabled) {
            if (userClient.isAdministrator()) {
                return Chattabox.ADMIN_PREFIX + userClient.getUserClientSettings().getUsernameColour().get() + userClient.getUsername() + Colour.WHITE.get() + ": " + Colour.RESET.get();
            } else if (userClient.isModerator()) {
                return Chattabox.MOD_PREFIX + userClient.getUserClientSettings().getUsernameColour().get() + userClient.getUsername() + Colour.WHITE.get() + ": " + Colour.RESET.get();
            }
        }
        return userClient.getUserClientSettings().getUsernameColour().get() + userClient.getUsername() + Colour.WHITE.get() + ": " + Colour.RESET.get();
    }

    public Socket socket;
    private UserClient userClient;
    public MRSClientHandler(Socket socket) {
        this.socket = socket;
    }

    /***
     * Handles the clients connection appropriately.
     */
    @Override
    public void run() {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            WindowUtils.setTerminalName(Chattabox.NAME + " - Set name", writer);
            writer.write(Colour.YELLOW.get() + "Enter your username / nickname: " + Colour.RESET.get());
            writer.flush();
            String x = reader.readLine();
            String username = ((x.contains("\u0003")) ? x.substring(x.lastIndexOf("\u0003")+1) : x);
            for (UserClient userClient : Chattabox.userClientThreadMap.keySet()){
                if (userClient.getUsername().equalsIgnoreCase(username)){
                    writer.write(Colour.RED.get() + "This username is under use....");
                    writer.flush();
                    Thread.sleep(5000);
                    return;
                }
            }
            userClient = new UserClient(username, socket);

            if (userClient.isBanned()){
                writer.write(Colour.CLEAR.get());
                writer.write(Colour.RED.get() + "This user is banned: '"+Chattabox.BANNED_USERS.get(userClient.getUsername())+"'");
                writer.flush();
                Thread.sleep(3000);
                return;
            }
            if (Chattabox.isMySQLEnabled) {
                if (userClient.isAdministrator() || userClient.isModerator()) {
                    writer.write("This username is reserved for a staff member... \r\n");
                    writer.write("Please enter your 2FA code to be allowed access: " + Colour.BLACK.get());
                    writer.flush();
                    String fa = reader.readLine();
                    writer.write(Colour.RESET.get());
                    if (!fa.equalsIgnoreCase(UserClient.getTwoFacAuthForUser(username))) {
                        writer.write("\r\nInvalid 2FA.... \r\n");
                        writer.flush();
                        Thread.sleep(5000);
                        return;
                    }
                }
            }
            prefix = getPrefix(userClient);
            userClient.setMrsClientHandler(this);
            Chattabox.userClientThreadMap.put(userClient, new Thread(this));
            writer.write(Colour.CLEAR.get());
            WindowUtils.setTerminalName(Chattabox.NAME + " - Home", writer);
            writer.write(Colour.WHITE.get() + MOTDFile.getMotd() + " \r\n");
            System.out.println(Chattabox.PREFIX + Colour.CYAN.get() + username + " has joined  ["+socket.getInetAddress().getHostAddress()+"]");
            while(Socks.isSocketConnected(socket) && !shouldClose && !kicked){
                writer.write(prefix);
                writer.flush();
                check(reader.readLine(), writer);
            }
            if (kicked){
                writer.write(Colour.CLEAR.get());
                writer.write("You have been kicked: '"+kick_reason+"'");
                writer.flush();
            }
            if (shouldClose){
                System.out.println(Chattabox.PREFIX + Colour.CYAN.get() + username + " has left");
            }
        }catch (Exception e){
            if(!e.getMessage().toLowerCase().contains("connection abort") && !e.getMessage().toLowerCase().contains("socket closed")){
                e.printStackTrace();
            }else{
                System.out.println(Chattabox.PREFIX + Colour.CYAN.get() + userClient.getUsername() + " has left");
            }
        }
        Chattabox.userClientThreadMap.remove(userClient);
    }

    /***
     * Parses and reads users input string to see if it matches a command or is just normal
     * text, if its a command it will execute the appropriate method.
     *
     * @param string Target Input Text
     * @param writer BufferedWriter Object to write output to target
     */
    private void check(String string, BufferedWriter writer) {
        String[] args = null;
        try {
            args = string.split(" ");
        }catch (NullPointerException e){
            return;
        }
        if (args[0].length() > 0) {
            if (args[0].charAt(0) == '/') {
                for (Class<? extends ChattaboxCommand> commandClass : Chattabox.COMMANDS) {
                    try {
                        ChattaboxCommand command = commandClass.newInstance();
                        if (command.getCommand().equalsIgnoreCase(args[0])) {
                            command.doAction(args, userClient, writer, this);
                            writer.write("\r\n");       // put this here because I keep forgetting to add \r\n after each line :(
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Socks.sendMessage(prefix + userClient.getUserClientSettings().getMessageColour().get() + string);
            }
        }
    }

}
