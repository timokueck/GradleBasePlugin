package me.TechsCode.GradeBasePlugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DevTask extends DefaultTask {

    @TaskAction
    public void dev(){
        String version = (String) getProject().getVersion();

        getProject().setProperty("version", version+" DEV");
    }

}
