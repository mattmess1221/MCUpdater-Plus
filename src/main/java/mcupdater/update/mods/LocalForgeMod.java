package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LocalForgeMod extends LocalMod{

	public LocalForgeMod(File file) throws IOException {
		this.file = file;
		ZipFile zip = new ZipFile(file);
		InputStream mcmod = null; //zip.getInputStream(zip.getEntry("mcmod.info"));
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			if(entry.getName().endsWith("mod.info")){
				mcmod = zip.getInputStream(entry);
				break;
			}
		}
		if(mcmod == null){
			zip.close();
			throw new IOException("mcmod.info not found!");
		}
		JsonArray array = gson.fromJson(new InputStreamReader(mcmod), JsonArray.class);
		JsonObject object = array.get(0).getAsJsonObject();

		this.name = object.get("name").getAsString();
		this.modid = object.get("modid").getAsString();
		this.version = object.get("version").getAsString();
		
		mcmod.close();
		zip.close();
	}

}
