package tech.akhtar.chattabox.server.runnables;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Socks;
import tech.akhtar.chattabox.file.manager.files.MOTDFile;
import tech.akhtar.chattabox.utils.Colour;
import tech.akhtar.chattabox.utils.WindowUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MDSClientHandler implements Runnable{
    public static Set<Socket> connectedClients = new HashSet<>();

    private static boolean isDup(InetAddress address){
        for (Socket socket : connectedClients){
            if (!Socks.isSocketConnected(socket)) return false;
            if (socket.getInetAddress().getHostAddress().equalsIgnoreCase(address.getHostAddress())) return true;
        }
        return false;
    }

    private Socket socket;

    public MDSClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            if (isDup(socket.getInetAddress())) {
                WindowUtils.setTerminalName(Chattabox.NAME + " - Oops :(", writer);
                writer.write(Colour.YELLOW.get() + "Sorry :(");
                writer.write(Colour.RED.get() + "There is already a connection to this server from your ip address...");
                writer.flush();
                Thread.sleep(10000);
            } else {
                WindowUtils.setTerminalName(Chattabox.NAME + " - Displaying all messages", writer);
                connectedClients.add(socket);
                while(true);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (connectedClients.contains(socket)) {
            connectedClients.remove(socket);
        }

    }
}
