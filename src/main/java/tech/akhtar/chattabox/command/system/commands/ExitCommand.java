package tech.akhtar.chattabox.command.system.commands;

import tech.akhtar.chattabox.Role;
import tech.akhtar.chattabox.Socks;
import tech.akhtar.chattabox.command.system.ChattaboxCommand;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;

import java.io.BufferedWriter;
import java.io.IOException;

public class ExitCommand implements ChattaboxCommand {

    @Override
    public String getCommand() {
        return "/exit";
    }

    @Override
    public String getCommandDescription() {
        return "exit the server";
    }

    @Override
    public Role getRequiredRole() {
        return Role.NORMAL;
    }

    @Override
    public void doAction(String[] args, UserClient executorUserClient, BufferedWriter writer, MRSClientHandler mrsClientHandler) {
        mrsClientHandler.shouldClose = true;

    }
}
