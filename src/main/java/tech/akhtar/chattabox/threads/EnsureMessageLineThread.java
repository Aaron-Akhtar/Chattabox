package tech.akhtar.chattabox.threads;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.Socks;
import tech.akhtar.chattabox.crypt.Sha256;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static tech.akhtar.chattabox.Socks.message_line;
import static tech.akhtar.chattabox.Socks.writer;

public class EnsureMessageLineThread extends Thread {

    /***
     * The message line (refer to tech.akhtar.chattabox.Socks.java) needs to be connected in order to broadcast messages,
     * this thread ensures that the message line is always connected, and if it is not connected it
     * will continue to try until it is connected.
     */
    @Override
    public void run() {
        System.out.println(Chattabox.PREFIX + "EML Started...");
        while(true) {
            if (!Socks.isSocketConnected(message_line)) {
                try{
                    Socket socket = new Socket(Chattabox.ADDRESS.getHostAddress(), Chattabox.DISPLAY_PORT);
                    message_line = socket;
                    writer = new BufferedWriter(new OutputStreamWriter(message_line.getOutputStream()));

                    try {
                        writer.write(Sha256.getSha(Chattabox.PAYLOAD_PASS) + "\n");
                        writer.flush();
                        System.out.println(Chattabox.PREFIX + "Message Line connection in progress...");
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try{
            Thread.sleep(10000);}catch (Exception e){}
        }
    }
}
