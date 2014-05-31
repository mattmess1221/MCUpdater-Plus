package mcupdater.remote;

import com.google.gson.JsonObject;

public class RemoteForgeMod extends RemoteMod {

	public RemoteForgeMod(JsonObject object) {
		super(object);
		this.version = object.get("version").getAsString();
	}

}
