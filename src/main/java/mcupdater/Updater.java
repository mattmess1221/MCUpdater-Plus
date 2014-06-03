package mcupdater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import mcupdater.download.Downloader;
import mcupdater.update.libs.LocalLibrary;
import mcupdater.update.libs.RemoteLibrary;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class Updater implements ITweaker {

	private UpdaterMain mcup;
	
	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir,
			String profile) {
		
		mcup = new UpdaterMain(gameDir);
		mcup.main(args.toArray(new String[args.size()]));
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		mcup.readLibraries(new File(Platform.getMinecraftHome(), "libraries"));
		for(RemoteLibrary remote : mcup.getRemoteJson().getLibrariesList()){
			if(!remote.installed())
				try {
					Downloader.downloadLibrary(remote);
				} catch (IOException e) {
					UpdaterMain.logger.error(String.format("Failed to download %s.", remote.getName()));
					e.printStackTrace();
				}
			
			LocalLibrary local = mcup.localLibraries.get(remote);
			//if(local != null)
			try {
				local.loadLibrary(classLoader);
			} catch (MalformedURLException e) {
				UpdaterMain.logger.error(String.format("Failed to load library: %s.", local.getFile().getPath()), e);
			}
		}
		
		// Add cascaded tweaks
		for(String tweak : mcup.getRemoteJson().tweaks){
			if(!tweak.contains("liteloader"))
			registerTweak(tweak);
		}
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}

	@SuppressWarnings("unchecked")
	private void registerTweak(String tweak){
		List<String> tweaks = (List<String>) Launch.blackboard.get("TweakClasses");
		tweaks.add(tweak);
	}
}
