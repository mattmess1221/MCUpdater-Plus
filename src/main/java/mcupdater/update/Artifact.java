package mcupdater.update;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Artifact implements IUpdatable {

    private String artifactID;
    private String path;

    public Artifact(String artifact) {
        this.artifactID = artifact;
        this.path = pathFromArtifact(artifact);
    }

    private String pathFromArtifact(String artifact) {

        String[] args = artifact.split(":");
        String group = args[0].replace('.', '/');
        String name = args[1];
        String version = args[2];
        String classifier = null;
        if (args.length == 4) {
            classifier = args[3];
        }
        String filename =
                name + "-" + version + (classifier != null ? "-" + classifier : "") + ".jar";
        String path = String.format("%s/%s/%s/%s", group, name, version, filename);
        return path;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getVersion() {
        return this.artifactID.split(":")[2];
    }

    public static class Serializer implements JsonDeserializer<Artifact> {
        @Override
        public Artifact deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Artifact(json.getAsString());
        }
    }
}
