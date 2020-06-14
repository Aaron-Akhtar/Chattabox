package tech.akhtar.chattabox.command.system.commands.admin;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;
import java.io.IOException;

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

    /***
     * This method is used to promote a target user to the moderator / administrator role.
     *
     * @param args If the executor inputs additional arguments they will be specified in
     *             this String Array
     * @param executorUserClient The executors UserClient Object
     * @param writer The output stream writer that is used to print output to the client
     * @param mrsClientHandler The Receiver Client Handler Object for the executor
     */
    @Override   @SuppressWarnings("Duplicates")
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try {

            if (!Boolean.parseBoolean(ChattaboxFile.getInfo(new MySQLFile().getFile()).get("enabled").toString())){
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
            if (userClient.isModerator() || userClient.isAdministrator()){
                writer.write(Colour.RED.get() + userClient.getUsername() + " is already a staff member, please demote them to continue... \r\n");
                writer.flush();
                return;

            }

            if (args[2].equalsIgnoreCase("administrator")) {
                if (!executorUserClient.getUsername().equals("root")){
                    writer.write(Colour.RED.get() + "Only the 'root' user can promote users to administrators...");
                    return;
                }
                userClient.makeAdministrator();
                writer.write(Colour.GREEN.get() + args[1] + " has been promoted! ");
                writer.write(args[1] + "'s Two Factor Code is '"+UserClient.getTwoFacAuthForUser(args[1])+"'");
                writer.flush();
            } else if (args[2].equalsIgnoreCase("moderator")) {
                userClient.makeModerator();
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
