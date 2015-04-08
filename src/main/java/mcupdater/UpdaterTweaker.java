package mcupdater;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import mcupdater.Side.Sides;
import mcupdater.download.Downloader;
import mcupdater.logging.LogHelper;
import mcupdater.update.LocalArtifact;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.libs.RemoteLibrary;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.collect.Maps;

public class UpdaterTweaker implements ITweaker {

    private static final LogHelper logger = LogHelper.getLogger();
    private Updater updater;

    public UpdaterTweaker() {
        Side.setSide(Sides.CLIENT);
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        updater = new Updater(gameDir);
        updater.run(args.toArray(new String[args.size()]));

        // add additional arguments
        // needs to post to blackboard
        if (!Launch.blackboard.containsKey("launchArgs")) {
            Launch.blackboard.put("launchArgs", Maps.newHashMap());
        }
        @SuppressWarnings("unchecked")
        Map<String, String> launchArgs = (Map<String, String>) Launch.blackboard.get("launchArgs");
        String mods = updater.getAdditionalMods();
        if (mods != null) {
            launchArgs.put("--mods", mods);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        loadLibraries(classLoader);

        // Add cascaded tweaks
        registerTweaks();
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    private void loadLibraries(LaunchClassLoader classLoader) {
        for (RemoteLibrary remote : updater.getRemoteJson().getLibraries()) {
            if (!remote.installed()) {
                try {
                    Downloader.downloadLibrary(remote);
                } catch (IOException e) {
                    logger.error(String.format("Failed to download %s.", remote.getName()), e);
                }
            }
            LocalArtifact<LocalLibrary> local = updater.libraryRepo.findArtifact(remote.getArtifactID());

            local.getArtifact().loadLibrary(classLoader);
        }
    }

    private void registerTweaks() {
        for (String tweak : updater.getRemoteJson().getTweaks()) {
            registerTweak(tweak);
        }
    }

    @SuppressWarnings("unchecked")
    protected void registerTweak(String tweak) {
        List<String> tweaks = (List<String>) Launch.blackboard.get("TweakClasses");
        tweaks.add(tweak);
    }
}
