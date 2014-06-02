package mcupdater.local;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mcupdater.ILibrary;
import mcupdater.LibraryClassLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.collect.Lists;

public class LocalLibrary implements ILibrary {

	private String group;
	private String name;
	private String version;
	private File file;
	
	public LocalLibrary(File lib){
		this.file = lib;
		File version = lib.getParentFile();
		File name = version.getParentFile();
		List<String> list = Lists.newArrayList();
		File g = name.getParentFile();
		while(true){
			if(g.getName().equals("libraries"))
				break;
			list.add(g.getName());
			g = g.getParentFile();
		}
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(String folder : Lists.reverse(list)){
			sb.append(prefix);
			sb.append(folder);
			prefix = ".";
		}
		this.group = sb.toString();
		this.name = name.getName();
		this.version = version.getName();
		
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

	public File getFile(){
		return file;
	}
	
	public void loadLibrary(LaunchClassLoader classLoader) throws IOException{
		LibraryClassLoader.getInstance().addLib(getFile());
		
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
