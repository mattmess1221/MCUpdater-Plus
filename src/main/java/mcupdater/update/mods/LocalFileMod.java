package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;

import mcupdater.logging.LogHelper;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class LocalFileMod extends LocalMod {

    private String name;
    private String modID;
    private String version;

    public LocalFileMod(File file) {
        super(file);
        this.name = file.getName();
        this.modID = file.getName();
        try {
            this.version = Files.hash(file, Hashing.md5()).toString();
        } catch (IOException e) {
            LogHelper.getLogger().warn("Unable to check hash of " + file.getName(), e);
        }
    }

    public String getModID() {
        return modID;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
