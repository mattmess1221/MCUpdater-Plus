package mcupdater;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class ServerMain {

    //TODO add an argument for this
    private static String mcjarprefix = "minecraft_server";


	public static void main(final String[] args) throws Exception {
		if(new ServerMain().loadMCJar()){
			new UpdaterMain().main(args);
		}
		else {
			System.out.println("Warning: Minecraft Server not found in user dir.  Unable to update!");
		}
	}

	private boolean loadMCJar() throws IOException{
		boolean mcfound = false;
		System.out.println("Loading Libraries...");
		for(File file : new File(System.getProperty("user.dir")).listFiles()){
			if(file.getName().startsWith(mcjarprefix) && file.getName().endsWith(".jar")){
				addURL(file.toURI().toURL());
				mcfound = true;
			} else if (file.isDirectory() && file.getName().equals("libraries")){
				recursiveLookup(file);
			}
		}
		return mcfound;
	}
	
	private void recursiveLookup(File file) throws MalformedURLException{
		for(File file1 : file.listFiles()){
			if(file1.isDirectory())
				recursiveLookup(file1);
			else
				addURL(file1.toURI().toURL());
		}
	}
	
	private void addURL(URL u){
		URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> clClass = URLClassLoader.class;
		try {
			Method method = clClass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, u);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
