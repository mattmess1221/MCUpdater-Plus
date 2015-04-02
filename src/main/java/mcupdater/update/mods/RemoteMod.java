package mcupdater.update.mods;

import java.lang.reflect.Type;

import mcupdater.Side;
import mcupdater.UpdaterMain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public abstract class RemoteMod implements IMod {

    private static UpdaterMain mcup = UpdaterMain.getInstance();

    private String modid;
    private String version;
    private String file;
    private String md5;
    private Side.Sides side = Side.getSide();

    @Override
    public String getModID() {
        return this.modid;
    }

    @Override
    public String getName() {
        return this.getModID();
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    public String getFile() {
        return this.file;
    }

    public String getMD5() {
        return this.md5;
    }

    public boolean hasHash() {
        return md5 != null;
    }

    public boolean isEnabled() {
        return !mcup.getLocalJson().isModDisabled(getModID());
    }

    public Side.Sides getSide() {
        return side;
    }

    public static class Serializer implements JsonDeserializer<RemoteMod> {

        @Override
        public RemoteMod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Type modType = null;
            String type = "forge";
            if (object.has("type")) {
                type = object.get("type").getAsString();
            }

            if (type.equals("forge")) {
                modType = RemoteForgeMod.class;
            } else if (type.equals("liteloader")) {
                modType = RemoteLiteMod.class;
            } else if (type.equals("file")) {
                modType = RemoteFileMod.class;
            } else {
                throw new IllegalArgumentException("Unknown mod type: " + type);
            }

            return context.deserialize(json, modType);
        }
    }
}
