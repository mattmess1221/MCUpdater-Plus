package mcupdater.update;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mcupdater.download.DLAuthenticator;
import mcupdater.logging.LogHelper;

public class LocalJson extends AbstractJson {

    private String modpack;
    private String version;
    private String repo;
    private List<String> disabled;

    private String username;
    private String password;

    public void authenticate() {
        if (username != null && password != null) {
            // register the username and password
            Authenticator.setDefault(new DLAuthenticator(username, password));
        }
    }

    public String getModpackName() {
        return modpack;
    }

    public String getModpackVersion() {
        return version;
    }

    public String getRepo() {
        return repo;
    }

    public URL getRemotePackURL() {
        try {
            return new URL(repo + (repo.endsWith("/") ? "" : "/") + modpack + "/" + version + "/");
        } catch (MalformedURLException e) {
            LogHelper.getLogger().error("The remote URL is invalid.", e);
            throw (new RuntimeException());
        }
    }

    public boolean isModDisabled(String modid) {
        if (disabled == null)
            return false;
        for (String mod : disabled)
            if (mod.equals(modid))
                return true;
        return false;
    }

    public URL getRemoteJson() {
        String s = getRemotePackURL().toString();
        URL url = null;
        try {
            url = new URL(s + "pack.json");
        } catch (MalformedURLException e) {
            LogHelper.getLogger().error(e.getLocalizedMessage());
            throw (new RuntimeException());
        }
        return url;
    }

}
