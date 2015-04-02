package mcupdater.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mcupdater.Platform;
import mcupdater.UpdaterMain;
import mcupdater.logging.LogHelper;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.libs.RemoteLibrary;
import mcupdater.update.mods.LocalMod;
import mcupdater.update.mods.RemoteMod;

import org.apache.commons.io.FileUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class Downloader {

    private static final LogHelper logger = LogHelper.getLogger();
    private final static File MC_DIR = Platform.getMinecraftHome();
    private final static File GAME_DIR = UpdaterMain.gameDir;
    private final static File MODS_DIR = new File(GAME_DIR, "mods");
    private final static File LIBRARIES_DIR = new File(MC_DIR, "libraries");
    private static final String DEFAULT_LIBRARY_URL = "https://libraries.minecraft.net/";

    private static URL repo = UpdaterMain.getInstance().getLocalJson().getRemotePackURL();

    public static void downloadMod(RemoteMod remote) throws MalformedURLException {
        downloadMod(remote, null);
    }

    public static void downloadMod(RemoteMod remote, LocalMod local) throws MalformedURLException {
        // rename old file
        if (local != null) {
            new File(local.getFile()).renameTo(new File(local.getFile() + ".old"));
        }
        // prepare for download
        URL url;
        String a = remote.getFile();
        if (a.startsWith("http")) {
            url = new URL(a);
        } else {
            url = new URL(repo + a);
        }
        String[] split = url.getPath().split("/");
        File newFile = new File(MODS_DIR, split[split.length - 1]);
        newFile.getParentFile().mkdirs();

        // download file
        try {
            if (!remote.hasHash()) {
                downloadFile(url, newFile);
            } else {
                downloadFileWithHashCheck(url, newFile, remote.getMD5(), 3);
            }
        } catch (FileNotFoundException e) {
            logger.warn(remote.getFile() + " not found!", e);
        } catch (IOException e) {
            logger.warn("Unable to read " + remote.getFile(), e);
        }

    }

    public static void downloadLibrary(RemoteLibrary library) throws MalformedURLException, IOException {
        String url = DEFAULT_LIBRARY_URL;
        if (library.getURL() != null)
            url = library.getURL();
        File file = new File(LIBRARIES_DIR, library.getRelativePath());
        File md5f = new File(LIBRARIES_DIR, library.getRelativePath() + ".md5");
        URL md5 = new URL(url + library.getRelativePathForDownload() + ".md5");
        URL u = new URL(url + library.getRelativePathForDownload());
        // TODO different hashing types
        logger.info(String.format("Downloading %s.", file.getPath()));
        try {
            downloadFile(md5, md5f);
            downloadFileWithHashCheck(u, file, FileUtils.readFileToString(md5f));
        } catch (FileNotFoundException e) {
            downloadFile(u, file);
        }

        UpdaterMain.getInstance().localLibraries.add(new LocalLibrary(file));

    }

    private static void downloadFileWithHashCheck(URL source, File destination, String hash, int tries) {
        boolean flag = false;
        int i = 0;
        while (!flag && i < tries) {
            try {
                logger.info(String.format("Downloading %s (try %s).", destination.getName(), i));
                flag = downloadFileWithHashCheck(source, destination, hash);
            } catch (IOException e) {
                logger.error("Unable to download " + destination.getName(), e);
            } finally {
                if (!flag) {
                    logger.info(String.format("%s MD5 Failed after %s tries!", destination.getName(), tries));
                    destination.delete();
                } else {
                    logger.info(destination.getName() + " MD5 Verified");
                }
                i++;
            }
        }
    }

    private static void downloadFile(URL source, File destination) throws IOException {
        URLConnection conn = source.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
        InputStream in = conn.getInputStream();

        FileUtils.copyInputStreamToFile(in, destination);
        in.close();
    }

    private static boolean downloadFileWithHashCheck(URL source, File destination, String md5sum) throws IOException {
        return downloadFileWithHashCheck(source, destination, md5sum, Hashing.md5());
    }

    private static boolean downloadFileWithHashCheck(URL source, File destination, String hash, HashFunction hashType)
            throws IOException {
        downloadFile(source, destination);
        return checkHash(destination, hash, hashType);
    }

    private static boolean checkHash(File file, String hash, HashFunction hashType) throws IOException {
        HashCode hashCode = Files.hash(file, hashType);
        return hashCode.toString().equals(hash);
    }
}
