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

    /***
     * Get the Properties File Object, if the file does not yet exist then one will be created.
     * The Properties File is used to customise Chattabox.
     *
     * @return File Object, returns null if a exception is thrown
     */
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

}
