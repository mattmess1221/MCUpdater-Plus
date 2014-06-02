package mcupdater;


public interface ILibrary {

	public String getGroup();
	
	public String getName();
	
	public String getVersion();
	
	public boolean equals(ILibrary library);
	
}
