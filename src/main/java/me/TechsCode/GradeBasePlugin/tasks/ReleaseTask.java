package me.TechsCode.GradeBasePlugin.tasks;

import me.TechsCode.GradeBasePlugin.Color;
import me.TechsCode.GradeBasePlugin.DeploymentFile;
import me.TechsCode.GradeBasePlugin.GradleBasePlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Optional;

public class ReleaseTask extends DefaultTask {

    @TaskAction
    public void release(){
        Optional<DeploymentFile.Remote> releaseRemote = DeploymentFile.getReleaseRemote();

        if(releaseRemote.isPresent() && releaseRemote.get().isEnabled()){
            GradleBasePlugin.log(Color.GREEN_BRIGHT+"Releasing...");
            File file = new File(DeploymentFile.getLocalOutputPath()+"/"+getProject().getName()+".jar");
            releaseRemote.get().uploadFile(file);
        } else {
            GradleBasePlugin.log(Color.GREEN_BRIGHT+"Could not find release remote in deployment.json");
        }
    }
}
