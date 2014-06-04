package mcupdater.update.libs;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import mcupdater.LibraryClassLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.collect.Lists;
/**
 * For other great adventures, check out your local library.
 */
public class LocalLibrary extends AbstractLibrary {

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
	public File getFile(){
		return file;
	}
	
	public void loadLibrary(LaunchClassLoader classLoader) throws MalformedURLException{
		LibraryClassLoader.getInstance().addLib(getFile());
	}
}
