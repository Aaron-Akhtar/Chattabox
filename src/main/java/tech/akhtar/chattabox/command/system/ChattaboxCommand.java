package tech.akhtar.chattabox.command.system;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;
import java.io.BufferedWriter;

public interface ChattaboxCommand {

    /***
     * The command that should be executed in the Message Receiver Server in
     * order to execute this command.
     *
     * @return Command String
     */
    String getCommand();

    /***
     * The description of the command, this will be displayed via the help command
     * (tech.akhtar.chattabox.command.system.commands.HelpCommand.java).
     *
     * @return Command description
     */
    String getCommandDescription();

    /***
     * The required role the user must have in order to execute this command
     * (tech.akhtar.chattabox.Role.java).
     *
     * @return The required roles Role Object
     */
    Role getRequiredRole();

    /***
     * This method will be executed once a user inputs the according command.
     *
     * @param args If the executor inputs additional arguments they will be specified in
     *             this String Array
     * @param executorUserClient The executors UserClient Object
     * @param writer The output stream writer that is used to print output to the client
     * @param mrsClientHandler The Receiver Client Handler Object for the executor
     */
    void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler);

}
