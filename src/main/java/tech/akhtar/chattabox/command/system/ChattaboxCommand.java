package tech.akhtar.chattabox.command.system;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import java.io.BufferedWriter;

public interface ChattaboxCommand {

    String getCommand();
    String getCommandDescription();
    Role getRequiredRole();
    void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler);

}
