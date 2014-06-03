package mcupdater.update.mods;

import mcupdater.update.IUpdatable;


public interface IMod extends IUpdatable {

	public String getModID();
	
	public String getVersion();
	
	public String getFile();
	
}
