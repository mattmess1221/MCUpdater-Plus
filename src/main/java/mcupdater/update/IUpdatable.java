package mcupdater.update;

public interface IUpdatable {

	public String getName();
	
	public String getVersion();
	
	public boolean equals(IUpdatable updatable);
}
