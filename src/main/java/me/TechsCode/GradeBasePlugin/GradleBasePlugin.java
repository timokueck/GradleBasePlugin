package me.TechsCode.GradeBasePlugin;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import me.TechsCode.GradeBasePlugin.extensions.MetaExtension;
import me.TechsCode.GradeBasePlugin.extensions.UploadExtension;
import me.TechsCode.GradeBasePlugin.tasks.DevTask;
import me.TechsCode.GradeBasePlugin.tasks.GenerateMetaFilesTask;
import me.TechsCode.GradeBasePlugin.tasks.UploadTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Arrays;
import java.util.Optional;

public class GradleBasePlugin implements Plugin<Project> {

    private static final String[] repositories = new String[]{
            "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
            "https://oss.sonatype.org/content/repositories/snapshots",
            "https://jitpack.io"
    };

    private static final String[] dependencies = new String[]{
            "compileOnly#org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT",
            "compileOnly#org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT",
            "compileOnly#net.md-5:bungeecord-api:1.12-SNAPSHOT"
    };

    private static final String[] relocations = new String[]{
            "me.TechsCode.base#me.TechsCode.PROJECT_NAME.base",
            "me.TechsCode.tpl#me.TechsCode.PROJECT_NAME.tpl",
            "me.TechsCode.dependencies#me.TechsCode.PROJECT_NAME.dependencies"
    };

    private MetaExtension meta;
    private UploadExtension upload;

    private Optional<String> githubToken;

    @Override
    public void apply(Project project) {
        this.meta = project.getExtensions().create("meta", MetaExtension.class);
        this.upload = project.getExtensions().create("upload", UploadExtension.class);

        project.getTasks().create("upload", UploadTask.class);
        project.getTasks().create("dev", DevTask.class);
        project.getTasks().create("generateMetaFiles", GenerateMetaFilesTask.class);

        project.getPlugins().apply("com.github.johnrengelman.shadow");

        project.getTasks().getByName("build").dependsOn("shadowJar");
        project.getTasks().getByName("shadowJar").dependsOn("generateMetaFiles");

        githubToken = Optional.ofNullable(System.getenv("GITHUB_TOKEN"));

        project.afterEvaluate(this::onProjectEvaluation);
    }

    public void onProjectEvaluation(Project project){
        if(meta.validate()) return;
        if(upload.validate()) return;

        log(Color.GREEN_BOLD_BRIGHT+"Configuring Gradle Project - Build Settings...");
        log();
        log("Project Info:");
        log("Plugin: "+project.getName()+" on Version: "+meta.version);
        log();

        project.setProperty("version", meta.version);
        project.setProperty("sourceCompatibility", "1.8");
        project.setProperty("targetCompatibility", "1.8");
        project.getRepositories().jcenter();
        project.getRepositories().mavenLocal();
        project.getRepositories().mavenCentral();

        if(ResourceManager.loadBasePlugin(githubToken.get(), meta.baseVersion)){
            log("Successfully retrieved BasePlugin.jar from Github...");
        } else {
            log(Color.RED+"Could not retrieve BasePlugin.jar from Github... Using older build if available");
            log(Color.RED+"Make sure that you have set the GITHUB_TOKEN environment variable that has access to the BasePlugin repository");
        }

        ResourceManager.createGitIgnore();

        // Adding BasePlugin Dependency
        project.getDependencies().add("implementation", project.files("libs/BasePlugin.jar"));

        // Retrieving ShadeTask for Relocation
        ShadowJar shadowTask = (ShadowJar) project.getTasksByName("shadowJar", false).stream().findFirst().get();
        shadowTask.getArchiveFileName().set(project.getName()+".jar");

        // Adding Common Repositories for Spigpt & Bungee + more
        Arrays.stream(repositories)
                .forEach(url -> project.getRepositories().maven((maven) -> maven.setUrl(url)));

        // Adding Common Dependencies for Spigpt & Bungee + more
        Arrays.stream(dependencies)
                .map(entry -> entry.split("#"))
                .forEach(confAndUrl -> project.getDependencies().add(confAndUrl[0], confAndUrl[1]));

        // Relocating Tpl + Base Packages with dependencies
        Arrays.stream(relocations)
                .map(entry -> entry.split("#"))
                .forEach(fromTo -> shadowTask.relocate(fromTo[0], fromTo[1].replace("PROJECT_NAME", project.getName())));
    }

    public static void log(String message){
        System.out.println(Color.BLACK_BOLD+message);
    }

    public static void log(){
        System.out.println();
    }
}
