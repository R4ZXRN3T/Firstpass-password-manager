package org.R4ZXRN3T;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;

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

	public Logger(String logFilePath) {
		this(logFilePath, "[%t] [%l]\t%m");
	}

	public Logger(String logFilePath, String formatString) {
		this.logFile = new File(new File(logFilePath).getAbsolutePath());
		this.formatString = formatString;
		this.worker = new Thread(this::drainLoop, "logger-writer");
		this.worker.setDaemon(true);
		this.worker.start();
	}

	public void error(String message) {
		enqueue("ERROR", message);
	}

	public void warn(String message) {
		enqueue("WARN", message);
	}

	public void info(String message) {
		enqueue("INFO", message);
	}

	public void debug(String message) {
		enqueue("DEBUG", message);
	}

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

	private void enqueue(String level, String msg) {
		if (!running) return;
		String line = formatString
				.replace("%t", LocalDateTime.now().format(TS_FMT))
				.replace("%l", level)
				.replace("%m", msg);
		queue.offer(line);
	}

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