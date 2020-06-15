package tech.akhtar.chattabox.command.system.commands;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import tech.akhtar.chattabox.utils.Colour;

import java.io.BufferedWriter;

public class ClearCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/clear";
    }

    @Override
    public String getCommandDescription() {
        return "clear your screen";
    }

    @Override
    public Role getRequiredRole() {
        return Role.NORMAL;
    }

    /***
     * This method will clear the executors screen.
     *
     * @param args If the executor inputs additional arguments they will be specified in
     *             this String Array
     * @param executorUserClient The executors UserClient Object
     * @param writer The output stream writer that is used to print output to the client
     * @param mrsClientHandler The Receiver Client Handler Object for the executor
     */
    @Override
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try{
            writer.write(Colour.CLEAR.get());
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
