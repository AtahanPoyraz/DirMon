package com.dirmon.project.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLogger {
    public static Logger getLogger(Class<?> source) {
        return LoggerFactory.getLogger(source);
    }

    public static void info(Class<?> source, String message) {
        getLogger(source).info(message);
    }

    public static void warn(Class<?> source, String message) {
        getLogger(source).warn(message);
    }

    public static void error(Class<?> source, String message) {
        getLogger(source).error(message);
    }

    public static void error(Class<?> source, String message, Throwable throwable) {
        getLogger(source).error(message, throwable);
    }

    public static void debug(Class<?> source, String message) {
        getLogger(source).debug(message);
    }
}