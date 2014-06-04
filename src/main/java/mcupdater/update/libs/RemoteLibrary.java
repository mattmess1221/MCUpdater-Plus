package mcupdater.update.libs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import mcupdater.Platform;

import com.google.gson.JsonObject;

public class RemoteLibrary extends AbstractLibrary{

	private String url;


	public RemoteLibrary(JsonObject obj){
		this.name = obj.get("name").getAsString();
		this.version = obj.get("version").getAsString();
		this.group = obj.get("group").getAsString();
        if(obj.has("classifier"))
            this.classifier = obj.get("classifier").getAsString();
		if(obj.has("url"))
			this.url = obj.get("url").getAsString();
	}


	public String getURL() {
		if(url == null)
			return null;
		return url + (url.endsWith("/") ? "" : "/" );
	}
	
	public boolean installed(){
		return getLocalFile().exists();
	}

	public String getRelativePath(){
		return group.replace('.', '/') + "/" + name + "/" + version + "/" + name + "-" + version + (classifier != null ? "-" + classifier : "") + ".jar";
	}
	
	private File getLocalFile(){
		return new File(new File(Platform.getMinecraftHome(), "libraries"), getRelativePath());
	}
	
	public URL getRemoteURL() throws MalformedURLException{
		return new URL(getURL() + getRelativePath());
	}
}
