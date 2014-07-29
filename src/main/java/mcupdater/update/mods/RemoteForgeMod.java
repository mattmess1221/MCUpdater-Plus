package mcupdater.update.mods;

import mcupdater.Side.Sides;

import com.google.gson.JsonObject;

public class RemoteForgeMod extends RemoteMod {

	private Sides side = null;

	public RemoteForgeMod(JsonObject object) {
		super(object);
		this.version = object.get("version").getAsString();
		if(object.has("side")){
			String side = object.get("side").getAsString();
			if("client".equalsIgnoreCase(side))
				this.side = Sides.CLIENT;
			else if ("server".equalsIgnoreCase(side))
				this.side = Sides.SERVER;
		}
	}
	
	@Override
	public Sides getSide(){
		return this.side;
	}

}
