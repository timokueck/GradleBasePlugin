package me.TechsCode;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class UploadTask extends DefaultTask {

    @TaskAction
    public void upload(){
        UploadExtension extension = getProject().getExtensions().findByType(UploadExtension.class);

        if (extension == null) {
            System.err.println("Could not find defined Upload Settings to upload the compiled jar");
            return;
        }

        System.out.println("Uploading to "+extension.host+" with user "+extension.username);
    }
}
