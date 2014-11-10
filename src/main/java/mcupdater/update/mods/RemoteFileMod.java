package mcupdater.update.mods;

import com.google.gson.JsonObject;

/**
 * Declared by using the type "file" in the remote json. modid, file and md5 are
 * required.
 */
public class RemoteFileMod extends RemoteMod {

    public RemoteFileMod(JsonObject object) {
        super(object);
        this.version = object.get("md5").getAsString();
    }

}
