package me.TechsCode.GradeBasePlugin.tasks;

import me.TechsCode.GradeBasePlugin.GradleBasePlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class RefreshTask extends DefaultTask {

    @TaskAction
    public void refresh(){
        GradleBasePlugin.log("Updating BasePlugin to get newest changes...");
    }
}
