package tech.akhtar.chattabox;

import org.reflections.Reflections;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
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
    public static final String VERSION = "1.0-PRODUCTION";
    public static Map<UserClient, Thread> userClientThreadMap = new HashMap<>();
    public static String PREFIX = Colour.YELLOW.get() + "["+PropertiesFile.getProps().get("name").toString()+"] " + Colour.RESET.get();
    public static String MOD_PREFIX = Colour.WHITE.get() + "["+Colour.RED.get()+"Mod"+Colour.WHITE.get() +"] " + Colour.RESET.get();
    public static String ADMIN_PREFIX = Colour.WHITE.get() + "["+Colour.CYAN.get()+"Admin"+Colour.WHITE.get() +"] " + Colour.RESET.get();

    public static Map<String, String> BANNED_USERS = new HashMap<>();

    public static InetAddress ADDRESS = null;
    public static String NAME = PropertiesFile.getProps().get("name").toString();
    public static String PAYLOAD_PASS = PropertiesFile.getProps().get("payload").toString();
    public static List<Thread> THREADS = new ArrayList<>();
    public static int DISPLAY_PORT = Integer.parseInt(PropertiesFile.getProps().get("display-port").toString());
    public static int RECEIVER_PORT = Integer.parseInt(PropertiesFile.getProps().get("receiver-port").toString());
    public static Thread receiver;
    public static Thread display;

    public static Set<Class<? extends ChattaboxCommand>> COMMANDS = new Reflections("tech.akhtar.chattabox.command.system.commands")
            .getSubTypesOf(ChattaboxCommand.class);

    public static void main(String[] args)throws Exception{
        System.out.println(Chattabox.PREFIX + "Starting...");
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
        if (Boolean.parseBoolean(MySQLFile.getDatabaseInfo().get("enabled").toString())){
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
