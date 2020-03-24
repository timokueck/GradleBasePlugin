package me.TechsCode;

import java.util.Random;

public class MetaExtension {

    public String version;
    public String baseVersion;
    public String loadAfter, loadBefore, load;

    public boolean validate() {
        if(version == null){
            GradleBasePlugin.log("Could not find a 'meta' section with a 'version' field in your build.gradle");
            return true;
        }

        if(baseVersion == null){
            GradleBasePlugin.log("Could not find a 'baseVersion' field in your build.gradle. Typically this is something like 'b"+new Random().nextInt(200) +"'");
            return true;
        }

        return false;
    }

}


