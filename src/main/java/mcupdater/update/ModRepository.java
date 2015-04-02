package mcupdater.update;

public interface ModRepository<T extends IUpdatable> {

    Artifact<T> findArtifact(String artifactId);

}
