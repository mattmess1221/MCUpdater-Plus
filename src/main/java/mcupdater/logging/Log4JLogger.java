package mcupdater.logging;

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

}
