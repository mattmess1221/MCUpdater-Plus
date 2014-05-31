package mcupdater.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import mcupdater.IMod;
import mcupdater.UpdaterMain;
import mcupdater.local.LocalMod;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.gson.JsonObject;


public abstract class RemoteMod implements IMod{

	private String modid;
	protected String version;
	private String url;
	private String md5;
	private URL repo;
	
	public RemoteMod(JsonObject object){
		
			this.modid = object.get("modid").getAsString();
			this.url = object.get("file").getAsString();
			this.md5 = object.get("md5").getAsString();
			this.repo = UpdaterMain.getInstance().local.getRemotePackURL();
	}
	
	@Override
	public String getModID() {
		return this.modid;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getFile() {
		return this.url;
	}
	
	public String getMD5(){
		return this.md5;
	}
	
	public boolean hasHash(){
		return md5 != null;
	}
	
	public void downloadMod(LocalMod local)
			throws MalformedURLException {
		// rename old file
		if (local != null) {
			new File(local.getFile()).renameTo(new File(local.getFile()
					+ ".old"));
		}
		// download new file
		URL url;
		String a = getFile(); 
		if (a.startsWith("http")) {
			url = new URL(a);
		} else {
			url = new URL(repo + a);
		}
		File newFile = new File(new File(UpdaterMain.gameDir, "mods"), url.toString()
				.split("/")[url.toString().split("/").length - 1]);
		newFile.getParentFile().mkdirs();
		int i = 1;
		boolean flag = false;
		while (i <= 3 && !flag) {
			try {
				UpdaterMain.logger.info("Downloading " + getModID() + ". (try " + i + ")");
				FileUtils.copyURLToFile(url, newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
			if(!hasHash())
				break;
			try {
				if (checkMD5(newFile, getMD5())) {
					flag = true;
					UpdaterMain.logger.info(getModID() + " MD5 Verified");
				} else {
					newFile.delete();
					UpdaterMain.logger.info(getModID() + " MD5 Failed!");
				}
			} catch (FileNotFoundException e) {
				UpdaterMain.logger.warn(getFile() + " not found!");
				e.printStackTrace();
			} catch (IOException e) {
				UpdaterMain.logger.warn("Unable to read " + getFile());
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkMD5(File file, String md5sum) throws IOException {
		HashCode hashCode = Files.hash(file, Hashing.md5());
		return hashCode.toString().equals(md5sum);
	}
}
