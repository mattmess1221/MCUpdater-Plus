package mcupdater.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import mcupdater.Side;
import mcupdater.UpdatableList;
import mcupdater.UpdaterMain;
import mcupdater.logging.LogHelper;
import mcupdater.update.libs.RemoteLibrary;
import mcupdater.update.mods.RemoteMod;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class RemoteJson extends AbstractJson {

    private UpdatableList<RemoteMod> mods = new UpdatableList<RemoteMod>();
    private UpdatableList<RemoteLibrary> libraries = new UpdatableList<RemoteLibrary>();
    public List<String> tweaks = Lists.newArrayList();
    private String additionalArguments;
    private Config config;
    private File cache = new File(UpdaterMain.gameDir, "localcache.json");

    public RemoteJson(Reader json) throws IOException {
        super(json);
        if (object.has("config"))
            this.config = new Config(object.get("config"));
        if (object.has("mods"))
            this.addMods(object.get("mods").getAsJsonArray());
        if (object.has("libraries"))
            this.addLibraries(object.get("libraries").getAsJsonArray());
        if (object.has("tweakClasses"))
            this.addTweaks(object.get("tweakClasses").getAsJsonArray());
        if (object.has("additionalArguments"))
            this.additionalArguments = object.get("additionalArguments").getAsString();

        // Saves cache
        String string = gson.toJson(object);
        FileUtils.writeStringToFile(this.cache, string);
    }

    private void addTweaks(JsonArray array) {
        for (JsonElement ele : array)
            tweaks.add(ele.getAsString());
    }

    private void addLibraries(JsonArray array) {
        for (JsonElement ele : array)
            libraries.add(new RemoteLibrary(ele.getAsJsonObject()));
    }

    private void addMods(JsonArray array) {
        for (JsonElement ele : array) {
            if (!ele.isJsonObject()) {
                LogHelper.getLogger().warn("Encountered a non-object: " + ele.getAsString() + ". Skipping.");
                break;
            }

            RemoteMod mod = RemoteMod.getMod(ele.getAsJsonObject());
            if (mod != null && Side.getSide() == mod.getSide())
                mods.add(mod);
        }
    }

    public RemoteJson(URL url) throws IOException {
        this(new InputStreamReader(url.openStream()));
    }

    public Config getConfig() {
        return config;
    }

    public UpdatableList<RemoteMod> getModsList() {
        return mods;
    }

    public UpdatableList<RemoteLibrary> getLibrariesList() {
        return this.libraries;
    }

    public String getAdditionalArguments() {
        return this.additionalArguments;
    }

}
