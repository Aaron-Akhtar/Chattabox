package tech.akhtar.chattabox.file.manager;

import java.io.File;

public interface ChattaboxFile {
    static String getChattaboxHomeDirectory(){
        File file = new File("./chattabox");
        if (!file.isDirectory()){
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    File getFile();

}
