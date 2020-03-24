package me.TechsCode;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
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
import java.util.Arrays;

public class GradleBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("meta", MetaExtension.class);
        project.getExtensions().create("upload", UploadExtension.class);

        project.getTasks().create("upload", UploadTask.class);
        project.getTasks().create("dev", DevTask.class);
        project.getTasks().create("generateMetaFiles", GenerateMetaFiles.class);

        project.getPlugins().apply("com.github.johnrengelman.shadow");

        project.getTasksByName("build", false).stream().findFirst().get().dependsOn("generateMetaFiles");
        project.getTasksByName("build", false).stream().findFirst().get().dependsOn("shadowJar");

        project.afterEvaluate((p) -> {
            MetaExtension meta = project.getExtensions().getByType(MetaExtension.class);
            UploadExtension upload = project.getExtensions().getByType(UploadExtension.class);

            if(meta.validate()){
                log();
                log(Color.RED+"Please check the GitHub page of GradleBasePlugin for more information");
                return;
            }

            if(upload.validate()){
                log();
                log(Color.RED+"Please check your SFTP Upload Settings and retry.");
                return;
            }

            log(Color.GREEN_BOLD_BRIGHT+"Configuring Gradle Project - Build Settings...");
            log();
            log("Project Info:");
            log("Plugin: "+project.getName()+" on Version: "+meta.version);
            log();

            if(System.getenv("GITHUB_TOKEN") != null){
                log("Loading BasePlugin.jar from Github...");

                try {
                    downloadBasePlugin(new File("libs"), meta.baseVersion);

                    log("Done!");
                } catch (ParseException | URISyntaxException | IOException e) {
                    log("Could not load BasePlugin from Github: "+e.getMessage());
                    return;
                }
            }

            createGitIgnore(new File(".gitignore"));

            project.setProperty("version", meta.version);
            project.setProperty("sourceCompatibility", "1.8");
            project.setProperty("targetCompatibility", "1.8");

            project.getDependencies().add("implementation", project.files("libs/BasePlugin.jar"));

            project.getRepositories().jcenter();
            project.getRepositories().mavenLocal();
            project.getRepositories().mavenCentral();

            String[] userRepositories = new String[]{
                    "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
                    "https://oss.sonatype.org/content/repositories/snapshots",
                    "https://jitpack.io"
            };

            String[] compileOnlyDependencies = new String[]{
                    "org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT",
                    "org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT",
                    "net.md-5:bungeecord-api:1.12-SNAPSHOT"
            };

            for(String repository : userRepositories){
                project.getRepositories().maven((maven) -> maven.setUrl(repository));
            }

            for(String dependency : compileOnlyDependencies){
                project.getDependencies().add("compileOnly", dependency);
            }
        });
    }

    public static void log(String message){
        System.out.println(Color.BLACK_BOLD+message);
    }

    public static void log(){
        System.out.println();
    }

    public void downloadBasePlugin(File directory, String version) throws ParseException, URISyntaxException, IOException {
        directory.mkdirs();

        String token = System.getenv("GITHUB_TOKEN");

        String RETRIEVE_RELEASES = "https://api.github.com/repos/techscode/baseplugin/releases/tags/"+version+"?access_token="+token;

        JSONParser parser = new JSONParser();
        String json = IOUtils.toString(new URI(RETRIEVE_RELEASES), "UTF-8");
        JSONObject root = (JSONObject) parser.parse(json);
        JSONArray assets = (JSONArray) root.get("assets");
        JSONObject asset = (JSONObject) assets.get(0);

        URL url = new URL((String) asset.get("url"));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/octet-stream");
        connection.setRequestProperty("Authorization", "token "+token);
        ReadableByteChannel uChannel = Channels.newChannel(connection.getInputStream());
        FileOutputStream foStream = new FileOutputStream(directory.getAbsolutePath()+"/BasePlugin.jar");
        FileChannel fChannel = foStream.getChannel();
        fChannel.transferFrom(uChannel, 0, Long.MAX_VALUE);
        uChannel.close();
        foStream.close();
        fChannel.close();
    }

    public void createGitIgnore(File file){
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
