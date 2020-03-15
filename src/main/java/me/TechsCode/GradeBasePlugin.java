package me.TechsCode;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradeBasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("uploadExt", UploadExtension.class);
        project.getTasks().create("upload", UploadTask.class);
    }
}
