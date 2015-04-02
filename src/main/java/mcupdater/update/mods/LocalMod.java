package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;

import mcupdater.logging.LogHelper;

public abstract class LocalMod extends AbstractMod {

    private final File file;

    public LocalMod(File file) {
        this.file = file;
    }

    public String getFile() {
        return file.getPath();
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
            LogHelper.getLogger().warn(String.format("%S is invalid. (%s)", file.getName(), e.getLocalizedMessage()));
            mod = new LocalFileMod(file);
        }
        return mod;
    }
}
