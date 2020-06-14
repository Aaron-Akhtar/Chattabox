package tech.akhtar.chattabox.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class WindowUtils {

    /***
     * Sets the terminals title string.
     *
     * @param n The new terminal title string
     * @param writer BufferedWriter Object for the set socket.
     */
    public static void setTerminalName(String n, BufferedWriter writer) throws IOException {
        writer.write("\033]0;"+n+"\007");
        writer.flush();
    }

}
