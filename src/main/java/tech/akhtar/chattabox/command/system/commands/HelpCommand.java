package tech.akhtar.chattabox.command.system.commands;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.file.manager.files.MySQLFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;

import java.io.BufferedWriter;

public class HelpCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getCommandDescription() {
        return "get a list of useful commands";
    }

    @Override
    public Role getRequiredRole() {
        return Role.NORMAL;
    }

    @Override
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try{
            writer.write("\r\n");
            for (Class<? extends ChattaboxCommand> commandClass : Chattabox.COMMANDS) {
                ChattaboxCommand chattaboxCommand = commandClass.newInstance();
                if (!Boolean.parseBoolean(MySQLFile.getDatabaseInfo().get("enabled").toString())){
                    if (chattaboxCommand.getRequiredRole().equals(Role.NORMAL)) {
                        writer.write(chattaboxCommand.getCommand() + " -> " + chattaboxCommand.getCommandDescription());
                        writer.write("\r\n");
                    }
                }else{
                    if (executorUserClient.getRole().getWeight() >= chattaboxCommand.getRequiredRole().getWeight()) {
                        writer.write(chattaboxCommand.getCommand() + " -> " + chattaboxCommand.getCommandDescription());
                        writer.write("\r\n");
                    }
                }


            }
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
