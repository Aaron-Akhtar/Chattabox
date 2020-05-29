package tech.akhtar.chattabox.command.system.commands.admin.commands;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;

public class PromoteCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/promote";
    }

    @Override
    public String getCommandDescription() {
        return "promote a user";
    }

    @Override
    public Role getRequiredRole() {
        return Role.ADMINISTRATOR;
    }

    @Override   @SuppressWarnings("Duplicates")
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try {

            if (!Boolean.parseBoolean(MySQLFile.getDatabaseInfo().get("enabled").toString())){
                writer.write(Colour.RED.get() + "This command is disabled, please enable database connectivity to enable it...");
                return;
            }

            if (executorUserClient.getRole().getWeight() < getRequiredRole().getWeight()) {
                writer.write(Colour.RED.get() + "You need to have the ["+getRequiredRole().toString()+"] role to use this command");
                writer.flush();
                return;
            }
            // user rank
            if (args.length != 3) {
                writer.write(Colour.RED.get() + "Incorrect Arguments: '/promote <user> <moderator | administrator>'");
                writer.flush();
                return;
            }
            UserClient userClient = new UserClient(args[1]);

            if (args[2].equalsIgnoreCase("administrator")) {
                if (!executorUserClient.getUsername().equals("root")){
                    writer.write(Colour.RED.get() + "Only the 'root' user can promote users to administrators...");
                    return;
                }
                userClient.makeAdministrator(writer);
                writer.write(Colour.GREEN.get() + args[1] + " has been promoted! ");
                writer.write(args[1] + "'s Two Factor Code is '"+UserClient.getTwoFacAuthForUser(args[1])+"'");
                writer.flush();
            } else if (args[2].equalsIgnoreCase("moderator")) {
                userClient.makeModerator(writer);
                writer.write(Colour.GREEN.get() + args[1] + " has been promoted! ");
                writer.write(args[1] + "'s Two Factor Code is '"+UserClient.getTwoFacAuthForUser(args[1])+"'");
                writer.flush();
            } else {
                writer.write(Colour.RED.get() + "Incorrect Arguments: '/promote <user> <moderator | administrator>'");
                writer.flush();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
