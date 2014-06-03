package mcupdater.update;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class AbstractJson implements IModPack{

	protected final Gson gson = new Gson();
	protected final JsonObject object;
	private String mcversion;
	
	public AbstractJson(Reader json){
		object = gson.fromJson(json, JsonObject.class);
		mcversion = object.get("mcversion").getAsString();
	}
	
	public JsonObject getJsonObject(){
		return object;
	}
	
	public String getMCVersion(){
		return mcversion;
	}
	
}
