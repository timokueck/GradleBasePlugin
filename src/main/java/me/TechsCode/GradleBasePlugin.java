package me.TechsCode;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

public class GradleBasePlugin implements Plugin<Project> {


    @Override
    public void apply(Project project) {
        System.out.println("Applying Plugin "+(new File(".").getAbsolutePath()));
        System.out.println(project.getBuildDir().getAbsoluteFile().getAbsolutePath());

        //project.getPluginManager().apply("com.github.johnrengelman.shadow");
        project.getDependencies().add("runtime", "com.github.jengelman.gradle.plugins:shadow:5.2.0");
        project.getPlugins().apply(com.github.jengelman.gradle.plugins.shadow.ShadowPlugin.class);
        project.getTasksByName("build", false).stream().findFirst().get().dependsOn("shadowJar");

        project.setProperty("sourceCompatibility", "1.8");
        project.setProperty("targetCompatibility", "1.8");

        String[] repositories = new String[]{
                "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
                "https://oss.sonatype.org/content/repositories/snapshots",
                "https://jitpack.io"
        };

        project.getRepositories().maven((maven) -> maven.setArtifactUrls(new HashSet<String>(Arrays.asList(repositories))));
        project.getRepositories().jcenter();
        project.getRepositories().mavenLocal();
        project.getRepositories().mavenCentral();

        String[] compileOnlyDependencies = new String[]{
                "org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT",
                "org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT",
                "net.md-5:bungeecord-api:1.12-SNAPSHOT"
        };

        project.getDependencies().add("compileOnly", compileOnlyDependencies);

        project.getExtensions().create("upload", UploadExtension.class);
        project.getTasks().create("upload", UploadTask.class);
    }


}
