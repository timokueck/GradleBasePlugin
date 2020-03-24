package me.TechsCode;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git;
import org.gradle.internal.impldep.org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Repository;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.HashSet;

public class GradleBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("meta", MetaExtension.class);
        project.getExtensions().create("upload", UploadExtension.class);

        project.getTasks().create("upload", UploadTask.class);
        project.getTasks().create("dev", DevTask.class);

        project.getPlugins().apply("com.github.johnrengelman.shadow");
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

            downloadBasePlugin(new File("libs"), meta.baseVersion);

            project.setProperty("version", meta.version);
            project.setProperty("sourceCompatibility", "1.8");
            project.setProperty("targetCompatibility", "1.8");

            //project.getDependencies().add("compile", "com.github.techscode:baseplugin:"+meta.baseVersion);
            project.getDependencies().add("compile", "com.github.techscode:baseplugin:"+meta.baseVersion);
        });

        project.getRepositories().jcenter();
        project.getRepositories().mavenLocal();
        project.getRepositories().mavenCentral();

        String[] userRepositories = new String[]{
                "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
                "https://oss.sonatype.org/content/repositories/sna112345 89Ü+>YXC VB´" +
                        "pshots",
                "https://jitpack.io"
        };

        for(String repository : userRepositories){
            project.getRepositories().maven((maven) -> maven.setUrl(repository));
        }

        /*

        project.getRepositories().maven((maven) -> maven.setArtifactUrls(new HashSet<String>(Arrays.asList(repositories))));
        project.getRepositories().jcenter();
        project.getRepositories().mavenLocal();
        project.getRepositories().mavenCentral();

        String[] compileOnlyDependencies = new String[]{
                "org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT",
                "org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT",
                "net.md-5:bungeecord-api:1.12-SNAPSHOT"
        };

        project.getDependencies().add("compileOnly", compileOnlyDependencies);*/
    }

    public static void log(String message){
        System.out.println(Color.BLACK_BOLD+message);
    }

    public static void log(){
        System.out.println();
    }

    public void downloadBasePlugin(File directory, String version){
        directory.mkdirs();

        String token = System.getenv("GITHUB_TOKEN");

        //String RETRIEVE_RELEASES = "https://api.github.com/repos/techscode/baseplugin/releases/tags/"+version+"?access_token="+token;
        String download = "https://api.github.com/repos/TechsCode/BasePlugin/releases/assets/18869444";

        try {
            URL url = new URL(download);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
