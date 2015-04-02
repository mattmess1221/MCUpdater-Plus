package mcupdater.update.libs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import mcupdater.Platform;

public class RemoteLibrary extends AbstractLibrary {

    String artifact;
    String url;

    @Override
    public String getGroup() {
        return artifact.split(":")[0];
    }

    @Override
    public String getName() {
        return artifact.split(":")[1];
    }

    @Override
    public String getVersion() {
        return artifact.split(":")[2];
    }

    public boolean hasClassifier() {
        return artifact.split(":").length == 4;
    }

    public String getClassifier() {
        return artifact.split(":")[3];
    }

    public String getArtifactID() {
        return artifact;
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
        return getParentPath() + (hasClassifier() ? "-" + getClassifier() : "") + ".jar";
    }

    public String getRelativePath() {
        return getParentPath() + ".jar";
    }

    private String getParentPath() {
        return getGroup().replace('.', '/') + "/" + getName() + "/" + getVersion() + "/" + getName() + "-"
                + getVersion();
    }

    private File getLocalFile() {
        return new File(new File(Platform.getMinecraftHome(), "libraries"), getRelativePath());
    }

    public URL getRemoteURL() throws MalformedURLException {
        return new URL(getURL() + getRelativePathForDownload());
    }
}
