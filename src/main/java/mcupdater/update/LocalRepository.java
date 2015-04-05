package mcupdater.update;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Function;

public class LocalRepository<T> implements IRepository {

    private final File topDirectory;
    private final Function<File, T> instanceCreator;

    public LocalRepository(File top, Function<File, T> function) throws IOException {
        if (!top.exists()) {
            top.mkdirs();
        } else if (!top.isDirectory()) {
            throw new IOException(top + " is not a directory.");
        }
        this.topDirectory = top;
        this.instanceCreator = function;
    }

    @Override
    public LocalArtifact<T> findArtifact(String artifactId) {
        return new LocalArtifact<T>(this, artifactId);
    }

    public File getFile() {
        return this.topDirectory;
    }

    @Override
    public URL getDirectory() {
        try {
            return getFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Function<File, T> getInstanceCreator() {
        return instanceCreator;
    }
}
