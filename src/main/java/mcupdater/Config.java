package mcupdater;

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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Config {

	private File gameDir;
	private URL remoteFile;
	private String remoteVersion = "";
	private String localVersion = "";
	private boolean shouldUpdate;

	/**
	 * 
	 * @param jsonElement
	 *            the config object. Check for nulls here.
	 * @param gameDir
	 *            the Game Directory
	 * @throws IOException
	 */
	public Config(JsonElement jsonElement) throws IOException {
		this.gameDir = UpdaterMain.gameDir;
		localVersion = getLocalVersion(gameDir);
		remoteVersion = getRemoteVersion(getJsonObject(jsonElement));
		remoteFile = getRemoteFile(getJsonObject(jsonElement), UpdaterMain.getInstance().local.getRemotePackURL().toString());
		shouldUpdate = !localVersion.equals(remoteVersion);
		if (shouldUpdate) {
			UpdaterMain.logger.info("Config updates avaliable");
		}
	}

	private String getLocalVersion(File gameDir) throws IOException {
		File version = new File(new File(gameDir, "config"), "version");
		if (!version.exists())
			return "";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(version)));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line);
		br.close();
		return sb.toString();
	}

	private String getRemoteVersion(JsonObject config) {
		if (config == null)
			return null;
		return config.get("version").getAsString();
	}

	private URL getRemoteFile(JsonObject object, String repo) throws MalformedURLException {
		if (object == null)
			return null;
		String url = object.get("file").getAsString();
		if (url.startsWith("http")) {
			return new URL(url);
		}
		return new URL(repo + url);
	}

	private JsonObject getJsonObject(JsonElement jsonElement) {
		if (jsonElement != null)
			return jsonElement.getAsJsonObject();
		return null;
	}

	public void updateConfigs() throws IOException {
		if (!shouldUpdate){
			UpdaterMain.logger.info("Configs up to date.");
			return;
		}
		UpdaterMain.logger.info("Downloading Configs.");
		ZipInputStream zip = new ZipInputStream(remoteFile.openStream());
		
		ZipEntry entry = zip.getNextEntry();
		while (entry != null) {
			String currentEntry = null;
			try {
				currentEntry = entry.getName();
				if(!currentEntry.endsWith("/"))
				UpdaterMain.logger.info("Extracting " + currentEntry);
				File destFile = new File(gameDir.getPath() + "/" + entry.getName());
				if (entry.isDirectory()) {
					destFile.mkdirs();
				} else {
					destFile.getParentFile().mkdirs();
					destFile.createNewFile();
					FileWriter writer = new FileWriter(destFile);
					while (zip.available() == 1) {
						int read = zip.read();
						if(read == -1) // Don't write EOF
							break;
						writer.write(read);
					}
					writer.close();
				}
			} catch (IOException e) {
				UpdaterMain.logger.error("Couldn't save " + currentEntry);
			}
			entry = zip.getNextEntry();
		}
		zip.close();
		saveVersion();
	}

	private void saveVersion() throws IOException {
		File version = new File(new File(gameDir, "config"), "version");
		version.createNewFile();
		FileWriter save = new FileWriter(version);
		save.write(remoteVersion);
		save.close();
	}

}
