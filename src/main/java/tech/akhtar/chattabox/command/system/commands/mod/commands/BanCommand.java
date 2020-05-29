package tech.akhtar.chattabox.command.system.commands.mod.commands;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;
import java.util.StringJoiner;

public class BanCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/ban";
    }

    @Override
    public String getCommandDescription() {
        return "ban a user from this server";
    }

    @Override
    public Role getRequiredRole() {
        return Role.MODERATOR;
    }

    @Override   @SuppressWarnings("Duplicates")
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try {

            if (!Boolean.parseBoolean(MySQLFile.getDatabaseInfo().get("enabled").toString())){
                writer.write(Colour.RED.get() + "This command is disabled, please enable database connectivity to enable it...");
                return;
            }

            if (executorUserClient.getRole().getWeight() < getRequiredRole().getWeight()) {
                writer.write(Colour.RED.get() + "You need to have the [" + getRequiredRole().toString() + "] role to use this command");
                writer.flush();
                return;
            }

            if (args.length < 3) {
                writer.write(Colour.RED.get() + "Incorrect Arguments: '/ban <user> <ban reason>'");
                writer.flush();
                return;
            }

            UserClient userClient = UserClient.getOnlineUser(args[1]);
            if (userClient == null) {
                writer.write(Colour.RED.get() + "Target user [" + args[1] + "] is offline");
                writer.flush();
                return;
            }

            if (userClient.getRole().getWeight() >= executorUserClient.getRole().getWeight()) {
                writer.write(Colour.RED.get() + "You cannot ban someone who has a higher role than you");
                writer.flush();
                return;
            }
            StringJoiner stringJoiner = new StringJoiner(" ");
            for (int x = 2; x < args.length; x++){
                stringJoiner.add(args[x]);
            }
            userClient.kick(stringJoiner.toString());
            Chattabox.BANNED_USERS.put(userClient.getUsername().toLowerCase(), stringJoiner.toString());
            System.out.println(Chattabox.PREFIX + Colour.CYAN.get() + userClient.getUsername() + " was banned by " + executorUserClient.getUsername() + " for: " + stringJoiner.toString());
            writer.write(Colour.RED.get() + "User successfully bannend");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
