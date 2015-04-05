package mcupdater.update;

import java.net.URL;

public interface IRepository {

    Artifact findArtifact(String artifactId);

    URL getDirectory();

}
