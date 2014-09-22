package mcupdater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import mcupdater.download.Downloader;
import mcupdater.update.Config;
import mcupdater.update.LocalJson;
import mcupdater.update.RemoteJson;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.mods.LocalForgeMod;
import mcupdater.update.mods.LocalLiteMod;
import mcupdater.update.mods.LocalMod;
import mcupdater.update.mods.RemoteMod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UpdaterMain {

	public static Logger logger = LogManager.getLogger("Updater");
	public UpdatableList<LocalMod> localMods = new UpdatableList<LocalMod>();
	public UpdatableList<LocalLibrary> localLibraries = new UpdatableList<LocalLibrary>();
	public static File gameDir;
	private RemoteJson remote;
	private LocalJson local;
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
			if (local.getMCVersion().equals(remote.getMCVersion()))
				compareMods();
			else
				logger.info("Local pack mcversion mismatches remote.  Not downloading mods.");
		} else {
			logger.warn("modpack.json not found!");
		}
		logger.info("Everything up to date.");

	}

	private void getInfo() {
		try {
			this.remote = this.local.getRemotePack();
			Config config = remote.getConfig();
			if(config != null)
				config.updateConfigs();
		} catch (MalformedURLException e) {
			logger.error("Bad URL in modpack.json");
			e.printStackTrace();
			throw (new RuntimeException());
		} catch (IOException e) {
			logger.error(String.format("Could not open modpack definition %s", local.getRemotePackURL()));
			e.printStackTrace();
			throw (new RuntimeException());
		} catch (JsonSyntaxException e) {
			logger.error(String.format("Bad JSON in %spack.json\n%s", local.getRemotePackURL(), new Gson().toJson(remote.getJsonObject())));
			e.printStackTrace();
			throw (new RuntimeException());
		}
	}

	private void compareMods() {
		for (RemoteMod remote : this.remote.getModsList()) {
			if(!remote.isEnabled()){
				logger.info("Skipping " + remote.getModID());
				continue;
			}
			if (!compareContainer(remote))
				try {
					Downloader.downloadMod(remote);
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
						Downloader.downloadMod(remote, local);
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

	private void readJson(File json) throws MalformedURLException {
		try {
			this.local = new LocalJson(json);
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
				else if (file.getName().equals(local.getMCVersion())) {
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
		} else if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
			localMods.add(new LocalForgeMod(file));
	}

	public static UpdaterMain getInstance() {
		if(instance == null)
			instance = new UpdaterMain();
		return instance;
	}
	
	public LocalJson getLocalJson(){
		return local;
	}
	
	public RemoteJson getRemoteJson(){
		return remote;
	}

	public void readLibraries(File file) {
		List<File> libs = getRecursiveChildren(file);
		for(File lib : libs){
			localLibraries.add(new LocalLibrary(lib));
		}
	}
	
	private List<File> getRecursiveChildren(File file){
		List<File> files = Lists.newArrayList();
		if(file.exists())
			if(file.isFile())
				files.add(file);
			else
				for(File lib : file.listFiles())
					if(lib.isDirectory())
						files.addAll(getRecursiveChildren(lib));
					else if(lib.getName().endsWith(".jar")) 
						files.add(lib);
		return files;
	}
}
