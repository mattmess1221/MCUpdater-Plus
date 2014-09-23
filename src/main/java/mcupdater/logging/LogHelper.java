package mcupdater.logging;

import org.apache.logging.log4j.Level;


public abstract class LogHelper {

	protected static final String id = "MCUpdater Plus";
	private static LogHelper instance = null;
	
	public static LogHelper getLogger() {
		if(instance == null){
			instance = null;
			try{
				instance = new Log4JLogger();
			}catch(Throwable t){
				// In case log4j isn't loaded.
				instance = new JavaLogger();
				instance.warn("Unable to load Log4J, falling back to Java Logging.");
			}
		}
		return instance;
	}
	
	protected abstract void log(LogLevel level, Object object);

	protected abstract void throwing(LogLevel level, String message, Throwable throwable);
	
	public void off(Object object) {
		this.log(LogLevel.OFF, object);
	}
	
	public void off(String message, Throwable throwable) {
		this.throwing(LogLevel.OFF, message, throwable);
	}
	
	public void fatal(Object object) {
		this.log(LogLevel.FATAL, object);
	}
	
	public void fatal(String message, Throwable throwable) {
		this.throwing(LogLevel.FATAL, message, throwable);
	}
	
	public void error(Object object) {
		this.log(LogLevel.ERROR, object);
	}
	
	public void error(String message, Throwable throwable) {
		this.throwing(LogLevel.ERROR, message, throwable);
	}
	
	public void warn(Object object) {
		this.log(LogLevel.WARN, object);
	}
	
	public void warn(String message, Throwable throwable) {
		this.throwing(LogLevel.WARN, message, throwable);
	}
	
	public void info(Object object) {
		this.log(LogLevel.INFO, object);
	}
	
	public void info(String message, Throwable throwable) {
		this.throwing(LogLevel.INFO, message, throwable);
	}
	
	public void debug(Object object) {
		this.log(LogLevel.DEBUG, object);
	}
	
	public void debug(String message, Throwable throwable) {
		this.throwing(LogLevel.DEBUG, message, throwable);
	}

	public void trace(Object object) {
		this.log(LogLevel.TRACE, object);
	}
	
	public void trace(String message, Throwable throwable) {
		this.throwing(LogLevel.TRACE, message, throwable);
	}
	
	public void all(Object object) {
		this.log(LogLevel.ALL, object);
	}
	
	public void all(String message, Throwable throwable) {
		this.throwing(LogLevel.ALL, message, throwable);
	}
	
	protected static enum LogLevel {
		
		OFF,
		FATAL,
		ERROR,
		WARN,
		INFO,
		DEBUG,
		TRACE, 
		ALL
		;
		
		public Level getLog4jLevel() {
			return Level.valueOf(name());
		}
	}

    public void setLog4jLevel(Level level){
        if(instance instanceof Log4JLogger){
            ((Log4JLogger) instance).setLevel(level);
        }else{
             instance.log(LogLevel.ERROR, "Could not set the log-level, the current logger does not support it!");
        }
    }
}
