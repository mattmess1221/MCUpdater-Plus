package mcupdater.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import mcupdater.UpdaterMain;
import mcupdater.logging.LogHelper;

import com.google.gson.annotations.SerializedName;

public class Config {

    private static final LogHelper logger = LogHelper.getLogger();

    private transient File gameDir;
    private transient String localVersion = "";

    private String file;
    @SerializedName("version")
    private String remoteVersion = "";

    public Config() throws IOException {
        this.gameDir = UpdaterMain.getInstance().gameDir;
        localVersion = getLocalVersion(gameDir);

    }

    private String getLocalVersion(File gameDir) throws IOException {
        File version = new File(new File(gameDir, "config"), "version");
        if (!version.exists())
            return "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(version)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        return sb.toString();
    }

    private boolean shouldUpdate() {
        return !localVersion.equals(remoteVersion);
    }

    public void updateConfigs() throws IOException {
        if (!shouldUpdate()) {
            logger.info("Configs up to date.");
            return;
        }
        logger.info("Config updates avaliable");
        logger.info("Downloading Configs.");
        ZipInputStream zip = new ZipInputStream(getRemoteFile().openStream());

        ZipEntry entry = zip.getNextEntry();
        while (entry != null) {
            String currentEntry = null;
            try {
                currentEntry = entry.getName();
                if (!currentEntry.endsWith("/"))
                    logger.info("Extracting " + currentEntry);
                File destFile = new File(gameDir.getPath() + "/" + entry.getName());
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    destFile.getParentFile().mkdirs();
                    destFile.createNewFile();
                    FileWriter writer = new FileWriter(destFile);
                    while (zip.available() == 1) {
                        int read = zip.read();
                        if (read == -1) // Don't write EOF
                            break;
                        writer.write(read);
                    }
                    writer.close();
                }
            } catch (IOException e) {
                logger.error("Couldn't save " + currentEntry);
            }
            entry = zip.getNextEntry();
        }
        zip.close();
        saveVersion();
    }

    private URL getRemoteFile() throws MalformedURLException {
        String repo = UpdaterMain.getInstance().getLocalJson().getRemotePackURL().toString();
        if (file.matches("^(https?|file):\\/\\/")) {
            return new URL(file);
        }
        return new URL(repo + file);
    }

    private void saveVersion() throws IOException {
        File version = new File(new File(gameDir, "config"), "version");
        version.createNewFile();
        FileWriter save = new FileWriter(version);
        save.write(remoteVersion);
        save.close();
    }

}
