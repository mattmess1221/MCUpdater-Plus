package mcupdater.update;

import java.util.List;

import mcupdater.UpdatableList;
import mcupdater.update.libs.RemoteLibrary;
import mcupdater.update.mods.RemoteMod;

import com.google.common.collect.Lists;

public class RemoteJson extends AbstractJson {

    private UpdatableList<RemoteMod> mods = new UpdatableList<RemoteMod>();
    private List<RemoteLibrary> libraries = Lists.newArrayList();
    private String repo;
    private List<Artifact> repoMods = Lists.newArrayList();
    private List<String> tweaks = Lists.newArrayList();
    private String additionalArguments;
    private Config config;

    public Config getConfig() {
        return config;
    }

    public UpdatableList<RemoteMod> getModsList() {
        return mods;
    }

    public List<RemoteLibrary> getLibraries() {
        return this.libraries;
    }

    public String getModsRepo() {
        return repo;
    }

    public String getAdditionalArguments() {
        return this.additionalArguments;
    }

    public List<String> getTweaks() {
        return tweaks;
    }

    public int getModsSize() {
        int size = 0;
        if (mods != null)
            size += mods.size();
        if (getRepoMods() != null) {
            size += getRepoMods().size();
        }
        return size;
    }

    public List<Artifact> getRepoMods() {
        return repoMods;
    }
}
