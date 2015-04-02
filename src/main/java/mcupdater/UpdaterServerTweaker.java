package mcupdater;

import net.minecraft.launchwrapper.LaunchClassLoader;

public class UpdaterServerTweaker extends UpdaterTweaker {

    public UpdaterServerTweaker() {
        Side.setSide(Side.Sides.SERVER);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        // not server side
    }
}
