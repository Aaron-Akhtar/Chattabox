package tech.akhtar.chattabox.command.system.commands.admin.commands;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;

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

            if (userClient.getUsername().equalsIgnoreCase("root")){
                writer.write(Colour.RED.get() + "You cannot demote the 'root' user!");
                return;
            }

            userClient.demoteUser(writer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
