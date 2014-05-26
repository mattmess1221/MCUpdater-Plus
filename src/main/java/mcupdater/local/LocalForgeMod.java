package mcupdater.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LocalForgeMod extends LocalMod{

	public LocalForgeMod(File file) throws IOException {
		this.file = file;
		ZipFile zip = new ZipFile(file);
		InputStream mcmod = zip.getInputStream(zip.getEntry("mcmod.info"));
		JsonArray array = gson.fromJson(new InputStreamReader(mcmod), JsonArray.class);
		JsonObject object = array.get(0).getAsJsonObject();
		if (mcmod != null) {
			this.name = object.get("name").getAsString();
			this.modid = object.get("modid").getAsString();
			this.version = object.get("version").getAsString();
		}
		mcmod.close();
		zip.close();
	}

}
