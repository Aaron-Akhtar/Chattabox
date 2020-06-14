package tech.akhtar.chattabox.server.runnables;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.crypt.Aes;
import tech.akhtar.chattabox.crypt.Sha256;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.PropertiesFile;
import tech.akhtar.chattabox.objects.UserClient;
import tech.akhtar.chattabox.server.MessageDisplayServer;
import tech.akhtar.chattabox.utils.Colour;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MDSReceiverHandler implements Runnable{

    private Socket socket;
    private MessageDisplayServer messageDisplayServer;

    public MDSReceiverHandler(Socket socket, MessageDisplayServer messageDisplayServer) {
        this.socket = socket;
        this.messageDisplayServer = messageDisplayServer;
    }

    /***
     * Sends @msg to all connected clients.
     *
     * @param msg The message you want to output
     */
    private static void sendMessageToAllClients(String msg) {
        for (Socket socket : MDSClientHandler.connectedClients) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(msg + "\r\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * This method handles the message line socket (refer to tech.akhtar.chattabox.Socks.java),
     * it waits for the payload, once the payload is, received it acts accordingly.
     */
    @Override
    public void run() {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // Authentication Process ->
                /*
                To ensure the server connecting is in fact the server that will be transmitting the messages
                we need to run a security / authorization check, we do this by checking if the connection is local
                and we will now ask it for the specified Payload Pass.
                 */

            String payload = Sha256.getSha(Chattabox.PAYLOAD_PASS);
            writer.write("Payload?" + "\n");
            writer.flush();
            String x;
            if((x = reader.readLine()) != null && !x.equals(payload)){
                return;
            }
            messageDisplayServer.isMessageLineConnected = true;
            System.out.println(Chattabox.PREFIX + "Message Line connection established...");
            while((x = reader.readLine()) != null){
                String m = Aes.decrypt(x, Chattabox.propertiesMap.get("encryption-key").toString());
                if (m == null) continue;
                sendMessageToAllClients(Colour.YELLOW.get() + "["+new SimpleDateFormat("HH:mm:ss").format(new Date()) +"] " + m);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        messageDisplayServer.isMessageLineConnected = false;
        System.out.println(Chattabox.PREFIX + "Message Line disconnected...");
    }
}
