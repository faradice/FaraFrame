package com.faradice.faraframe.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * General Property Container.  Reads and keeps configuration
 * properties.
 *
 * @author ragnar.valdimarsson
*/
public class Configuration {
	private final String fileName;
    private Properties properties = null;

    public Configuration(String fileName) {
    	this.fileName = fileName;
	}

	public String get(String key) throws Exception {
		if (properties == null) {
			load();
		}
		String value =  properties.getProperty(key);
		if (value == null) {
			throw new Exception("The property "+key+" is missing from the configuration file "+fileName);
		}
		return value.trim();
	}

	private void load() throws Exception {
	    InputStream stream = new FileInputStream(fileName);
		try {
		    properties = new Properties();
	        properties.load(stream);
		} finally {
			if (stream != null) stream.close();
		}
	}

	public static boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0 || s.trim().equalsIgnoreCase("null"));
	}

}
