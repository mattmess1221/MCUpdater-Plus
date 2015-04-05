package mcupdater.update;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Repository implements IRepository {

    private final URL url;

    public Repository(URL topUrl) {
        this.url = topUrl;
    }

    @Override
    public Artifact findArtifact(String artifactId) {
        return new Artifact(artifactId);
    }

    @Override
    public URL getDirectory() {
        return url;
    }

    public static class Serializer implements JsonDeserializer<Repository> {
        @Override
        public Repository deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return new Repository(new URL(json.getAsString()));
            } catch (MalformedURLException e) {
                throw new JsonParseException("Not a valid url: " + json.getAsString(), e);
            }
        }
    }
}
