package mcupdater.local;

import java.io.File;

import mcupdater.IMod;

import com.google.gson.Gson;

public abstract class LocalMod implements IMod {

	protected String name;
	protected String modid;
	protected String version;
	protected File file;
	
	protected Gson gson = new Gson();

	public String getName() {
		return name;
	}

	public String getModID() {
		return modid;
	}

	public String getVersion() {
		return version;
	}

	public String getFile() {
		return file.getPath();
	}
}
