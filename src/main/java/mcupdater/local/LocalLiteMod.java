package mcupdater.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;

import com.google.gson.JsonObject;

public class LocalLiteMod extends LocalMod {

	private String revision;
	
	public LocalLiteMod(File file) throws IOException {
		this.file = file;
		ZipFile litemod = new ZipFile(file);
		InputStream json = litemod.getInputStream(litemod.getEntry("litemod.json"));
		JsonObject object = gson.fromJson(new InputStreamReader(json), JsonObject.class);
		if (json != null) {
			this.name = object.get("name").getAsString();
			this.modid = object.get("name").getAsString();
			this.version = object.get("version").getAsString();
			this.revision = object.get("revision").getAsString();
		}
		json.close();
		litemod.close();
	}
	
	@Override
	public String getVersion(){
		return revision;
	}
	
	public String getReadableVersion(){
		return version;
	}

}
