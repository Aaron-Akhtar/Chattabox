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

public class KickCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/kick";
    }

    @Override
    public String getCommandDescription() {
        return "kick a user from the server";
    }

    @Override
    public Role getRequiredRole() {
        return Role.MODERATOR;
    }

    @Override
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try{

            if (!Boolean.parseBoolean(MySQLFile.getDatabaseInfo().get("enabled").toString())){
                writer.write(Colour.RED.get() + "This command is disabled, please enable database connectivity to enable it...");
                return;
            }

            if (executorUserClient.getRole().getWeight() < getRequiredRole().getWeight()){
                writer.write(Colour.RED.get() + "You need to have the ["+getRequiredRole().toString()+"] role to use this command");
                writer.flush();
                return;
            }

            if (args.length < 3){
                writer.write(Colour.RED.get() + "Incorrect Arguments: '/kick <user> <kick reason>'");
                writer.flush();
                return;
            }

            UserClient userClient = UserClient.getOnlineUser(args[1]);
            if (userClient == null){
                writer.write(Colour.RED.get() + "Target user ["+args[1]+"] is offline");
                writer.flush();
                return;
            }

            if (userClient.getRole().getWeight() >= executorUserClient.getRole().getWeight()){
                writer.write(Colour.RED.get() + "You cannot kick someone who has a higher role than you");
                writer.flush();
                return;
            }

            StringJoiner stringJoiner = new StringJoiner(" ");
            for (int x = 2; x < args.length; x++){
                stringJoiner.add(args[x]);
            }
            userClient.kick(stringJoiner.toString());
            System.out.println(Chattabox.PREFIX + Colour.CYAN.get() + args[1] + " has been kicked by "+executorUserClient.getUsername()+" for: '"+mrsClientHandler.kick_reason+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
