package mcupdater.update.libs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import mcupdater.Platform;

public class RemoteLibrary extends AbstractLibrary {

    private String group;
    private String name;
    private String version;
    private String url;
    private String classifier;

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public String getURL() {
        if (url == null)
            return null;
        return url + (url.endsWith("/") ? "" : "/");
    }

    public String getClassifier() {
        return classifier;
    }

    public boolean installed() {
        return getLocalFile().exists();
    }

    public boolean hasClassifier() {
        return classifier != null;
    }

    public String getRelativePathForDownload() {
        return getParentPath() + (hasClassifier() ? "-" + classifier : "") + ".jar";
    }

    public String getRelativePath() {
        return getParentPath() + ".jar";
    }

    private String getParentPath() {
        return group.replace('.', '/') + "/" + name + "/" + version + "/" + name + "-" + version;
    }

    private File getLocalFile() {
        return new File(new File(Platform.getMinecraftHome(), "libraries"), getRelativePath());
    }

    public URL getRemoteURL() throws MalformedURLException {
        return new URL(getURL() + getRelativePathForDownload());
    }
}
