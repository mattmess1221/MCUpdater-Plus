package mcupdater.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mcupdater.UpdaterMain;
import mcupdater.download.DLAuthenticator;
import mcupdater.logging.LogHelper;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class LocalJson extends AbstractJson {

    private String modpack;
    private String version;
    private String repo;
    private List<String> disabled = Lists.newArrayList();

    private File localCache = new File(UpdaterMain.gameDir, "localcache.json");
    private RemoteJson remote;

    public LocalJson(Reader json) throws NullPointerException {
        super(json);
        if (object.has("disabled")) {
            JsonArray array = object.get("disabled").getAsJsonArray();
            for (JsonElement ele : array) {
                disabled.add(ele.getAsString());
            }
        }
        modpack = object.get("modpack").getAsString();
        version = object.get("version").getAsString();
        repo = object.get("repo").getAsString();

        if (object.has("username") && object.has("password")) {
            // register the username and password
            String username = object.get("username").getAsString();
            String password = object.get("password").getAsString();
            Authenticator.setDefault(new DLAuthenticator(username, password));
        }
    }

    public LocalJson(File file) throws NullPointerException, FileNotFoundException {
        this(new FileReader(file));
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

    public RemoteJson getRemotePack() throws IOException {
        if (remote == null) {
            Reader reader;
            try {
                reader = new InputStreamReader(getRemoteJson().openStream());
            } catch (IOException e) {
                if (localCache.exists() && localCache.isFile()) {
                    LogHelper.getLogger().warn(
                            "Unable to connect to update server.  Using local backup cache.");
                    reader = new FileReader(localCache);
                } else
                    throw new IOException(e);
            }
            this.remote = new RemoteJson(reader);
        }
        return this.remote;
    }

}
