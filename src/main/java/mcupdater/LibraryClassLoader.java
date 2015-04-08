package mcupdater;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import mcupdater.logging.LogHelper;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class LibraryClassLoader {

    private static final LogHelper logger = LogHelper.getLogger();

    private static LibraryClassLoader instance;
    private static Method ADDURL;

    private LibraryClassLoader() {}

    public void addLib(File file, LaunchClassLoader classLoader) {
        try {
            logger.info(String.format("Loading library %s.", file.getPath()));
            if (ADDURL == null) {
                ADDURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                ADDURL.setAccessible(true);
            }
            ADDURL.invoke(classLoader.getClass().getClassLoader(), file.toURI().toURL());
            classLoader.addURL(file.toURI().toURL());
        } catch (Exception e) {
            logger.error(String.format("Failed to load %s into classpath.", file.getPath()), e);
        }
    }

    public static LibraryClassLoader getInstance() {
        if (instance == null)
            instance = new LibraryClassLoader();
        return instance;
    }

}
