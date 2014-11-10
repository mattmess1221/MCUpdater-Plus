package mcupdater.update.mods;

import mcupdater.Side;
import mcupdater.UpdaterMain;

import com.google.gson.JsonObject;

public abstract class RemoteMod extends AbstractMod {

    protected String version;
    private String url;
    private String md5;
    private Side.Sides side;
    private boolean enabled;
    private UpdaterMain mcup = UpdaterMain.getInstance();

    public RemoteMod(JsonObject object) {
        this.modid = object.get("modid").getAsString();
        this.url = object.get("file").getAsString();
        if (object.has("md5"))
            this.md5 = object.get("md5").getAsString();
        if (object.has("side"))
            side = Side.Sides.valueOf(object.get("side").getAsString());
        else
            side = Side.getSide();

        enabled = !mcup.getLocalJson().isModDisabled(modid);
    }

    @Override
    public String getName() {
        return this.getModID();
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getFile() {
        return this.url;
    }

    public String getMD5() {
        return this.md5;
    }

    public boolean hasHash() {
        return md5 != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Side.Sides getSide() {
        return side;
    }

    public static RemoteMod getMod(JsonObject object) {
        RemoteMod mod = null;
        String type;
        if (object.has("type")) {
            type = object.get("type").getAsString();
        } else {
            type = "forge";
        }
        if (type.equals("forge")) {
            mod = new RemoteForgeMod(object);
        } else if (type.equals("liteloader")) {
            mod = new RemoteLiteMod(object);
        } else if (type.equals("file")) {
            mod = new RemoteFileMod(object);
        }

        return mod;
    }
}
