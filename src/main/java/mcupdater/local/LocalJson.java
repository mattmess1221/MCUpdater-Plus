package mcupdater.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import mcupdater.AbstractJson;
import mcupdater.remote.RemoteJson;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class LocalJson extends AbstractJson{

	private String modpack;
	private String version;
	private String repo;
	private List<String> disabled = Lists.newArrayList();
	
	
	public LocalJson(Reader json) throws NullPointerException{
		super(json);
		if(object.has("disabled")){
			JsonArray array = object.get("disabled").getAsJsonArray();
			for(JsonElement ele : array){
				disabled.add(ele.getAsString());
			}
		}
		modpack = object.get("modpack").getAsString();
		version = object.get("version").getAsString();
		repo = object.get("repo").getAsString();
		
		
	}
	
	public LocalJson(File file) throws NullPointerException, FileNotFoundException{
		this(new FileReader(file));
	}
	
	public String getModpackName(){
		return modpack;
	}
	
	public String getModpackVersion(){
		return version;
	}
	
	public String getRepo(){
		return repo;
	}
	
	public URL getRemotePackURL(){
		try {
			return new URL(repo + (repo.endsWith("/") ? "" : "/" ) + modpack + "/" + version + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isDisabled(String modid){
		for(String mod : disabled)
			if(mod.equals(modid))
				return true;
		return false;
	}
	
	private URL getRemoteJson() throws MalformedURLException{
		String s = getRemotePackURL().toString();
		return new URL(s + "pack.json");
	}
	
	public RemoteJson getRemotePack() throws IOException{
		InputStreamReader reader = new InputStreamReader(getRemoteJson().openStream());
		return new RemoteJson(reader);
	}
	
}
