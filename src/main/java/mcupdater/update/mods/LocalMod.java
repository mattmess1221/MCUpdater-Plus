package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;

import mcupdater.logging.LogHelper;
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

    public static LocalMod getMod(File file) throws IOException {
        LocalMod mod = null;
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        try {
            if (ext.equalsIgnoreCase("jar") || ext.equalsIgnoreCase("zip")) {
                mod = new LocalForgeMod(file);
            } else if (ext.equalsIgnoreCase("litemod")) {
                mod = new LocalLiteMod(file);
            }
        } catch (Exception e) {
            LogHelper.getLogger().warn(
                    file.getName() + " is invalid. " + String.format("(%s)", e.getLocalizedMessage()));
            mod = new LocalFileMod(file);
        }
        return mod;
    }
}
