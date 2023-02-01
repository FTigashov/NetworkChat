package com.personal.networkchat.server.handler;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggingConfig {
    public static Logger admin;
    public static Logger admin_console;
    public static Logger file;

    static {
        PropertyConfigurator.configure("src/main/resources/config/log4j.properties");
        admin = Logger.getLogger("admin");
        admin_console = Logger.getLogger("admin_console");
//        file = Logger.getLogger("file");
    }
}
