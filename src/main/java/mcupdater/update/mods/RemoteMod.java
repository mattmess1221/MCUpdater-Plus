package mcupdater.update.mods;

import mcupdater.Side.Sides;
import mcupdater.UpdaterMain;

import com.google.gson.JsonObject;


public abstract class RemoteMod extends AbstractMod{

	protected String version;
	private String url;
	private String md5;
	private boolean enabled;
	private UpdaterMain mcup = UpdaterMain.getInstance();
	
	public RemoteMod(JsonObject object){
		
			this.modid = object.get("modid").getAsString();
			this.url = object.get("file").getAsString();
			this.md5 = object.get("md5").getAsString();
			
			enabled = !mcup.getLocalJson().isModDisabled(modid);
	}
	
	
	@Override
	public String getName(){
		return this.getModID();
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getFile() {
		return this.url;
	}
	
	public String getMD5(){
		return this.md5;
	}
	
	public boolean hasHash(){
		return md5 != null;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public Sides getSide(){
		return null;
	}
	
}
