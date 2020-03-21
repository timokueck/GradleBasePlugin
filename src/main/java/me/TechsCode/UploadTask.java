package me.TechsCode;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class UploadTask extends DefaultTask {

    @TaskAction
    public void upload(){
        UploadExtension extension = getProject().getExtensions().findByType(UploadExtension.class);

        if (!extension.isConfigured()) {
            GradleBasePlugin.log("Could not find defined Upload Settings to upload the compiled jar");
            return;
        }

        GradleBasePlugin.log("Uploading to "+extension.host+" with User "+extension.username+" in "+extension.path);
    }
}
