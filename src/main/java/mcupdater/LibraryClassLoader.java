package mcupdater;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class LibraryClassLoader extends URLClassLoader {

	private LaunchClassLoader classLoader;
	private static LibraryClassLoader instance;
	
	private LibraryClassLoader() {
		super(new URL[0], Launch.classLoader);
		this.classLoader = Launch.classLoader;
		instance = this;
	}

	public void addLib(File modFile) throws MalformedURLException {
		URL url = modFile.toURI().normalize().toURL();
		classLoader.addURL(url);
	}
		
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return classLoader.loadClass(name);
	}

	public static LibraryClassLoader getInstance(){
		if(instance == null)
			instance = new LibraryClassLoader();
		return instance;
	}
	
}
