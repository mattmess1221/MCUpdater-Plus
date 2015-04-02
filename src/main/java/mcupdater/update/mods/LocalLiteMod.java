package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class LocalLiteMod extends LocalMod {

    private LiteModJson json;

    public LocalLiteMod(File file) throws IOException {
        super(file);

        ZipFile litemod = null;
        InputStream in = null;
        Reader reader = null;

        try {
            litemod = new ZipFile(file);
            in = litemod.getInputStream(litemod.getEntry("litemod.json"));
            reader = new InputStreamReader(in);
            json = new Gson().fromJson(reader, LiteModJson.class);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(litemod);
        }
    }

    @Override
    public String getModID() {
        return json.name;
    }

    @Override
    public String getName() {
        return json.name;
    }

    @Override
    public String getVersion() {
        return json.revision;
    }

    public String getReadableVersion() {
        return json.version;
    }

    private class LiteModJson {

        private String name;
        private String version;
        private String revision;
    }
}
