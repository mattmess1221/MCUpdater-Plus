package mcupdater;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class LibraryClassLoader {

	private LaunchClassLoader classLoader;
	private static LibraryClassLoader instance;
	
	private LibraryClassLoader() {
		this.classLoader = Launch.classLoader;
		instance = this;
	}

	public void addLib(File modFile) throws MalformedURLException {
		URL url = modFile.toURI().normalize().toURL();
		try{
			UpdaterMain.logger.info(String.format("Loading library %s.", modFile.getPath()));
			URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class}); 
			addURL.setAccessible(true);
			addURL.invoke(classLoader, url);
		}catch(Exception e){
			UpdaterMain.logger.error(String.format("Failed to load %s into classpath.", modFile.getPath()), e);
		} finally {
			classLoader.addURL(url);
		}
	}
	
	public static LibraryClassLoader getInstance(){
		if(instance == null)
			instance = new LibraryClassLoader();
		return instance;
	}
	
}
