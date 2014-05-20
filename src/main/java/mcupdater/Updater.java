package mcupdater;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.collect.Lists;

public class Updater implements ITweaker {
	private List<String> args = Lists.newArrayList();

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir,
			String profile) {
		//this.gameDir = gameDir;
		this.args.addAll(args);
		this.args.add("--gameDir");
		this.args.add(gameDir.toString());
		this.args.add("--assetsDir");
		this.args.add(assetsDir.toString());
		this.args.add("--profile");
		this.args.add(profile);

		new UpdaterMain(gameDir).main(args.toArray(new String[0]));
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
		return new String[] {};
	}

}
