package mcupdater;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mcupdater.local.LocalLibrary;
import mcupdater.remote.RemoteLibrary;
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
					remote.download();
				} catch (IOException e) {
					UpdaterMain.logger.error(String.format("Failed to download %s.", remote.getName()));
					e.printStackTrace();
				}
			
			for(LocalLibrary local : mcup.localLibraries){
				if(remote.equals(local)){
					try {
						System.out.println(local.getName() + " loaded");
						local.loadLibrary(classLoader);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		for(String tweak : mcup.getRemoteJson().tweaks){
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
