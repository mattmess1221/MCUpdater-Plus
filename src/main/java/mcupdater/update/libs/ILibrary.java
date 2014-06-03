package mcupdater.update.libs;

import mcupdater.update.IUpdatable;


public interface ILibrary extends IUpdatable {

	public String getGroup();
	
	public String getName();
	
	public String getVersion();
	
}
