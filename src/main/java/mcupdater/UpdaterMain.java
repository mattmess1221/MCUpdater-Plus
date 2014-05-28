package mcupdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mcupdater.local.LocalForgeMod;
import mcupdater.local.LocalLiteMod;
import mcupdater.local.LocalMod;
import mcupdater.remote.RemoteMod;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class UpdaterMain {

	static Logger logger = LogManager.getLogger("Updater");
	private List<LocalMod> localMods = Lists.newArrayList();
	private List<RemoteMod> remoteMods = Lists.newArrayList();
	private File gameDir;
	private String[] pack = new String[4]; // {repo, modpack, version,
											// mcVersion}
	private boolean flag;
	private static UpdaterMain instance;

	public UpdaterMain() {
		gameDir = new File(System.getProperty("user.dir"));
		instance = this;
	}

	public UpdaterMain(File file) {
		gameDir = file;
		instance = this;
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
		String urlstr = "";
		String jsonstr = "";
		try {
			urlstr = getRepo();
			URL url = new URL(urlstr + "pack.json");
			InputStream stream = url.openStream();
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line);
			jsonstr = sb.toString();
			JsonObject object = gson.fromJson(jsonstr, JsonObject.class);
			String mcversion = object.get("mcversion").getAsString();
			if (mcversion != pack[3])
				this.flag = true;
			try {
				new Config(object.get("config"), this.gameDir).updateConfigs();
			} catch (IOException e) {
				logger.error("Something went wrong while downloading configs!");
			}
			JsonArray array = object.get("mods").getAsJsonArray();
			for (JsonElement element : array) {
				JsonObject mod = element.getAsJsonObject();
				String modname = mod.get("modid").getAsString();
				String version = "";
				if(!mod.has("type") || mod.get("type").getAsString().equals("forge"))
					version = mod.get("version").getAsString();
				else if (mod.get("type").getAsString().equals("liteloader"))
					version = mod.get("revision").getAsString();
				String file = mod.get("file").getAsString();
				String md5 = null;
				if(mod.has("md5"))
					md5 = mod.get("md5").getAsString();
				this.remoteMods.add(new RemoteMod(modname, version, file, md5));
			}

		} catch (MalformedURLException e) {
			logger.error("Bad URL in modpack.json");
			e.printStackTrace();
			throw (new RuntimeException());
		} catch (IOException e) {
			logger.error(String.format("Could not open modpack definition %s",
					urlstr));
			e.printStackTrace();
			throw (new RuntimeException());
		} catch (JsonSyntaxException e) {
			logger.error(String.format("Bad JSON in %spack.json\n%s", urlstr,
					jsonstr));
			e.printStackTrace();
			throw (new RuntimeException());
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
			if (remote.getModID().equalsIgnoreCase(local.getModID())) {
				if (!local.getVersion().equalsIgnoreCase(remote.getVersion())) {
					logger.info("Updating " + local.getName() + " "
							+ local.getVersion() + " to " + remote.getVersion());
					try {
						downloadMod(remote, local);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					String version;
					if(local instanceof LocalLiteMod)
						version = ((LocalLiteMod)local).getReadableVersion();
					else version = local.getVersion();
					logger.info(local.getModID() + " " + version + " is up to date.");
				}
				return true;
			}
		}
		logger.info(remote.getModID() + " not found.");
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
			url = new URL(getRepo() + a);
		}
		File newFile = new File(new File(gameDir, "mods"), url.toString()
				.split("/")[url.toString().split("/").length - 1]);
		newFile.getParentFile().mkdirs();
		int i = 1;
		boolean flag = false;
		while (i <= 3 && !flag) {
			try {
				logger.info("Downloading " + remote.getModID() + ". (try " + i + ")");
				FileUtils.copyURLToFile(url, newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
			if(!remote.hasHash())
				break;
			try {
				if (checkMD5(newFile, remote.getMD5())) {
					flag = true;
					logger.info(remote.getModID() + " MD5 Verified");
				} else {
					newFile.delete();
					logger.info(remote.getModID() + " MD5 Failed!");
				}
			} catch (FileNotFoundException e) {
				logger.warn(remote.getFile() + " not found!");
				e.printStackTrace();
			} catch (IOException e) {
				logger.warn("Unable to read " + remote.getFile());
				e.printStackTrace();
			}
		}
	}

	private boolean checkMD5(File file, String md5sum) throws IOException {
		HashCode hashCode = Files.hash(file, Hashing.md5());
		return hashCode.toString().equals(md5sum);
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
					addMod(file);
				else if (file.getName().equals(pack[3])) {
					for (File file1 : file.listFiles()) {
						if (file1.isFile()) {
							addMod(file1);
						}
					}
				}
			} catch (IOException e) {
				logger.error("Unable to read mod file " + file.getName());
			}
	}

	private void addMod(File file) throws IOException {
		if (file.getName().endsWith(".litemod")) {
			localMods.add(new LocalLiteMod(file));
		} else if(file.getName().endsWith(".jar"))
			localMods.add(new LocalForgeMod(file));
	}

	public String getRepo() {
		return pack[0] + (pack[0].endsWith("/") ? "" : "/") + pack[1] + "/"
				+ pack[2] + "/";
	}

	public static UpdaterMain getInstance() {
		return instance;
	}
}
