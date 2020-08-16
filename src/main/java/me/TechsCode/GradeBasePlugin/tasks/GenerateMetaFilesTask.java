package me.TechsCode.GradeBasePlugin.tasks;

import me.TechsCode.GradeBasePlugin.Color;
import me.TechsCode.GradeBasePlugin.GradleBasePlugin;
import me.TechsCode.GradeBasePlugin.extensions.MetaExtension;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GenerateMetaFilesTask extends DefaultTask {

    @TaskAction
    public void generateMetaFiles(){
        GradleBasePlugin.log(Color.GREEN_BRIGHT+"Generating Plugin.yml & Bungee.yml");

        File build = getProject().getBuildDir();
        File resourcesFolder = new File(build.getAbsolutePath()+"/resources/main");
        resourcesFolder.mkdirs();

        File pluginYml = new File(resourcesFolder.getAbsolutePath()+"/plugin.yml");
        File bungeeYml = new File(resourcesFolder.getAbsolutePath()+"/bungee.yml");

        try {
            pluginYml.createNewFile();
            bungeeYml.createNewFile();

            MetaExtension meta = getProject().getExtensions().getByType(MetaExtension.class);

            PrintWriter writer = new PrintWriter(pluginYml, "UTF-8");
            writer.println("name: "+getProject().getName());
            writer.println("version: "+meta.version);
            writer.println("build: "+getBuildNumber());
            writer.println("main: me.TechsCode."+getProject().getName()+".base.loader.SpigotLoader");
            writer.println("api-version: 1.13");
            if(meta.loadAfter != null) writer.println("softdepend: "+meta.loadAfter);
            if(meta.loadBefore != null) writer.println("loadbefore: "+meta.loadBefore);
            if(meta.load != null) writer.println("load: "+meta.load);
            writer.close();

            writer = new PrintWriter(bungeeYml, "UTF-8");
            writer.println("name: "+getProject().getName());
            writer.println("version: "+meta.version);
            writer.println("build: "+getBuildNumber());
            writer.println("main: me.TechsCode."+getProject().getName()+".base.loader.BungeeLoader");
            writer.println("author: Tech");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getBuildNumber(){
        File ciFile = new File(getProject().getProjectDir().getAbsolutePath()+"/ci.yml");

        if(ciFile.exists()){
            try {
                String line = FileUtils.readLines(ciFile, StandardCharsets.UTF_8).get(0).replace("build-", "");

                return Integer.parseInt(line);
            } catch (IOException e) {
                return 0;
            }
        }

        return 0;
    }
}
