package me.TechsCode;

public class MetaExtension implements ValidatableExtension {

    public String version;

    @Override
    public boolean validate() {
        if(version == null){
            System.out.println("Could not find a 'meta' section with a 'version' field in your build.gradle");
        }

        return false;
    }
}

