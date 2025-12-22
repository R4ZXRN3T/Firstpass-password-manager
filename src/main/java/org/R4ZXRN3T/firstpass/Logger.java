package org.R4ZXRN3T.firstpass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Logger class for asynchronous logging to a file.
 * Supports different log levels and customizable log format.
 * Thread-safe and uses a background thread for writing logs.
 */
public class Logger {
	private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final Object POISON = new Object();

	private final File logFile;
	private final String formatString;
	private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
	private final Thread worker;
	private volatile boolean running = true;
	private PrintWriter writer;
	private volatile boolean initialized = false;

	/**
	 * Constructs a Logger with the specified log file path and default format.
	 * @param logFilePath the path to the log file
	 */
	public Logger(String logFilePath) {
		this(logFilePath, "[%t] [%l]\t%m");
	}

	/**
	 * Constructs a Logger with the specified log file path and format string.
	 * @param logFilePath the path to the log file
	 * @param formatString the format string for log entries
	 */
	public Logger(String logFilePath, String formatString) {
		this.logFile = new File(new File(logFilePath).getAbsolutePath());
		this.formatString = formatString;
		this.worker = new Thread(this::drainLoop, "logger-writer");
		this.worker.setDaemon(true);
		this.worker.start();
	}

	/**
	 * Logs an error message.
	 * @param message the message to log
	 */
	public void error(String message) {
		enqueue("ERROR", message);
	}

	/**
	 * Logs a warning message.
	 * @param message the message to log
	 */
	public void warn(String message) {
		enqueue("WARN", message);
	}

	/**
	 * Logs an informational message.
	 * @param message the message to log
	 */
	public void info(String message) {
		enqueue("INFO", message);
	}

	/**
	 * Logs a debug message.
	 * @param message the message to log
	 */
	public void debug(String message) {
		enqueue("DEBUG", message);
	}

	/**
	 * Closes the logger and releases resources.
	 * Waits for the background thread to finish writing.
	 */
	public void close() {
		if (!running) return;
		running = false;
		queue.offer(POISON);
		try {
			worker.join();
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
		if (writer != null) writer.close();
	}

	/**
	 * Enqueues a formatted log entry for writing.
	 * @param level the log level
	 * @param msg the log message
	 */
	private void enqueue(String level, String msg) {
		if (!running) return;
		String line = formatString
				.replace("%t", LocalDateTime.now().format(TS_FMT))
				.replace("%l", level)
				.replace("%m", msg);
		queue.offer(line);
	}

	/**
	 * Initializes the PrintWriter if it has not been initialized.
	 * Creates the log file and its parent directories if necessary.
	 */
	private void initWriterIfNeeded() {
		if (initialized) return;
		synchronized (this) {
			if (initialized) return;
			File parent = logFile.getParentFile();
			if (parent != null && !parent.exists()) parent.mkdirs();
			try {
				if (!logFile.exists()) logFile.createNewFile();
				writer = new PrintWriter(new FileWriter(logFile, true));
				initialized = true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Background thread loop for draining the log queue and writing to the file.
	 */
	private void drainLoop() {
		try {
			while (true) {
				Object obj = queue.take();
				if (obj == POISON) break;
				initWriterIfNeeded();
				writer.println((String) obj);
				writer.flush();
			}
			queue.forEach(o -> {
				if (o != POISON) writer.println((String) o);
			});
			if (writer != null) writer.flush();
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}
}