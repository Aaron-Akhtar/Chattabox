package tech.akhtar.chattabox;

import tech.akhtar.chattabox.crypt.Aes;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;
import tech.akhtar.chattabox.file.manager.files.PropertiesFile;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Socks {
    /***
     * Socket that allows the receiver to send to the display server
     */
    public static Socket message_line = null;
    public static BufferedWriter writer = null;

    /***
     *
     * sends out packet to see if socket is still connected.
     *
      * @param socket Target Socket
     * @return boolean value based on whether or not the socket is s till connected
     */
    public static boolean isSocketConnected(Socket socket){
        try{
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(0);
            outputStream.flush();
            return true;
        }catch (Exception e){

        }
        return false;
    }

    /***
     * sends message to the display server.
     *
     * @param m The message to send
     */
    public static void sendMessage(String m){
        try {
            writer.write(Aes.encrypt(m, ChattaboxFile.getInfo(new PropertiesFile().getFile()).get("encryption-key").toString())+ "\n");
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
