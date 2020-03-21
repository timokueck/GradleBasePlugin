package me.TechsCode;

public class MetaExtension implements ValidatableExtension {

    private String version;

    @Override
    public boolean validate() {
        if(version == null){
            GradleBasePlugin.log("Could not find a 'meta' section with a 'version' field in your build.gradle");
            return true;
        }

        return false;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}


