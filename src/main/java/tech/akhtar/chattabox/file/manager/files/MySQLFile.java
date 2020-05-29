package tech.akhtar.chattabox.file.manager.files;

import tech.akhtar.chattabox.file.manager.ChattaboxFile;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLFile implements ChattaboxFile {

    @Override
    public File getFile() {
        File file = new File(ChattaboxFile.getChattaboxHomeDirectory() + "/mysql.txt");
        if (!file.exists()){
            try {
                if (file.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                        writer.println("# this config is only if you want to enable MySQL connectivity to allow moderators / administrators into your IRC Server");
                        writer.println("enabled=false");
                        writer.println("mysql-server=127.0.0.1");
                        writer.println("mysql-port=3306");
                        writer.println("mysql-database=chattabox");
                        writer.println("mysql-user=root");
                        writer.println("# if your server password is empty then put the password value as \"empty\"");
                        writer.println("mysql-password=root");
                    }
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public static Map<String, Object> getDatabaseInfo(){
        Map<String, Object> dbInfo = new HashMap<>();
        try{
            List<String> x = Files.readAllLines(Paths.get(new MySQLFile().getFile().getAbsolutePath()));
            for (String l : x){
                if (l.charAt(0) == '#') continue;
                if (!dbInfo.containsKey(l.split("=")[0])){
                    dbInfo.put(l.split("=")[0], (l.split("=")[1].equalsIgnoreCase("empty")) ? "" : l.split("=")[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dbInfo;
    }



}
