package mcupdater.update.libs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import mcupdater.Platform;

public class RemoteLibrary extends AbstractLibrary {

    String name;
    String url;

    @Override
    public String getGroup() {
        return name.split(":")[0];
    }

    @Override
    public String getName() {
        return name.split(":")[1];
    }

    @Override
    public String getVersion() {
        return name.split(":")[2];
    }

    public boolean hasClassifier() {
        return name.split(":").length == 4;
    }

    public String getClassifier() {
        return name.split(":")[3];
    }

    public String getArtifactID() {
        return name;
    }

    public String getURL() {
        if (url == null)
            return null;
        return url + (url.endsWith("/") ? "" : "/");
    }

    public boolean installed() {
        return getLocalFile().exists();
    }

    public String getRelativePathForDownload() {
        return getParentPath() + ".jar";
    }

    public String getRelativePath() {
        return getParentPath() + ".jar";
    }

    private String getParentPath() {
        return getGroup().replace('.', '/') + "/" + getName() + "/" + getVersion() + "/" + getName() + "-"
                + getVersion() + (hasClassifier() ? "-" + getClassifier() : "");
    }

    private File getLocalFile() {
        return new File(new File(Platform.getMinecraftHome(), "libraries"), getRelativePath());
    }

    public URL getRemoteURL() throws MalformedURLException {
        return new URL(getURL() + getRelativePathForDownload());
    }
}
