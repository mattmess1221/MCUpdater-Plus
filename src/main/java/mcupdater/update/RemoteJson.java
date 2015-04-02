package mcupdater.update;

import java.util.List;

import mcupdater.UpdatableList;
import mcupdater.update.libs.RemoteLibrary;
import mcupdater.update.mods.RemoteMod;

import com.google.common.collect.Lists;

public class RemoteJson extends AbstractJson {

    private UpdatableList<RemoteMod> mods = new UpdatableList<RemoteMod>();
    private UpdatableList<RemoteLibrary> libraries = new UpdatableList<RemoteLibrary>();
    public List<String> tweaks = Lists.newArrayList();
    private String additionalArguments;
    private Config config;

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
