package mcupdater.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JavaLogger extends LogHelper {

	private final DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final Calendar calendar = Calendar.getInstance();
	private PrintStream fileLogger = null;
	
	public JavaLogger() {
		try {
			this.fileLogger = new PrintStream(new File("mcupdater-plus.log"));
		} catch (FileNotFoundException e) {
			this.warn("Log file cannot be accessed.", e);
		}
	}
	
	@Override
	protected void log(LogLevel level, Object object) {
		String out = createPrefix(calendar, level, id) + object.toString();
		System.out.println(out);
		fileLogger.println(out);
	}

	@Override
	protected void throwing(LogLevel level, String message, Throwable throwable) {
		if(message == null || message.isEmpty())
			message = throwable.getLocalizedMessage();
		log(level, message);
		throwable.printStackTrace();
		throwable.printStackTrace(this.fileLogger);
	}
	
	private String createPrefix(Calendar calendar, LogLevel level, String id){
		return String.format("%s [%s] [%s] ", date.format(calendar.getTime()), level.name(), id);
	}
}
