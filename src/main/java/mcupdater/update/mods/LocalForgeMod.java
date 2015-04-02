package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class LocalForgeMod extends LocalMod {

    private ModList modList;

    public LocalForgeMod(File file) throws IOException {
        super(file);
        ZipFile zip = new ZipFile(file);
        // workaround for chickenbones and profmobius mods
        InputStream mcmod = null; // zip.getInputStream(zip.getEntry("mcmod.info"));
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith("mod.info")) {
                mcmod = zip.getInputStream(entry);
                break;
            }
        }
        if (mcmod == null) {
            zip.close();
            throw new IOException("mcmod.info not found!");
        }
        modList = new GsonBuilder().registerTypeAdapter(ModList.class, new InfoListAdapter()).create()
                .fromJson(new InputStreamReader(mcmod), ModList.class);
        try {

        } finally {
            mcmod.close();
            zip.close();
        }
    }

    @Override
    public String getModID() {
        return modList.getModID();
    }

    @Override
    public String getName() {
        return modList.getName();
    }

    @Override
    public String getVersion() {
        return modList.getVersion();
    }

    private interface ModList {

        String getModID();

        String getName();

        String getVersion();
    }

    private class InfoListAdapter implements JsonDeserializer<ModList> {

        @Override
        public ModList deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (element.isJsonArray())
                return ((ModInfo[]) context.deserialize(element, ModInfo[].class))[0];
            else if (element.isJsonObject()) {
                JsonElement modListVersion = element.getAsJsonObject().get("modListVersion");
                if (modListVersion.getAsInt() == 2) {
                    return context.deserialize(element, ModInfo2.class);
                }
            }
            throw new JsonIOException("Invalid mcmod.info");
        }
    }

    private class ModInfo implements ModList {

        @SerializedName("modid")
        String modID;
        String name;
        String version = "unknown";

        public String getModID() {
            return modID;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }

    private class ModInfo2 implements ModList {

        @SerializedName("modListVersion")
        int version;
        @SerializedName("modList")
        ModInfo[] mods;

        @Override
        public String getModID() {
            return mods[0].getModID();
        }

        @Override
        public String getName() {
            return mods[0].getName();
        }

        @Override
        public String getVersion() {
            return mods[0].getVersion();
        }
    }

}
