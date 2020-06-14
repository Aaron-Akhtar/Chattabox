package tech.akhtar.chattabox.command.system.commands;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class OnlineCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/online";
    }

    @Override
    public String getCommandDescription() {
        return "get all online users";
    }

    @Override
    public Role getRequiredRole() {
        return Role.NORMAL;
    }

    /***
     * This method will show the executor what clients are currently online.
     *
     * @param args If the executor inputs additional arguments they will be specified in
     *             this String Array
     * @param executorUserClient The executors UserClient Object
     * @param writer The output stream writer that is used to print output to the client
     * @param mrsClientHandler The Receiver Client Handler Object for the executor
     */
    @Override
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        try {
            writer.write("\r\n");
            List<UserClient> userClients = new ArrayList<>(Chattabox.userClientThreadMap.keySet());
            StringJoiner stringJoiner = new StringJoiner(", ");
            for (int x = 0; x < userClients.size(); x++) {
                UserClient userClient = userClients.get(x);
                stringJoiner.add(userClient.getUserClientSettings().getUsernameColour().get() + userClient.getUsername());
            }
            writer.write(stringJoiner.toString() + "\r\n");
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
