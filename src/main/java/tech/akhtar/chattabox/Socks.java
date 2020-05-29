package tech.akhtar.chattabox;

import tech.akhtar.chattabox.crypt.Aes;
import tech.akhtar.chattabox.file.manager.files.PropertiesFile;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Socks {

    public static Socket message_line = null;
    public static BufferedWriter writer = null;

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

    public static void sendMessage(String m){
        try {
            writer.write(Aes.encrypt(m, PropertiesFile.getProps().get("encryption-key").toString())+ "\n");
            writer.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
