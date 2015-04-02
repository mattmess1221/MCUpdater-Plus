package mcupdater.logging;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;

public class Log4JLogger extends LogHelper {

    private Logger logger;

    Log4JLogger() {
        logger = LogManager.getLogger("MCUpdater Plus");
        if (logger instanceof SimpleLogger)
            throw new RuntimeException();
    }

    private static Level getLevel(LogLevel level) {
        return Level.valueOf(level.name());
    }

    @Override
    protected void log(LogLevel level, Object object) {
        logger.log(getLevel(level), object);
    }

    @Override
    protected void throwing(LogLevel level, String message, Throwable t) {
        logger.log(getLevel(level), message, t);
    }

    @Override
    public void setLevel(LogLevel level) {
        super.setLevel(level);
        boolean isNull = level == null;
        try {
            Method method = logger.getClass().getMethod("setLevel", Level.class);
            method.invoke(logger, isNull ? null : getLevel(level));
        } catch (Exception e) {
            error("The current logger doesn't support setting the level", e);
        }
    }
}
