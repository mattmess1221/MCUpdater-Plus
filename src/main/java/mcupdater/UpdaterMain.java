package mcupdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mcupdater.download.Downloader;
import mcupdater.gui.ProgressWindow;
import mcupdater.logging.LogHelper;
import mcupdater.logging.LogHelper.LogLevel;
import mcupdater.update.Config;
import mcupdater.update.LocalJson;
import mcupdater.update.RemoteJson;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.mods.LocalLiteMod;
import mcupdater.update.mods.LocalMod;
import mcupdater.update.mods.RemoteMod;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;

public class UpdaterMain {

    private static final LogHelper logger = LogHelper.getLogger();
    public UpdatableList<LocalMod> localMods = new UpdatableList<LocalMod>();
    public UpdatableList<LocalLibrary> localLibraries = new UpdatableList<LocalLibrary>();
    public static File gameDir;
    private RemoteJson remote;
    private LocalJson local;
    private ProgressWindow window;
    private static UpdaterMain instance;

    public UpdaterMain() {
        this(new File("."));
    }

    public UpdaterMain(File file) {
        gameDir = file;
        window = ProgressWindow.newWindow();
        instance = this;
    }

    public void main(String[] args) {
        logger.info("Starting Updater");
        parseArguments(args);
        window.setVisible(true);
        File modpack = new File(gameDir, "modpack.json");
        if (modpack.exists()) {
            try {
                window.setCurrentTask("Reading modpack info.", false);
                readJson(modpack);
            } catch (MalformedURLException e) {
                logger.warn("Modpack URL is invalid.", e);
            }
            window.setCurrentTask("Scanning installed mods.", false);
            readMods(new File(gameDir, "mods"));
            window.setCurrentTask("Reading " + local.getRemoteJson().toString() + ".", false);
            getInfo();
            window.setMaximum(remote.getModsList().size() + (remote.getConfig() != null ? 1 : 0));
            window.release();
            try {
                Config config = remote.getConfig();
                if (config != null) {
                    window.setCurrentTask("Downloading configs", false);
                    config.updateConfigs();
                    window.setCurrentTask("", true);
                }
            } catch (IOException ioe) {
                logger.error("Unable to extract configs", ioe);
            }
            if (local.getMCVersion().equals(remote.getMCVersion()))
                compareMods();
            else
                logger.info("Local pack mcversion mismatches remote.  Not downloading mods.");
        } else {
            logger.warn("modpack.json not found!");
        }
        window.dispose();
        logger.info("Everything up to date.");

    }

    private void parseArguments(String[] args) {

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        // log level
        ArgumentAcceptingOptionSpec<String> argLogLevel = parser
                .accepts("updaterLogLevel", "The log level to use.").withRequiredArg()
                .defaultsTo("INFO");
        OptionSet optionSet = parser.parse(args);

        // Set the log level
        String level = argLogLevel.value(optionSet);
        logger.setLevel(LogLevel.getLevel(level));
        if (logger.getLevel() == null) {
            throw new IllegalArgumentException("The specified log level doesn't exist.");
        }
        logger.debug("Log Level set to " + logger.getLevel().name());
    }

    private void getInfo() {
        try {
            this.remote = this.local.getRemotePack();
        } catch (IOException e) {
            logger.error(
                    String.format("Could not open modpack definition %s", local.getRemoteJson()), e);
            throw (new RuntimeException());
        } catch (JsonSyntaxException e) {
            logger.error(String.format("Bad JSON in %s", local.getRemoteJson()), e);
            throw (new RuntimeException());
        }
    }

    private void compareMods() {
        for (RemoteMod remote : this.remote.getModsList()) {
            if (!remote.isEnabled()) {
                window.setCurrentTask("", true);
                logger.info("Skipping " + remote.getModID());
                continue;
            }
            window.setCurrentTask("Downloading " + remote.getName(), false);
            if (!compareContainer(remote))
                try {
                    Downloader.downloadMod(remote);
                } catch (MalformedURLException e) {
                    logger.warn(remote.getName() + "'s download is invalid.", e);
                }
            window.setCurrentTask("", true);
        }
    }

    private boolean compareContainer(RemoteMod remote) {
        for (LocalMod local : localMods) {
            if (remote.getModID().equalsIgnoreCase(local.getModID())) {
                if (!local.getVersion().equalsIgnoreCase(remote.getVersion())) {
                    logger.info("Updating " + local.getName() + " " + local.getVersion() + " to "
                            + remote.getVersion());
                    window.setCurrentTask(
                            String.format("Updating %s %s to %s", local.getName(),
                                    local.getVersion(), remote.getVersion()), false);
                    try {
                        Downloader.downloadMod(remote, local);
                    } catch (MalformedURLException e) {
                        logger.warn(remote.getName() + "'s download is invalid.", e);
                    }
                } else {
                    String version;
                    if (local instanceof LocalLiteMod)
                        version = ((LocalLiteMod) local).getReadableVersion();
                    else
                        version = local.getVersion();
                    logger.info(local.getModID() + " " + version + " is up to date.");
                }
                return true;
            }
        }
        logger.info(remote.getModID() + " not found.");
        return false;
    }

    private void readJson(File json) throws MalformedURLException {
        try {
            this.local = new LocalJson(json);
        } catch (FileNotFoundException e) {
            logger.error("Local modpack.json not found.", e);
        }
    }

    private void readMods(File modsDir) {
        modsDir.mkdirs();
        for (File file : modsDir.listFiles())
            try {
                if (file.isFile())
                    addMod(file);
                else if (file.getName().equals(local.getMCVersion())) {
                    for (File file1 : file.listFiles()) {
                        if (file1.isFile()) {
                            addMod(file1);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Unable to read mod file " + file.getName() + " (" + e.getMessage()
                        + ")");
            }
    }

    private void addMod(File file) throws IOException {
        LocalMod mod = LocalMod.getMod(file);
        if (mod != null)
            localMods.add(mod);
    }

    public static UpdaterMain getInstance() {
        if (instance == null)
            instance = new UpdaterMain();
        return instance;
    }

    public LocalJson getLocalJson() {
        return local;
    }

    public RemoteJson getRemoteJson() {
        return remote;
    }

    public void readLibraries(File file) {
        List<File> libs = getRecursiveChildren(file);
        for (File lib : libs) {
            localLibraries.add(new LocalLibrary(lib));
        }
    }

    private List<File> getRecursiveChildren(File file) {
        List<File> files = Lists.newArrayList();
        if (file.exists())
            if (file.isFile())
                files.add(file);
            else
                for (File lib : file.listFiles())
                    if (lib.isDirectory())
                        files.addAll(getRecursiveChildren(lib));
                    else if (lib.getName().endsWith(".jar"))
                        files.add(lib);
        return files;
    }
}
