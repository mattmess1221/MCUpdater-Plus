package mcupdater.update.mods;

public class RemoteLiteMod extends RemoteMod {

    private String revision;

    @Override
    public String getVersion() {
        return revision;
    }
}
