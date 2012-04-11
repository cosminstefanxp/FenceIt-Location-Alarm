package com.fenceit;

import org.apache.log4j.Level;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Configures the Log4J Environment. 
 * 
 * NOTE: To check the files on the SD, either user Eclipse -> DDMS -> File Explorer
 * or use "$android-sdks/platform-tools/adb shell"
 */
public class Log4jConfiguration {
	static {
		final LogConfigurator logConfigurator = new LogConfigurator();
		//File log configuration
		logConfigurator.setUseFileAppender(true);
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ "/FenceIt/Main.log");
		logConfigurator.setMaxFileSize(1024*50);
		logConfigurator.setMaxBackupSize(3);
		logConfigurator.setFilePattern("%d [%-5p] %c - %m%n");
		
		//Log cat configuration
		logConfigurator.setUseLogCatAppender(true);
		logConfigurator.setLogCatPattern("[%-5p] %c - %m%n");

		//Basic configuration
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.configure();
	}
}