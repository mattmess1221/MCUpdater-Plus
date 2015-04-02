package mcupdater.update;

import java.io.File;
import java.io.FileNotFoundException;

public class Artifact<T extends IUpdatable> implements IUpdatable {

    private LocalRepo<T> repository;
    private String artifactID;
    private File file;
    private T artifact;

    public Artifact(LocalRepo<T> repo, String artifact) throws FileNotFoundException {
        // TODO Auto-generated constructor stub
        this.repository = repo;
        this.artifactID = artifact;
        this.file = fileFromArtifact(repository.getDirectory(), artifact);
        if (!this.file.exists()) {
            throw new FileNotFoundException("Artifact with ID " + artifact + " in repo " + repo + " does not exist.");
        }
        this.artifact = repo.getInstanceCreator().apply(file);
    }

    private File fileFromArtifact(File dir, String artifact) {
        String[] args = artifact.split(":");
        String group = args[0].replace('.', '/');
        String name = args[1];
        String version = args[2];
        String classifier = null;
        if (args.length == 4) {
            classifier = args[3];
        }
        String filename = name + "-" + version + (classifier != null ? "-" + classifier : "") + ".jar";
        String path = String.format("%s/%s/%s/%s", group, name, version, filename);
        return new File(dir, path);
    }

    public T getArtifact() {
        return artifact;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getVersion() {
        return this.artifactID.split(":")[2];
    }
}
