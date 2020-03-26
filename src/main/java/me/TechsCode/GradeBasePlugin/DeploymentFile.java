package me.TechsCode.GradeBasePlugin;

import org.apache.commons.io.FileUtils;
import org.gradle.api.internal.provider.Collectors;
import org.gradle.internal.impldep.com.google.gson.JsonElement;
import org.gradle.internal.impldep.com.jcraft.jsch.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class DeploymentFile {

    private JSONObject root;

    public DeploymentFile() {
        File file = new File("deployment.json");

        if(!file.exists()){
            try {
                InputStream src = ResourceManager.class.getResourceAsStream("/deployment.json");
                Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            JSONParser jsonParser = new JSONParser();
            root = (JSONObject) jsonParser.parse(json);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getLocalOutputPath(){
        JSONObject local = (JSONObject) root.get("local");
        return (String) local.get("path");
    }

    public List<Remote> getRemotes(){
        List<Remote> remotes = new ArrayList<>();

        for(Object object : (JSONArray) root.get("remotes")){
            JSONObject remote = (JSONObject) object;

            remotes.add(new Remote(remote));
        }

        return remotes;
    }

    public class Remote {

        private boolean enabled;
        private String hostname, username, password, path;

        public Remote(JSONObject jsonObject) {
            this.enabled = (boolean) jsonObject.get("enabled");
            this.hostname = (String) jsonObject.get("hostname");
            this.username = (String) jsonObject.get("username");
            this.password = (String) jsonObject.get("password");
            this.path = (String) jsonObject.get("path");
        }

        public void uploadFile(File file){
            if(!enabled) return;

            try {
                JSch jsch = new JSch();
                Session jschSession = jsch.getSession(username, hostname);
                jschSession.setPassword(password);
                jschSession.connect();
                ChannelSftp sftp = (ChannelSftp) jschSession.openChannel("sftp");
                sftp.put(file.getAbsolutePath(), path+"/"+file.getName());
                sftp.exit();
                jschSession.disconnect();
            } catch (JSchException | SftpException e) {
                e.printStackTrace();
            }
        }
    }
}
