package mcupdater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import mcupdater.Side.Sides;
import mcupdater.download.Downloader;
import mcupdater.logging.LogHelper;
import mcupdater.update.Artifact;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.libs.RemoteLibrary;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

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
        for (RemoteLibrary remote : updater.getRemoteJson().getLibrariesRepository().getArtifacts()) {
            if (!remote.installed()) {
                try {
                    Downloader.downloadLibrary(remote);
                } catch (IOException e) {
                    logger.error(String.format("Failed to download %s.", remote.getName()), e);
                }
            }
            Artifact<LocalLibrary> local = updater.libraryRepo.findArtifact(remote.getArtifactID());

            try {
                local.getArtifact().loadLibrary(classLoader);
            } catch (MalformedURLException e) {
                logger.error(String.format("Failed to load library: %s.", local.getFile().getPath()), e);
            }
        }
    }

    private void registerTweaks() {
        for (String tweak : updater.getRemoteJson().tweaks) {
            registerTweak(tweak);
        }
    }

    @SuppressWarnings("unchecked")
    protected void registerTweak(String tweak) {
        List<String> tweaks = (List<String>) Launch.blackboard.get("TweakClasses");
        tweaks.add(tweak);
    }
}
