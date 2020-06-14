package tech.akhtar.chattabox;

import org.reflections.Reflections;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.MOTDFile;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.file.manager.files.PropertiesFile;
import tech.akhtar.chattabox.mysql.Database;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.MessageDisplayServer;
import tech.akhtar.chattabox.server.MessageReceiverServer;
import tech.akhtar.chattabox.threads.EnsureMessageLineThread;
import tech.akhtar.chattabox.utils.Colour;

import java.net.InetAddress;
import java.util.*;

public class Chattabox {

    public static final Map<String, Object> mysqlMap = ChattaboxFile.getInfo(new MySQLFile().getFile());
    public static final Map<String, Object> propertiesMap = ChattaboxFile.getInfo(new PropertiesFile().getFile());
    private static final String VERSION = "1.1-PRODUCTION";
    public static Map<UserClient, Thread> userClientThreadMap = new HashMap<>();
    public static final String PREFIX = Colour.YELLOW.get() + "["+propertiesMap.get("name").toString()+"] " + Colour.RESET.get();
    public static final String MOD_PREFIX = Colour.WHITE.get() + "["+Colour.RED.get()+"Mod"+Colour.WHITE.get() +"] " + Colour.RESET.get();
    public static final String ADMIN_PREFIX = Colour.WHITE.get() + "["+Colour.CYAN.get()+"Admin"+Colour.WHITE.get() +"] " + Colour.RESET.get();
    public static Map<String, String> BANNED_USERS = new HashMap<>();
    public static InetAddress ADDRESS = null;
    public static final String NAME = propertiesMap.get("name").toString();
    public static final String PAYLOAD_PASS = propertiesMap.get("payload").toString();
    public static List<Thread> THREADS = new ArrayList<>();
    public static final int DISPLAY_PORT = Integer.parseInt(propertiesMap.get("display-port").toString());
    public static final int RECEIVER_PORT = Integer.parseInt(propertiesMap.get("receiver-port").toString());
    public static Thread receiver;
    public static Thread display;
    public static boolean isMySQLEnabled = Boolean.parseBoolean(mysqlMap.get("enabled").toString());

    public static final Set<Class<? extends ChattaboxCommand>> COMMANDS = new Reflections("tech.akhtar.chattabox.command.system.commands")
            .getSubTypesOf(ChattaboxCommand.class);

    public static void main(String[] args)throws Exception{
        System.out.println(Chattabox.PREFIX + "Starting Chattabox [Version "+VERSION+"]...");
        ADDRESS = InetAddress.getLocalHost();
        System.out.println(Chattabox.PREFIX + "Server Address = ["+ADDRESS.getHostAddress()+"]");
        receiver = new Thread(new MessageReceiverServer(RECEIVER_PORT));
        new MOTDFile().getFile(); //creates the motd file as without this line the motd file will only be created once a user connects to the receiver.
        display = new Thread(new MessageDisplayServer(DISPLAY_PORT));
        THREADS.add(receiver);
        THREADS.add(display);
        receiver.start();
        display.start();
        new EnsureMessageLineThread().start();
        if (isMySQLEnabled){
            Database.setup();
            UserClient userClient = new UserClient("root");
            if (!userClient.isAdministrator()){
                userClient.makeAdministrator();
                System.out.println(Chattabox.PREFIX + "'root' user has been made administrator and has the correct permissions to demote administrators...");
                System.out.println(Chattabox.PREFIX + "'root' user's 2FA Code: " + UserClient.getTwoFacAuthForUser(userClient.getUsername()));
            }

        }

    }

}
