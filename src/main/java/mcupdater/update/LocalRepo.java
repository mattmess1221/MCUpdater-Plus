package mcupdater.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.common.base.Function;

public class LocalRepo<T extends IUpdatable> implements ModRepository<T> {

    private final File topDirectory;
    private final Function<File, T> instanceCreator;

    public LocalRepo(File top, Function<File, T> function) throws IOException {
        if (!top.isDirectory()) {
            throw new IOException(top + " is not a directory.");
        }
        this.topDirectory = top;
        this.instanceCreator = function;
    }

    @Override
    public Artifact<T> findArtifact(String artifactId) {
        try {
            return new Artifact<T>(this, artifactId);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public File getDirectory() {
        return topDirectory;
    }

    public Function<File, T> getInstanceCreator() {
        return instanceCreator;
    }

    public void install(String artifactID) {
        // TODO Auto-generated method stub

    }

}
