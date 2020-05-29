package tech.akhtar.chattabox.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class WindowUtils {

    public static void setTerminalName(String n, BufferedWriter writer) throws IOException {
        writer.write("\033]0;"+n+"\007");
        writer.flush();
    }

}
