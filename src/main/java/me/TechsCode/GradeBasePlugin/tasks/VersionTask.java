package me.TechsCode.GradeBasePlugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionTask extends DefaultTask {

    @TaskAction
    public void version(){
        System.out.println(getProject().getVersion());
    }
}
