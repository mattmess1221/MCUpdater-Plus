package mcupdater.update.mods;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

public class LocalForgeMod extends LocalMod {

    public LocalForgeMod(File file) throws IOException {
        this.file = file;
        ZipFile zip = new ZipFile(file);
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
        JsonElement element = gson.fromJson(new InputStreamReader(mcmod), JsonElement.class);
        try {
            JsonArray array = null;
            if (element.isJsonArray())
                array = element.getAsJsonArray();
            else if (element.isJsonObject()) {
                JsonElement modListVersion = element.getAsJsonObject().get("modListVersion");
                if (modListVersion.getAsInt() == 2) {
                    array = element.getAsJsonObject().get("modList").getAsJsonArray();
                }
            } else
                throw new JsonIOException("Invalid mcmod.info");
            JsonObject object = array.get(0).getAsJsonObject();
            try {
                this.name = object.get("name").getAsString();
                this.modid = object.get("modid").getAsString();
                this.version = object.get("version").getAsString();
            } catch (NullPointerException e) {
                throw new JsonIOException("Missing required elements", e);
            }
        } finally {
            mcmod.close();
            zip.close();
        }
    }

}
