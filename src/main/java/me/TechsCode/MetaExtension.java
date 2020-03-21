package me.TechsCode;

public class MetaExtension implements ValidatableExtension {

    public String version;

    @Override
    public boolean validate() {
        if(version == null){
            GradleBasePlugin.log("Could not find a 'meta' section with a 'version' field in your build.gradle");
        }

        return false;
    }
}

