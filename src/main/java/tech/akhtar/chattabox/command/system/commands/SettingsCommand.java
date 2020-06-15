package tech.akhtar.chattabox.command.system.commands;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class SettingsCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/settings";
    }

    @Override
    public String getCommandDescription() {
        return "customize your user settings";
    }

    @Override
    public Role getRequiredRole() {
        return Role.NORMAL;
    }

    /***
     * This method allows the executor to modify there unique settings from there
     * username colour to there message colour.
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
            if (args.length != 3) {
                writer.write(Colour.WHITE.get() + "Types of things you can modify: \r\n");
                writer.write("  message-colour [Change the colour of your messages text]  \r\n");
                writer.write("  name-colour [Change the colour of your username] \r\n");
                writer.write("Example: '/settings message-colour <COLOUR>' \r\n");
                writer.flush();
                return;
            }

            if (args[1].equalsIgnoreCase("message-colour")){
                try{
                    Colour colour = Colour.valueOf(args[2].toUpperCase());
                    executorUserClient.getUserClientSettings().setMessageColour(colour);
                }catch (IllegalArgumentException e){
                    writer.write(Colour.RED.get() + "Invalid Colour Type... \r\n");
                    writer.flush();
                }
            }else if (args[1].equalsIgnoreCase("name-colour")){
                try{
                    Colour colour = Colour.valueOf(args[2].toUpperCase());
                    executorUserClient.getUserClientSettings().setUsernameColour(colour);
                    mrsClientHandler.prefix = MRSClientHandler.getPrefix(executorUserClient);
                    writer.write(Colour.GREEN.get() + "Name colour changed successfully!");
                }catch (IllegalArgumentException e){
                    writer.write(Colour.RED.get() + "Invalid Colour Type... \r\n");
                }
            }else{
                writer.write(Colour.WHITE.get() + "Types of things you can modify: \r\n");
                writer.write("  message-colour [Change the colour of your messages text]  \r\n");
                writer.write("  name-colour [Change the colour of your username] \r\n");
                writer.write("Example: '/settings message-colour <COLOUR>' \r\n");
                writer.flush();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
