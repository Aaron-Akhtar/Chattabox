package tech.akhtar.chattabox.command.system.commands.admin;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;
import java.io.IOException;

public class DemoteCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/demote";
    }

    @Override
    public String getCommandDescription() {
        return "demote a user";
    }

    @Override
    public Role getRequiredRole() {
        return Role.ADMINISTRATOR;
    }

    /***
     * This method is used to demote a moderator / administrator to a normal user.
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

            if (!Chattabox.isMySQLEnabled){
                writer.write(Colour.RED.get() + "This command is disabled, please enable database connectivity to enable it...");
                return;
            }

            if (executorUserClient.getRole().getWeight() < getRequiredRole().getWeight()) {
                writer.write(Colour.RED.get() + "You need to have the ["+getRequiredRole().toString()+"] role to use this command");
                writer.flush();
                return;
            }

            // user
            if (args.length != 2){
                writer.write(Colour.RED.get() + "Incorrect Arguments: '/demote <user>");
                writer.flush();
                return;
            }
            UserClient userClient = new UserClient(args[1]);
            if (userClient.isAdministrator() && !executorUserClient.getUsername().equals("root")){
                writer.write(Colour.RED.get() + "Only the 'root' user can demote administrators...");
                return;
            }

            if (!userClient.isAdministrator() && !userClient.isModerator()){
                writer.write(userClient.getUsername() + " is not a staff member, please promote them to gain access to this... \r\n");
                writer.flush();
                return;
            }

            if (userClient.getUsername().equalsIgnoreCase("root")){
                writer.write(Colour.RED.get() + "You cannot demote the 'root' user!");
                return;
            }

            userClient.demoteUser();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
