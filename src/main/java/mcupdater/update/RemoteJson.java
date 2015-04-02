package mcupdater.update;

import java.util.List;

import mcupdater.UpdatableList;
import mcupdater.update.libs.RemoteLibrary;
import mcupdater.update.mods.RemoteMod;

import com.google.common.collect.Lists;

public class RemoteJson extends AbstractJson {

    private UpdatableList<RemoteMod> mods;
    private RemoteRepo<RemoteLibrary> libraries;
    private RemoteRepo<RemoteMod> repo;
    public List<String> tweaks = Lists.newArrayList();
    private String additionalArguments;
    private Config config;

    public Config getConfig() {
        return config;
    }

    public UpdatableList<RemoteMod> getModsList() {
        return mods;
    }

    public RemoteRepo<RemoteLibrary> getLibrariesRepository() {
        return this.libraries;
    }

    public RemoteRepo<RemoteMod> getModsRepository() {
        return repo;
    }

    public String getAdditionalArguments() {
        return this.additionalArguments;
    }

}
