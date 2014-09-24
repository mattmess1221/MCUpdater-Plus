package mcupdater.update.mods;

import java.io.File;

import mcupdater.update.IUpdatable;

public abstract class LocalMod extends AbstractMod {

    protected String name;
    protected String version;
    protected File file;

    public String getName() {
        return name;
    }

    public String getModID() {
        return modid;
    }

    public String getVersion() {
        return version;
    }

    public String getFile() {
        return file.getPath();
    }

    @Override
    public boolean equals(IUpdatable mod) {
        // TODO
        return false;

    }
}
