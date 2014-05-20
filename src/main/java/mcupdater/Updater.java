package mcupdater;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Updater implements ITweaker {
	private Map<String,String> launchArgs = Maps.newHashMap();
    private List<String> standaloneArgs;
    private File gameDirectory;

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir,
			String profile) {

        gameDirectory = gameDir == null ? new File(".") : gameDir;

        if(!Launch.blackboard.containsKey("launchArgs")){
            launchArgs = Maps.newHashMap();
            Launch.blackboard.put("launchArgs",launchArgs);
        }else{
            if(Launch.blackboard.get("launchArgs") instanceof Map){
                launchArgs = (Map) Launch.blackboard.get("launchArgs");
            }else{
                throw(new RuntimeException());
            }
        }

        standaloneArgs = args;

        if(!this.launchArgs.containsKey("--version"))
            this.launchArgs.put("--version","version");

        if(!this.launchArgs.containsKey("--gameDir"))
            this.launchArgs.put("--gameDir",gameDirectory.getAbsolutePath());

        if(!this.launchArgs.containsKey("--assetsDir"))
            this.launchArgs.put("--assetsDir",assetsDir.getAbsolutePath());


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
        List<String> args = Lists.newArrayList();
        args.addAll(this.standaloneArgs);
         for(Map.Entry<String,String> arg : launchArgs.entrySet()){
            args.add(arg.getKey());
            args.add(arg.getValue());
         }
        this.launchArgs.clear();
        return args.toArray(new String[args.size()]);
	}

}
