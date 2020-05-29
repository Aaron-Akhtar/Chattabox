package tech.akhtar.chattabox.file.manager.files;

import tech.akhtar.chattabox.Chattabox;
import tech.akhtar.chattabox.file.manager.ChattaboxFile;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertiesFile implements ChattaboxFile {

    @Override
    public File getFile() {
        File file = new File(ChattaboxFile.getChattaboxHomeDirectory() + "/default-settings.txt");
        if (!file.exists()){
            try {
                if (file.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                        writer.println("name=Chattabox");
                        writer.println("encryption-key=AkhtarTechnologies");
                        writer.println("payload=Chattabox-Default-Payload-Pass");
                        writer.println("display-port=3991");
                        writer.println("receiver-port=3992");
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

    public static Map<String, Object> getProps(){
        Map<String, Object> properties = new HashMap<>();
        try{
            List<String> x = Files.readAllLines(Paths.get(new PropertiesFile().getFile().getAbsolutePath()));
            for (String l : x){
                if (!properties.containsKey(l.split("=")[0])) properties.put(l.split("=")[0], l.split("=")[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return properties;
    }

}
