package tech.akhtar.chattabox.server;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.server.runnables.MDSClientHandler;
import tech.akhtar.chattabox.server.runnables.MDSReceiverHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageDisplayServer extends Thread{
    public boolean isMessageLineConnected = false;
    private int port;

    public MessageDisplayServer(int port) {
        this.port = port;
    }

    @Override
    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(Chattabox.PREFIX + "MDS Started...");
            while(true){
                try{
                    Socket client = serverSocket.accept();
                    if (!isMessageLineConnected && client.getInetAddress().getHostAddress().equals("127.0.0.1")) {
                        new Thread(new MDSReceiverHandler(client, this)).start();
                    }else {
                        new Thread(new MDSClientHandler(client)).start();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Chattabox.THREADS.contains(Chattabox.display)) Chattabox.THREADS.remove(Chattabox.display);
    }

}
