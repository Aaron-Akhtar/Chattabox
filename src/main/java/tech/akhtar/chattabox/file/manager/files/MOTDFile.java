package tech.akhtar.chattabox.file.manager.files;

import tech.akhtar.chattabox.file.manager.ChattaboxFile;

import java.io.*;

public class MOTDFile implements ChattaboxFile {

    /***
     * Get the MOTD File Object, if the file does not yet exist then one will be created.
     * The MOTD File is used to customize the welcome message that gets displayed to a new
     * client upon joining.
     *
     * @return File Object, returns null if a exception is thrown
     */
    @Override
    public File getFile() {
        File file = new File(ChattaboxFile.getChattaboxHomeDirectory() + "/motd.txt");
        if (!file.exists()){
            try {
                if (file.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                        writer.println("Welcome to Chattabox! Type ['/help'] for help...");
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

    /***
     * Get the MOTD from the MOTD File.
     *
     * @return MOTD String
     */
    public static String getMotd(){
        File file = new MOTDFile().getFile();
        StringBuilder motd = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String x;
            while((x = reader.readLine()) != null){
                motd.append(x + "\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return motd.toString();
    }

}
