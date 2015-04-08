package mcupdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mcupdater.download.Downloader;
import mcupdater.gui.ProgressWindow;
import mcupdater.gui.UpdateWindow;
import mcupdater.logging.LogHelper;
import mcupdater.logging.LogHelper.LogLevel;
import mcupdater.update.Artifact;
import mcupdater.update.Config;
import mcupdater.update.LocalArtifact;
import mcupdater.update.LocalJson;
import mcupdater.update.LocalRepository;
import mcupdater.update.RemoteJson;
import mcupdater.update.Repository;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.mods.LocalLiteMod;
import mcupdater.update.mods.LocalMod;
import mcupdater.update.mods.RemoteMod;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Updater {

    private static final LogHelper logger = LogHelper.getLogger();

    public UpdatableList<LocalMod> localMods = new UpdatableList<LocalMod>();
    public LocalRepository<LocalMod> modsRepo;
    public LocalRepository<LocalLibrary> libraryRepo;

    public File gameDir;
    private File localCache;
    private RemoteJson remote;
    private LocalJson local;
    private UpdateWindow window;

    private static Updater instance;

    public Updater() {
        this(new File("."));
    }

    public Updater(File file) {
        if (file == null)
            file = new File(".");
        gameDir = file;
        localCache = new File(gameDir, "localcache.json");
        window = ProgressWindow.newWindow();
        instance = this;
    }

    public static void main(final String[] args) throws Exception {
        Side.setSide(Side.Sides.SERVER);
        new Updater().run(args);
    }

    public void run(String[] args) {
        logger.info("Starting Updater");
        parseArguments(args);
        window.setVisible(true);
        File modpack = new File(gameDir, "modpack.json");
        if (modpack.exists()) {

            // local things
            try {
                window.setCurrentTask("Reading modpack info.", false);
                readJson(modpack);
            } catch (MalformedURLException e) {
                logger.warn("Modpack URL is invalid.", e);
            }
            try {
                window.setCurrentTask("Setting up local repos", false);
                setupRepos();
            } catch (IOException e) {
                logger.warn("Error while setting up repos", e);
            }
            window.setCurrentTask("Scanning installed mods.", false);
            readMods(new File(gameDir, "mods"));
            window.setCurrentTask("Reading " + local.getRemoteJson().toString() + ".", false);

            // remote
            getInfo();

            window.setMaximum(remote.getModsSize() + (remote.getConfig() != null ? 1 : 0));
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
                updateMods();
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
        ArgumentAcceptingOptionSpec<String> argLogLevel =
                parser.accepts("updaterLogLevel", "The log level to use.").withRequiredArg().defaultsTo("INFO");
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
            this.remote = getRemotePack(this.local);
        } catch (IOException e) {
            logger.error(String.format("Could not open modpack definition %s", local.getRemoteJson()), e);
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            logger.error(String.format("Bad JSON in %s", local.getRemoteJson()), e);
            throw new RuntimeException(e);
        }
    }

    private RemoteJson getRemotePack(LocalJson local) throws IOException {
        String json;
        try {
            json = IOUtils.toString(local.getRemoteJson().openStream());
        } catch (IOException e) {
            if (localCache.exists() && localCache.isFile()) {
                LogHelper.getLogger().warn("Unable to connect to update server.  Using local backup cache.");
                json = FileUtils.readFileToString(localCache);
            } else
                throw new IOException("Unable to access remote and local cache doesn't exist or is not a file.", e);
        }

        return new GsonBuilder().registerTypeAdapter(RemoteMod.class, new RemoteMod.Serializer())
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer()).create()
                .fromJson(json, RemoteJson.class);
    }

    private void updateMods() {
        compareModsDir();
        updateModsRepo();
    }

    private void compareModsDir() {
        for (RemoteMod remote : this.remote.getModsList()) {
            if (!remote.isEnabled()) {
                window.setCurrentTask("", true);
                logger.debug("Skipping " + remote.getModID());
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

    private void updateModsRepo() {
        if (remote.getModsRepo() == null)
            return;
        // setup remote repo
        URL url;
        try {
            URI uri = URI.create(this.remote.getModsRepo());
            if (uri.isAbsolute()) {
                url = uri.toURL();
            } else {
                url = new URL(this.local.getRemoteRepo() + "/" + uri);
            }
        } catch (MalformedURLException e) {
            logger.warn("Invalid remote mods maven repo url.", e);
            return;
        }
        Repository repo = new Repository(url);

        // check that versions are installed
        for (Artifact artifact : this.remote.getRepoMods()) {
            LocalArtifact<LocalMod> local = new LocalArtifact<LocalMod>(this.modsRepo, artifact.getArtifactID());
            if (!local.getFile().exists()) {
                try {
                    logger.info("Downloading " + artifact.getArtifactID());
                    window.setCurrentTask("Downloading " + artifact.getArtifactID().split(":")[2], false);
                    Downloader.downloadArtifact(repo, artifact, this.modsRepo);
                    window.setCurrentTask("", true);
                } catch (IOException e) {
                    logger.warn("Unable to download " + artifact.getArtifactID(), e);
                }
            } else {
                logger.debug(artifact.getArtifactID() + " is installed");
            }
        }
    }

    private boolean compareContainer(RemoteMod remote) {
        for (LocalMod local : localMods) {
            if (remote.getModID().equalsIgnoreCase(local.getModID())) {
                if (!remote.getVersion().equalsIgnoreCase(local.getVersion())) {
                    logger.info("Updating " + local.getName() + " " + local.getVersion() + " to " + remote.getVersion());
                    window.setCurrentTask(
                            String.format("Updating %s %s to %s", local.getName(), local.getVersion(),
                                    remote.getVersion()), false);
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
                    logger.debug(local.getModID() + " " + version + " is up to date.");
                }
                return true;
            }
        }
        logger.info(remote.getModID() + " not found.");
        return false;
    }

    private void readJson(File json) throws MalformedURLException {
        FileReader fr = null;
        try {
            fr = new FileReader(json);
            this.local = new Gson().fromJson(fr, LocalJson.class);
            this.local.authenticate();
        } catch (FileNotFoundException e) {
            logger.error("Local modpack.json not found.", e);
        } finally {
            IOUtils.closeQuietly(fr);
        }
    }

    private void readMods(File modsDir) {
        modsDir.mkdirs();
        for (File file : modsDir.listFiles()) {
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
                logger.error("Unable to read mod file " + file.getName() + " (" + e.getMessage() + ")");
            }
        }
    }

    private void setupRepos() throws IOException {
        this.modsRepo = new LocalRepository<LocalMod>(this.local.getLocalRepo(), new Function<File, LocalMod>() {

            @Override
            public LocalMod apply(File input) {
                return LocalMod.getMod(input);
            }
        });
        File libraries = new File(Platform.getMinecraftHome(), "libraries");
        libraryRepo = new LocalRepository<LocalLibrary>(libraries, new Function<File, LocalLibrary>() {

            @Override
            public LocalLibrary apply(File input) {
                return new LocalLibrary(input);
            }
        });
    }

    private void addMod(File file) throws IOException {
        LocalMod mod = LocalMod.getMod(file);
        if (mod != null)
            localMods.add(mod);
    }

    public static Updater getInstance() {
        if (instance == null)
            instance = new Updater();
        return instance;
    }

    public LocalJson getLocalJson() {
        return local;
    }

    public RemoteJson getRemoteJson() {
        return remote;
    }

    String getAdditionalMods() {
        // start with the additional arguments
        if (remote.getRepoMods().isEmpty()) {
            return null;
        }

        StringBuilder mods = new StringBuilder();
        for (Artifact artifact : this.remote.getRepoMods()) {
            if (mods.length() > 0) { // append a comma beforehand.
                mods.append(",");
            }
            mods.append(modsRepo.findPath(artifact));
        }

        return mods.toString();
    }
}
