package me.TechsCode.GradeBasePlugin;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ResourceManager {

    public static boolean loadBasePlugin(String githubToken, String version){
        if(githubToken == null) return false;

        File libraryFolder = new File("libs");
        libraryFolder.mkdirs();

        String RETRIEVE_RELEASES = "https://api.github.com/repos/techscode/baseplugin/releases/tags/"+version+"?access_token="+githubToken;

        try {
            JSONParser parser = new JSONParser();
            String json = IOUtils.toString(new URI(RETRIEVE_RELEASES), "UTF-8");
            JSONObject root = (JSONObject) parser.parse(json);
            JSONArray assets = (JSONArray) root.get("assets");
            JSONObject asset = (JSONObject) assets.get(0);
            URL url = new URL((String) asset.get("url"));

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/octet-stream");
            connection.setRequestProperty("Authorization", "token "+githubToken);

            ReadableByteChannel uChannel = Channels.newChannel(connection.getInputStream());
            FileOutputStream foStream = new FileOutputStream(libraryFolder.getAbsolutePath()+"/BasePlugin.jar");
            FileChannel fChannel = foStream.getChannel();
            fChannel.transferFrom(uChannel, 0, Long.MAX_VALUE);
            uChannel.close();
            foStream.close();
            fChannel.close();
        } catch (IOException | URISyntaxException | ParseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void createGitIgnore(){
        File file = new File(".gitignore");

        try {
            file.delete();
            file.createNewFile();

            String[] lines = new String[]{
                    "*.DS_Store",
                    "*.iml",
                    ".idea/",
                    ".gradle/",
                    "out/",
                    "build/",
                    "libs/"
            };

            PrintWriter writer = new PrintWriter(file, "UTF-8");

            for(String line : lines){
                writer.println(line);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
