package mcupdater.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Log4JLogger extends LogHelper {

	private Logger logger;

	Log4JLogger() {
		logger = LogManager.getLogger("MCUpdater Plus");
	}
	
	@Override
	protected void log(LogLevel level, Object object) {
		logger.log(level.getLog4jLevel(), object);
	}

	@Override
	protected void throwing(LogLevel level, String message, Throwable t) {
		logger.log(level.getLog4jLevel(), message, t);
	}

    public void setLevel(Level level) {
        Class loggerclass = logger.getClass();
        List<Method> methodsList = Arrays.asList(loggerclass.getDeclaredMethods());
        Method setLevelMethod = null;
        for(Method method : methodsList){
            if(method.getName().equals("setLevel") && Arrays.equals(method.getParameterTypes(), new Class[] {Level.class})){
                setLevelMethod = method;
                break;
            }
        }
        if(setLevelMethod == null){
            logger.error("The current logger does not support setting the level!");
        }else {
            try {
                setLevelMethod.invoke(logger, level);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
