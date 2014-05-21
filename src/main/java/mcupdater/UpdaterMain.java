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

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UpdaterMain {

	static Logger logger = LogManager.getLogger("Updater");
	private List<LocalMod> localMods = Lists.newArrayList();
	private List<RemoteMod> remoteMods = Lists.newArrayList();
	private File gameDir;
	private String[] pack = new String[4]; // {repo, modpack, version, mcVersion}
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
			String urlstr = pack[0] + "/" + pack[1] + "/" + pack[2] + "/";
			URL url = new URL(urlstr + "pack.json");
			InputStream stream = url.openStream();
			Gson gson = new Gson();
			JsonObject object = gson.fromJson(new InputStreamReader(stream),
					JsonObject.class);
			String mcversion = object.get("mcversion").getAsString();
			if (mcversion != pack[3])
				this.flag = true;
			JsonArray array = object.get("mods").getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject mod = element.getAsJsonObject();
				String modname = mod.get("modid").getAsString();
				String version = mod.get("version").getAsString();
				String file = mod.get("file").getAsString();
				String md5 = mod.get("md5").getAsString();
				this.remoteMods.add(new RemoteMod(modname, version, file, md5));
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compareMods() {
		for (RemoteMod remote : remoteMods) {
			if (!compareContainer(remote))
				try {
					downloadMod(remote, null);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
		}
	}

	private boolean compareContainer(RemoteMod remote) {
		for (LocalMod local : localMods) {
			if (remote.getModID().equals(local.getModID())) {
				if (!local.getVersion().equalsIgnoreCase(remote.getVersion())) {
					logger.info("Updating " + local.getName() + " "
							+ local.getVersion() + " to " + remote.getVersion());
					try {
						downloadMod(remote, local);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					logger.info(local.getModID() + " " + local.getVersion()
							+ " is up to date.");
				}
				return true;
			}
		}
		logger.info(remote + " not found.");
		return false;
	}

	private void downloadMod(RemoteMod remote, LocalMod local)
			throws MalformedURLException {
		// rename old file
		if (local != null) {
			new File(local.getFile()).renameTo(new File(local.getFile()
					+ ".old"));
		}
		// download new file
		URL url;
		String a = remote.getFile();
		if (a.startsWith("http")) {
			url = new URL(a);
		} else {
			url = new URL(pack[0] + (pack[0].endsWith("/") ? "" : "/")
					+ pack[1] + "/" + pack[2] + "/" + a);
		}
		File newFile = new File(new File(gameDir, "mods"), url.toString()
				.split("/")[url.toString().split("/").length - 1]);
		newFile.getParentFile().mkdirs();
		int i = 1;
		boolean flag = false;
		while (i <= 3 && !flag) {
			try {
				logger.info("Downloading " + remote + ". (try " + i + ")");
				FileUtils.copyURLToFile(url, newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
			if (checkMD5(newFile, remote.getMD5())) {
				flag = true;
				logger.info(remote + " MD5 Verified");
			} else {
				logger.info(remote + " MD5 Failed! Retrying...");
			}
		}
	}

	private boolean checkMD5(File newFile, String md5sum) {
		// TODO return true for now
		return true;

	}

	private void readJson(File json) throws MalformedURLException {
		try {
			Gson gson = new Gson();
			InputStream is = new FileInputStream(json);
			JsonObject object = gson.fromJson(new InputStreamReader(is),
					JsonObject.class);

			this.pack[0] = object.get("repo").getAsString();
			this.pack[1] = object.get("modpack").getAsString();
			this.pack[2] = object.get("version").getAsString();
			this.pack[3] = object.get("mcversion").getAsString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void readMods(File modsDir) {
		modsDir.mkdirs();
		for (File file : modsDir.listFiles())
			try {
				if (file.isFile())
					localMods.add(new LocalMod(file));
				else if(file.getName().equals(pack[3])){
					for (File file1 : file.listFiles()) {
						if (file1.isFile()) {
							localMods.add(new LocalMod(file1));
						}
					}
				}
			} catch (IOException e) {

			}
	}

}
