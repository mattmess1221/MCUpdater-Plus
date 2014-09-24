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
		if(logger instanceof SimpleLogger)
			throw new RuntimeException();
	}
	
	@Override
	protected void log(LogLevel level, Object object) {
		logger.log(level.getLog4jLevel(), object);
	}

	@Override
	protected void throwing(LogLevel level, String message, Throwable t) {
		logger.log(level.getLog4jLevel(), message, t);
	}

	@Override
	public void setLevel(LogLevel level) {
		super.setLevel(level);
		boolean isNull = level == null;
		try {
			Method method = logger.getClass().getMethod("setLevel", Level.class);
			method.invoke(logger, isNull ? null : level.getLog4jLevel());
		} catch (Exception e) {
			error("The current logger doesn't support setting the level", e);
		}
	}
}
