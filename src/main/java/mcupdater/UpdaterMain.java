package mcupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UpdaterMain {

	static Logger logger = LogManager.getLogger("Updater");
	private List<ModContainer> modContainers = Lists.newArrayList();
	private URL url;
	private File gameDir;
	private String mcVersion;
	private String modpack;
	private String packVersion;
	private List<String> mods = Lists.newArrayList();
	private Map<String, String> urls = Maps.newHashMap();
	private Map<String, String> versions = Maps.newHashMap();
	private Map<String, String> md5s = Maps.newHashMap();
	private boolean flag;

	public UpdaterMain() {
		gameDir = new File(System.getProperty("user.dir"));
	}

	public UpdaterMain(File file) {
		gameDir = file;
	}

	public void main(String[] args) {
		logger.info("Starting Updater");
		File modpack = new File(gameDir, "modpack.json");
		if (modpack.exists()) {
			try {
				readJson(modpack);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			readMods(new File(gameDir, "mods"));
			getInfo();
			if (flag)
				compareMods();
			else
				logger.info("Local pack mcversion mismatches remote.  Not downloading mods.");
		} else {
			logger.warn("modpack.json not found!");
		}

	}

	private void getInfo() {
		try {
			String urlstr = this.url.toString() + "/" + modpack + "/"
					+ packVersion + "/";
			URL url = new URL(urlstr + "pack.json");
			InputStream stream = url.openStream();
			Gson gson = new Gson();
			JsonObject object = gson.fromJson(new InputStreamReader(stream),
					JsonObject.class);
			String mcversion = object.get("mcversion").getAsString();
			if (mcversion != this.mcVersion)
				this.flag = true;
			JsonArray array = object.get("mods").getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject mod = element.getAsJsonObject();
				String modname = mod.get("modid").getAsString();
				this.mods.add(modname);
				this.urls.put(modname, mod.get("file").getAsString());
				this.versions.put(modname, mod.get("version").getAsString());
				this.md5s.put(modname, mod.get("md5").getAsString());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compareMods() {
		for (String modid : mods) {
			if (!compareContainer(modid))
				try {
					downloadMod(modid, null);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
		}
	}

	private boolean compareContainer(String modid) {
		for (ModContainer mod : modContainers) {
			if (modid.equalsIgnoreCase(mod.getModID())) {
				if (!mod.getVersion().equalsIgnoreCase(versions.get(modid))) {
					logger.info("Updating " + mod.getName() + " " + mod.getVersion() + " to " + versions.get(modid));
					try {
						downloadMod(modid, mod.getFile());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					logger.info(mod.getModID() + " " + mod.getVersion() + " is up to date.");
				}
				return true;
			}
		}
		logger.info(modid + " not found.");
		return false;
	}

	private void downloadMod(String modid, File file)
			throws MalformedURLException {
		// rename old file
		if (file != null) {
			file.renameTo(new File(file.toString() + ".old"));
		}
		// download new file
		URL url;
		String a = urls.get(modid);
		if (a.startsWith("http")) {
			url = new URL(a);
		} else {
			url = new URL(this.url.toString() + "/" + modpack + "/"
					+ packVersion + "/" + a);
		}
		File newFile = new File(new File(gameDir, "mods"), url.toString()
				.split("/")[url.toString().split("/").length - 1]);
		newFile.getParentFile().mkdirs();
		int i = 1;
		boolean flag = false;
		while (i <= 3 && !flag) {
			try {
				logger.info("Downloading " + modid + ". (try " + i + ")");
				FileUtils.copyURLToFile(url, newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
			if (checkMD5(newFile, md5s.get(modid))) {
				flag = true;
				logger.info(modid + " MD5 Verified");
			} else {
				logger.info(modid + " MD5 Failed! Retrying...");
			}
		}
	}

	private boolean checkMD5(File newFile, String md5sum) {

		return true;

	}

	private void readJson(File json) throws MalformedURLException {
		try {
			Gson gson = new Gson();
			InputStream is = new FileInputStream(json);
			JsonObject object = gson.fromJson(new InputStreamReader(is),
					JsonObject.class);

			this.modpack = object.get("modpack").getAsString();
			this.packVersion = object.get("version").getAsString();
			this.url = new URL(object.get("repo").getAsString());
			this.mcVersion = object.get("mcversion").getAsString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void readMods(File modsDir) {
		for (File file : modsDir.listFiles())
			if (file.isFile())
				modContainers.add(new ModContainer(file));
	}

}
