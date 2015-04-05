package mcupdater.update;

import java.io.File;

public class LocalArtifact<T> extends Artifact {

    private LocalRepository<T> repository;

    public LocalArtifact(LocalRepository<T> repo, String artifact) {
        super(artifact);
        this.repository = repo;
    }

    public File getFile() {
        return new File(repository.getFile(), this.getPath());
    }

    public T getArtifact() {
        return this.repository.getInstanceCreator().apply(getFile());
    }

}
