package mcupdater.update.mods;

/**
 * Declared by using the type "file" in the remote json. modid, file and md5 are
 * required.
 */
public class RemoteFileMod extends RemoteMod {

    @Override
    public String getVersion() {
        return this.getMD5();
    }
}
