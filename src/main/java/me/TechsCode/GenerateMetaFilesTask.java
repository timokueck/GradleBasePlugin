package me.TechsCode;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.*;

public class GenerateMetaFilesTask extends DefaultTask {

    @TaskAction
    public void generateMetaFiles(Project project){
        System.out.println(Color.GREEN_BRIGHT+"Generating Plugin.yml & Bungee.yml");

        File build = project.getBuildDir();
        File pluginYml = new File(build.getAbsolutePath()+"/src/resources/plugin.yml");
        File bungeeYml = new File(build.getAbsolutePath()+"/src/resources/bungee.yml");

        try {
            pluginYml.createNewFile();
            bungeeYml.createNewFile();

            MetaExtension meta = project.getExtensions().getByType(MetaExtension.class);

            PrintWriter writer = new PrintWriter(pluginYml, "UTF-8");
            writer.println("name: "+project.getName());
            writer.println("version: "+meta.version);
            writer.println("main: me.TechsCode."+project.getName()+".base.loader.SpigotLoader");
            writer.println("api-version: 1.13");
            if(meta.loadAfter != null) writer.println("softdepend: "+meta.loadAfter);
            if(meta.loadBefore != null) writer.println("loadbefore: "+meta.loadBefore);
            if(meta.load != null) writer.println("load: "+meta.load);
            writer.close();

            writer = new PrintWriter(bungeeYml, "UTF-8");
            writer.println("name: "+project.getName());
            writer.println("version: "+meta.version);
            writer.println("main: me.TechsCode."+project.getName()+".base.loader.BungeeLoader");
            writer.println("author: Tech");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
