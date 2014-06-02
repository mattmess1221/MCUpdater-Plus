package mcupdater.remote;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mcupdater.ILibrary;
import mcupdater.Platform;
import mcupdater.UpdaterMain;
import mcupdater.local.LocalLibrary;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;

public class RemoteLibrary implements ILibrary {

	private String group;
	private String name;
	private String version;
	private String url;
	
	public RemoteLibrary(JsonObject obj){
		this.name = obj.get("name").getAsString();
		this.version = obj.get("version").getAsString();
		this.group = obj.get("group").getAsString();
		if(obj.has("url"))
			this.url = obj.get("url").getAsString();
		else
			this.url = "http://libraries.minecraft.net/";
	}
	
	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public String getURL() {
		
		return url + (url.endsWith("/") ? "" : "/" );
	}
	
	public boolean installed(){
		return getLocalFile().exists();
	}

	public String getRelativePath(){
		return group.replace('.', '/') + "/" + name + "/" + version + "/" + name + "-" + version + ".jar";
	}
	
	public File getLocalFile(){
		return new File(new File(Platform.getMinecraftHome(), "libraries"), getRelativePath());
	}
	
	public URL getRemoteURL() throws MalformedURLException{
		return new URL(getURL() + getRelativePath());
	}
	
	public void download() throws IOException{
		URL remote = getRemoteURL();
		File local = getLocalFile();
		URLConnection conn = remote.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
		InputStream in = conn.getInputStream();
		FileUtils.copyInputStreamToFile(in, local);
		UpdaterMain.getInstance().localLibraries.add(new LocalLibrary(local));
	}
	
	public boolean equals(ILibrary library){
		if(library == null)
			return false;
		if(library.getName().equals(this.getName())
				&& library.getGroup().equals(this.getGroup())
				&& library.getVersion().equals(this.getVersion())){
			return true;
		}
		return false;
	}
}
