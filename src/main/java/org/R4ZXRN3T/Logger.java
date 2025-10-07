package org.R4ZXRN3T;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	private File logFile;
	private PrintWriter writer;
	private String formatString = "[%t] [%l]\t%m"; // [timestamp] message
	private boolean isWriterInitialized = false;

	public Logger(String logFilePath) {
		this.logFile = new File(new File(logFilePath).getAbsolutePath());
	}

	public Logger(String logFilePath, String formatString) {
		this.logFile = new File(new File(logFilePath).getAbsolutePath());
		this.formatString = formatString;
	}

	public void error(String message) {
		print("ERROR", message);
	}

	public void warn(String message) {
		print("WARN", message);
	}

	public void info(String message) {
		print("INFO", message);
	}

	public void debug(String message) {
		print("DEBUG", message);
	}

	public void close() {
		if (isWriterInitialized) writer.close();
		logFile = null;
		writer = null;
		isWriterInitialized = false;
	}

	private void print(String levelString, String message) {
		if (!isWriterInitialized) initWriter();
		String logEntry = formatString.replace("%t", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.replace("%l", levelString)
				.replace("%m", message);
		writer.println(logEntry);
		writer.flush();
	}

	private void initWriter() {
		if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			writer = new PrintWriter(logFile);
			isWriterInitialized = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}