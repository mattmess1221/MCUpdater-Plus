package mcupdater.update.mods;

import com.google.gson.JsonObject;

public class RemoteLiteMod extends RemoteMod {

    public RemoteLiteMod(JsonObject object) {
        super(object);
        this.version = object.get("revision").getAsString();
    }

}
