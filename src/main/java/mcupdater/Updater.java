package mcupdater;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class Updater implements ITweaker {

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir,
			String profile) {

        new UpdaterMain(gameDir).main(args.toArray(new String[args.size()]));
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public String[] getLaunchArguments() {
        return new String[0];
	}

}
