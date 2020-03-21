package me.TechsCode;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git;
import org.gradle.internal.impldep.org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Repository;

import java.io.File;

public class GitInteractor {

    public static GitInteractor instance;

    public static void initialize(File projectDirectory) {
        instance = new GitInteractor(projectDirectory);
    }

    private Git git;

    private GitInteractor(File projectDirectory) {
        try {
            git = Git.init().setDirectory(projectDirectory).call();
            git.submoduleAdd()
                    .setPath("BasePlugin")
                    .setURI("https://github.com/TechsCode/BasePlugin.git")
                    .call();
        } catch (GitAPIException e) {
            GradleBasePlugin.log(Color.RED+"Could not interact with Git. Is git installed?");
        }
    }

    public void refresh(){
        try {
            git.submoduleUpdate().addPath("BasePlugin").call();
        } catch (GitAPIException e) {
            GradleBasePlugin.log(Color.RED+"Could not interact with Git. Is git installed?");
        }
    }
}
