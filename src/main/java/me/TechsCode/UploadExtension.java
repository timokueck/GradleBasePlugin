package me.TechsCode;

import java.util.ArrayList;
import java.util.List;

public class UploadExtension implements ValidatableExtension {

    public String host, username, password, path;

    @Override
    public boolean validate() {
        // If all fields are unset, it is not configured
        if(!isConfigured()){
            return false;
        }

        if(host == null){
            GradleBasePlugin.log("The 'host' setting has not been specified. Example of a hostname is 'somedomain.com' or an ip address");
            return true;
        }

        if(username == null){
            GradleBasePlugin.log("The 'username' Setting has not been specified, Please use a valid ftp user with access rights");
            return true;
        }

        if(password == null){
            GradleBasePlugin.log("The 'password' Setting has not been specified, Please use the valid password for user '"+username+"'");
            return true;
        }

        if(path == null){
            GradleBasePlugin.log("The 'path' Setting has not been specified, Please use an absolute path to the folder on the server");
            return true;
        }

        return false;
    }

    public boolean isConfigured(){
        return !(host == null && username == null && password == null && path == null);
    }
}
