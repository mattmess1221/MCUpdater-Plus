package mcupdater;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ModContainer {

	private String name;
	private String modid;
	private String version;
	private File file;

	public ModContainer(File file) {
		if (file.getName().endsWith(".jar")) {
			try {
				this.file = file;
				ZipFile zip = new ZipFile(file);
				InputStream mcmod = zip.getInputStream(zip
						.getEntry("mcmod.info"));
				if (mcmod != null) {
					Gson gson = new Gson();
					JsonArray array = gson.fromJson(
							new InputStreamReader(mcmod), JsonArray.class);
					JsonObject object = array.get(0).getAsJsonObject();
					this.name = object.get("name").getAsString();
					this.modid = object.get("modid").getAsString();
					this.version = object.get("version").getAsString();
				}
				mcmod.close();
				zip.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getName(){
		return name;
	}
	
	public String getModID(){
		return modid;
	}
	
	public String getVersion(){
		return version;
	}

	public File getFile() {
		return file;
	}
}
