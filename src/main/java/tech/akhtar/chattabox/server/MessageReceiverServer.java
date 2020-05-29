package tech.akhtar.chattabox.server;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.server.runnables.MRSClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiverServer implements Runnable{

    private int port;

    public MessageReceiverServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(Chattabox.PREFIX + "MRS Started...");
            while(true){
                try{
                    Socket client = serverSocket.accept();
                    new Thread(new MRSClientHandler(client)).start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        if (Chattabox.THREADS.contains(Chattabox.receiver)) Chattabox.THREADS.remove(Chattabox.receiver);
    }
}
