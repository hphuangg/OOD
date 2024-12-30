package org.example.logframework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/*
* TODO:
* 1. implement file appender, database appender.
* 2. implement json formatter.
* 3. implement LogManager?
* 4. datetime format
* */
public class LogFrameworkDemo {

    public static void main(String[] args) {
        Logger logger = new Logger(new Config(LogLevel.DEBUG, new ConsoleAppender()));
        logger.debug("debug message.");
        logger.info("info message.");
        logger.warning("warning message.");
        logger.error("error message.");
        logger.fatal("fatal message.");
    }
}

enum LogLevel {
    DEBUG, INFO, WARNING, ERROR, FATAL
}

@AllArgsConstructor
@Data
class LogMessage {
    LogLevel level;
    String message;
    LocalDateTime timestamp;
}

@AllArgsConstructor
@Data
abstract class Appender {

    LogFormater formater;

    public Appender() {
        formater = new DefaultLogFormater();
    }

    abstract public void append(LogMessage logMessage);
}

@AllArgsConstructor
class ConsoleAppender extends Appender {

    @Override
    public void append(LogMessage logMessage) {
        System.out.println(formater.format(logMessage));
    }
}

@AllArgsConstructor
class FileAppender extends Appender {

    @Override
    public void append(LogMessage logMessage) {
        // implement append logic
    }
}

@AllArgsConstructor
class DatabaseAppender extends Appender {

    @Override
    public void append(LogMessage logMessage) {
        // implement append logic
    }
}

interface LogFormater {
    String format(LogMessage message);
}

class DefaultLogFormater implements LogFormater {

    @Override
    public String format(LogMessage message) {
        Thread thread = Thread.currentThread();
        return String.format("[%s][%s][%s]: %s",
                message.getTimestamp(), thread.getName(), message.getLevel(), message.getMessage());
    }
}

@AllArgsConstructor
@Data
class Config {
    LogLevel level;
    Appender appender;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class Logger {

    Config config;

    public void log(LogLevel level, String message) {
        if (level.ordinal() >= config.getLevel().ordinal()) {
            LogMessage logMessage = new LogMessage(level, message, LocalDateTime.now());
            config.getAppender().append(logMessage);
        }
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

}
