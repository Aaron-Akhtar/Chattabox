package tech.akhtar.chattabox.file.manager;

import tech.akhtar.chattabox.file.manager.files.MySQLFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ChattaboxFile {
    /***
     * Get the directory that contains all the Chattabox files, if the
     * directory does not exist one will be created.
     *
     * @return Path of the directory
     */
    static String getChattaboxHomeDirectory(){
        File file = new File("./chattabox");
        if (!file.isDirectory()){
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    static Map<String, Object> getInfo(File file){
        Map<String, Object> info = new HashMap<>();
        try{
            List<String> x = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            for (String l : x){
                if (l.charAt(0) == '#') continue;
                if (!info.containsKey(l.split("=")[0])){
                    info.put(l.split("=")[0], l.split("=")[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return info;
    }

    File getFile();

}
