package mcupdater.update;

public class RemoteRepo<T extends IUpdatable> implements ModRepository<T> {

    private String repo;
    private T[] artifacts;

    @Override
    public Artifact<T> findArtifact(String artifactId) {
        return null;
    }

    public String getRepo() {
        return repo;
    }

    public T[] getArtifacts() {
        return artifacts;
    }
}
