package me.TechsCode;

public class MetaExtension {

    public String version;

    public boolean validate() {
        if(version == null){
            GradleBasePlugin.log("Could not find a 'meta' section with a 'version' field in your build.gradle");
            return true;
        }

        return false;
    }

}


