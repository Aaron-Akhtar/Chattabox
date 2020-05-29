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
