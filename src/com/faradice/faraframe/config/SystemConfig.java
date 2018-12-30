/*
 * Copyright (c) 2006 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */
package com.faradice.faraframe.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.faradice.faraframe.log.Log;
import com.faradice.faraframe.util.StringUtil;

/**
 *
 * Configuration Management for system and application config
 *
 * System Configuration can be loaded from 2 sources:
 *
 * 1. From an xml configuration factory
 *    that contains all configuration files that are loaded.
 *    Specified with the vm parameter: config.factory
 *
 * 2. From a config file
 *    specified with the vm parameter: config.file
 *    The file can be an xml file (must have .xml ending) or a normal property file
 *
 * SystemProperties are automatically included in configuration
 * (even though no config file is specified)
 *
 * Example of a configuration factory file:
 * <?xml version="1.0" encoding="ISO-8859-1" ?>
 *   <configuration>
 *     <system/>
 *     <properties fileName="application.properties"/>
 *     <xml fileName="application.xml"/>
 *   </configuration>
 *
 * @version $Id: SystemConfig.java,v 1.43 2011/05/05 15:34:05 gudmfr Exp $
 */
public class SystemConfig {
    private static final String CONFIG_FACTORY_KEY = "config.factory";
    private static final String CONFIG_FILE_KEY = "config.file";
    private static final String CONFIG_PREFIX = "config_prefix";
    private static final String CONFIG_FILE_OLD_KEY = "config_file";  // Backward compatibility key

    private static String defaultPrefix = null;    
    private static final Config config = new Config();
    
	/** Keep a map of filename starts that should be autoconverted based on current platform. */
	private static final String[] dosPrefixes;  
	private static final String[] unixPrefixes;
	static {
		final ArrayList<String> dos = new ArrayList<String>();
		final ArrayList<String> unix = new ArrayList<String>();
		final String param = System.getProperty("decode.files.dos2unix", "").trim();
		if (param.length() > 0) {
			final ArrayList<String> pairs = StringUtil.split(System.getProperty("decode.files.dos2unix", ""), '+');			
			for (String pair : pairs) {
				final ArrayList<String> list = StringUtil.split(pair, '#');
				if (list.size() == 2) {
					final String d = list.get(0).trim().replace('\\', '/');
					final String u = list.get(1).trim().replace('\\', '/');
					dos.add(d);
					unix.add(u);
				} else {
					System.err.println("skip invalid gor.dos2unix entry: " + pair);
				}
			}
		}
		
		dosPrefixes = dos.toArray(new String[dos.size()]);
		unixPrefixes = unix.toArray(new String[unix.size()]);
		assert dosPrefixes.length == unixPrefixes.length;
	}
	
	/**
	 * Based on configured mapping of equivalent dos and unix filename prefixes, convert name according to platform
	 * @param filename The filename to convert, all beginning / tokens are ignored
	 * @return The filename fitted for the current platform
	 */
	public static final String convertFileName2Platform(String filename) {
		return isWindowsOS() ? convertFileName2Dos(filename) : convertFileName2Unix(filename);
	}

	/**
	 * Based on configured mapping of equivalent dos and unix filename prefixes, convert name to dos compatible name
	 * @param filename The filename to convert.
	 * @return The filename as fitting dos
	 */
	public static final String convertFileName2Dos(String filename) {
		return convertFileName(filename, unixPrefixes, dosPrefixes);
	}

	/**
	 * Based on configured mapping of equivalent dos and unix filename prefixes, convert name to unix compatible name
	 * @param filename The filename to convert.
	 * @return The filename as fitting unix
	 */
	public static final String convertFileName2Unix(String filename) {
		return convertFileName(filename, dosPrefixes, unixPrefixes);
	}
	
	/**
	 * Convert file names based on the provided prefix arrays. I.e. if a filename is prefixed with
	 * a string in the array from, then the prefix will be replaced with the string in to at the same index. 
	 * @param filename The filename to convert
	 * @param from The from array
	 * @param to The to array
	 * @return The converted filename
	 */
	public static final String convertFileName(String filename, String[] from, String[] to) {
		for (int i = 0; i < from.length; i++) {
			if (filename.startsWith(from[i])) {
				return to[i] + filename.substring(from[i].length());
			}
		}
		return filename;
	}
	
	    
    static class Config {
    	private Properties props = System.getProperties(); // System properties should have the lowest priority
    	void addConfiguration(String file) {
    		final Properties p = new Properties(props); // Chain the properties collections, in effect giving last added properties most priority 
    		try {
	    		FileReader reader = new FileReader(file);
	    		p.load(reader);
	    		reader.close();
	    		props = p;
    		} catch (Exception ex) {
    			throw new RuntimeException("Can't read properties file: " + file, ex);
    		}
    	}
    	void addConfiguration(Properties prop) {
    		for (Map.Entry<Object,Object> entry : prop.entrySet()) {
    			props.put(entry.getKey(), entry.getValue());
    		}
    	}
		public boolean getBoolean(String key) {
			final String value = getProperty(key);
			if (value != null) {
				return "true".equalsIgnoreCase(value);
			}
			throw new RuntimeException("Configuration Parameter " + key + " has no value");
		}
		public boolean getBoolean(String key, boolean defaultValue) {
			final String value = getProperty(key);
			return value != null ? "true".equalsIgnoreCase(value) : defaultValue; 
		}
		public int getInt(String key) {
			final String value = getProperty(key);
			if (value != null) {
				return Integer.parseInt(value);
			}
			throw new RuntimeException("Configuration Parameter " + key + " has no value");
		}
		public int getInt(String key, int defaultValue) {
			final String value = getProperty(key);
			return value != null ? Integer.parseInt(value) : defaultValue; 
		}
		public long getLong(String key) {
			final String value = getProperty(key);
			if (value != null) {
				return Long.parseLong(value);
			}
			throw new RuntimeException("Configuration Parameter " + key + " has no value");
		}
		public long getLong(String key, long defaultValue) {
			final String value = getProperty(key);
			return value != null ? Long.parseLong(value) : defaultValue; 
		}
		public float getFloat(String key) {
			final String value = getProperty(key);
			if (value != null) {
				return Float.parseFloat(value);
			}
			throw new RuntimeException("Configuration Parameter " + key + " has no value");
		}
		public float getFloat(String key, float defaultValue) {
			final String value = getProperty(key);
			return value != null ? Float.parseFloat(value) : defaultValue; 
		}
		public double getDouble(String key) {
			final String value = getProperty(key);
			if (value != null) {
				return Double.parseDouble(value);
			}
			throw new RuntimeException("Configuration Parameter " + key + " has no value");
		}
		public double getDouble(String key, double defaultValue) {
			final String value = getProperty(key);
			return value != null ? Double.parseDouble(value) : defaultValue; 
		}
		public String getString(String key) {
			return getProperty(key);
		}
		public String getString(String key, String defaultValue) {
			final String value = getProperty(key);
			return value != null ? value : defaultValue; 
		}
		
		private String getProperty(String key) {
			final String value = props.getProperty(key);
			return replaceVariable(value);
		}
		
		/** Replace variable in values with lookup value, i.e. ${xxx}yyy will be someyyy if a property named xxx is defined to some*/
        private String replaceVariable(final String value) {
	        if (value == null) {
				return null;
			}
			
			String retvalue = value;
			int begin = 0;
			while ((begin = value.indexOf("${", begin)) != -1) {
				begin += 2;
				final int end = value.indexOf("}", begin);
				if (end >= begin) { 
					final String varname = value.substring(begin, end);
					final String varvalue = getString(varname);
					if (varvalue != null) {
						retvalue = retvalue.replace("${" + varname + "}", varvalue);
					}
					begin = end;
				}
			}
			return retvalue.trim();
        }
	}    

    static {
        loadConfig();
    }

    static ArrayList<File> parseConfigFactory(String filename) {    	
    	final SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
        	final File file = new File(filename);
            final SAXParser parser = factory.newSAXParser();
            final ArrayList<File> files = new ArrayList<File>();
            final DefaultHandler handler = new DefaultHandler() {
                @Override public void startElement(String uri, String localName, String qName, Attributes attr) {
                    if (qName.equals("properties")) {
                    	final String configfile = attr.getValue("fileName");
                    	File f = new File(configfile);
                    	if (!f.exists()) {
                    		f = new File(file.getParent() + '/' + configfile);
                    	}
                        files.add(f);
                    }
                }
            };
            parser.parse(new BufferedInputStream(new FileInputStream(filename)), handler);
            return files;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Loads the configuration from config file or factory
     */
    public static final void loadConfig() {
        try {
            String fileName = System.getProperty(CONFIG_FACTORY_KEY);
            try {
                if (fileName != null) {
                	ArrayList<File> files = parseConfigFactory(fileName);
                	Collections.reverse(files); // The first file should be first read, add it last in the config
                	for (File file : files) { 
                		config.addConfiguration(file.getCanonicalPath());
                	}
                } else {
                    fileName = System.getProperty(CONFIG_FILE_KEY);
                    if (fileName == null) {
                        fileName = System.getProperty(CONFIG_FILE_OLD_KEY);
                    }
                    if (fileName != null && fileName.endsWith("xml")) {
                    	throw new RuntimeException(".xml configuration files are not supported");
                    } else if (fileName != null) {
                    	config.addConfiguration(fileName);
                    }
                }
            } catch (Throwable ex) {
                Log.echo.severe(ex, "Could not load configuration file: "+fileName+"\n"+ex.getMessage());
            }
            defaultPrefix = System.getProperty(CONFIG_PREFIX);
        } catch (Throwable ex) {
            Log.echo.severe(ex, "Could not read system properties");
        }
    }
    

    /**
     * Adds all properties from a property file to the configuration
     * All properties that exists will be replaced with the new properties
     * This must be a valid key value property file
     * @param fileName name of the file
     */
    public static void addPropertyFile(String fileName) {
        try {
        	config.addConfiguration(fileName);
        } catch (Exception ex) {
            String path = "";
        	try {
        		path = " in directory " + new File(".").getCanonicalPath();
        	} catch (IOException e) {
        		path = " because directory is invalid(" + e.getMessage() + ") ";
        	}
        	Log.echo.severe(ex, "Could not load configuration file: " + fileName + path + "\n"+ex.getMessage());
        }
    }

    /**
     * Adds all properties from a map to the configuration
     * All properties that exists will be replaced with the new properties
     * This must be a valid key value property file
     * @param properties Map containing all properties to add or replace
     */
    public static void addProperties(Map<?,?> properties) {
    	for (Map.Entry<?, ?> entry : properties.entrySet()) {
    		config.props.setProperty((String)entry.getKey(), (String)entry.getValue());
    	}
    }

    /**
     * sets (replaces) a value of a property to the configuration
     * @param key the property name
     * @param value the value of the property
     */
    public static void setProperty(String key, String value) {
        config.props.setProperty(key, value);
    }


    /**
     * Returns a property identified by the given key as a string, or null if not found.
     * @param key The key value to look for
     * @return The property associated with the key, or null if nothing is found
     */
    public static final String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * @param key the name of the property
     * @param defVal Value to return if property not found
     * @return the int value of the property or default value
     */

    public static final int getInt(String key, int defVal) {
        int result = defVal;
        if (defaultPrefix != null) {
            result = config.getInt(defaultPrefix+"."+key, defVal);
        }
        if (result == defVal) {
            result = config.getInt(key, defVal);
        }
        return result;
    }
    
    /**
     * @param key the name of the property
     * @return the long value of the property
     */
    public static final int getInt(String key) {
        if (defaultPrefix != null) {
            return config.getInt(defaultPrefix+"."+key);
        }        
        return config.getInt(key);        
    }
    
    /**
     * Returns multiple int values for a key. Values should be comma-separated in configuration file
     * @param key
     * @return An array of one ore more values
     */
    public static final int[] getInts(String key) {
    	String value = getProperty(key);
    	String[] values = value.split(",");
    	int[] ints = new int[values.length];
    	try {
			for (int i = 0; i < ints.length; i++) {
				ints[i] = Integer.parseInt(values[i]);
			}
			return ints;
		} catch (NumberFormatException e) {
			throw new RuntimeException("Values for key '" + key + "' must only contain numbers. Found '" + value + "'.");
		}
    }
    
    /**
     * @param key the name of the property
     * @param defVal Value to return if property not found
     * @return the long value of the property or default value
     */

    public static final long getLong(String key, long defVal) {
        long result = defVal;
        if (defaultPrefix != null) {
            result = config.getLong(defaultPrefix+"."+key, defVal);
        }
        if (result == defVal) {
            result = config.getLong(key, defVal);
        }
        return result;
    }

    /**
     * @param key the name of the property
     * @return the long value of the property
     */
    public static final long getLong(String key) {
        if (defaultPrefix != null) {
            return config.getLong(defaultPrefix+"."+key);
        }        
        return config.getLong(key);
        
    }
    
    /**
     * @param key the name of the property
     * @param defVal Value to return if property not found
     * @return the float value of the property or default value
     */

    public static final float getFloat(String key, float defVal) {
        float result = defVal;
        if (defaultPrefix != null) {
            result = config.getFloat(defaultPrefix+"."+key, defVal);
        }
        if (result == defVal) {
            result = config.getFloat(key, defVal);
        }
        return result;
    }

    /**
     * @param key the name of the property
     * @return the float value of the property
     */
    public static final float getFloat(String key) {
        if (defaultPrefix != null) {
            return config.getFloat(defaultPrefix+"."+key);
        }        
        return config.getFloat(key);
        
    }


    /**
     * @param key the name of the property
     * @param defVal Value to return if property not found
     * @return the boolean value of the property or default value
     */
    public static final boolean getBoolean(String key, boolean defVal) {
        boolean result = defVal;
        if (defaultPrefix != null) {
            result = config.getBoolean(defaultPrefix+"."+key, defVal);
        }
        if (result == defVal) {
           result =config.getBoolean(key, defVal);
        }
        return result;
    }

    /**
     * @param key the name of the property
     * @param defVal Value to return if property not found
     * @return the double value of the property or default value
     */
    public static final double getDouble(String key, double defVal) {
        double result = defVal;
        if (defaultPrefix != null) {
            result = config.getDouble(defaultPrefix+"."+key, defVal);
        }
        if (result == defVal) {
           result =config.getDouble(key, defVal);
        }
        return result;
    }

    /**
     * Query for file path, assuming entries exists for X if running on windows, and XLinux if running on unix
     * @param key The key to query for
     * @return The file path
     */
    public static final String getFilePath(String key) {
        return getProperty(isWindowsOS() ? key : key + "Linux");
    }

	/** @return True if we are running on the windows OS */
    public static boolean isWindowsOS() {
	    return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    /**
     * Returns a property value as a string
     * @param key the property
     * @param defVal Value to return if property was not found
     * @return The property or default value
     */
    public static final String getString(String key, String defVal) {
        if (defaultPrefix !=  null) {
            return getPrefixProperty(defaultPrefix, key, defVal);
        }
		return config.getString(key, defVal);
    }

    /**
     * Returns a property value as a string
     * @param key the property
     * @param defVal Value to return if property was not found
     * @return The property or default value
     */
    public static final String getProperty(String key, String defVal) {
        return getString(key, defVal);
    }
    
    /**
     * Returns a property identified by prefix and key combined.
     * If not found then default.key is tried.
     * If not found then the key is tried alone.
     * @param prefix The prefix to add to the key
     * @param key The key value
     * @return The value associated with the key or null if nothing is found
     */
    public static final String getPrefixProperty(String prefix, String key) {
        String value = null;

        // Try using the given prefix        
        value = config.getString(prefix + "." + key);

        // Try without prefix - the default
        if (value == null || value.equals("")) {
            value = config.getString(key);
        }

        return value;
    }

    /**
     * Returns a property identified by prefix and key combined.
     * If not found then default.key is tried.
     * If not found then the key is tried alone.
     * If not found then the default value is returned.
     * @param prefix The prefix to add to the key
     * @param key The key to look for
     * @param defaultvalue The default value to use if nothing is found
     * @return The value associated with the key, or defaultValue if nothing is found
     */
    public static final String getPrefixProperty(String prefix, String key, String defaultvalue) {
        String prop = getPrefixProperty(prefix, key);
        return (prop != null) ? prop : defaultvalue;
    }

    /**
     * Returns a property identified by key and postfix combined.
     * If not found then the key is tried alone.
     * @param key The key value to look for
     * @param postfix The postfix to add to the key for looking
     * @return The value associated with the key, or null if nothing is found.
     */
    public static final String getPostfixProperty(String key, String postfix) {
        String value = null;

        // Try using the given postfix
        value = getProperty(key + "." + postfix);

        // Try without postfix - the default
        if (value == null || value.equals("")) {
            value = getProperty(key);
        }

        return value;
    }

    /**
     * Returns a property identified by key and postfix combined.
     * If not found then the key is tried alone.
     * if not found the default value is returned
     * @param key The key value to look for
     * @param postfix The postfix to add to the key
     * @param defaultvalue The default value to use if nothing is found
     * @return The value associated with the key, or default value if nothing is found.
     */
    public static final String getPostfixProperty(String key, String postfix, String defaultvalue) {
        String prop = getPostfixProperty(key, postfix);
        return (prop != null) ? prop : defaultvalue;
    }

	/**
	 * Sets the default prefix configured
	 * @param defaultPrefix the prefix that we want as default for all values in the config
	 */
	public static void setDefaultPrefix(String defaultPrefix) {
		SystemConfig.defaultPrefix = defaultPrefix;
	}
	
	/**
	 * Gets the default prefix configured
	 * @return String the defaultPrefix
	 */
	public static String getDefaultPrefix() {
		return defaultPrefix;
	}
	    
    /**
     * Returns a property object containing all values prefixed by the given prefix parameter.
     * The Key value in the returned object contains only the remaining suffix of the original key.
     * @param prefix The prefix to use
     * @return The Properties collection of all values found with the specified prefix
     */    
    public static Properties getPropertiesSubset(String prefix) {
    	final Properties p = new Properties();
    	Enumeration<?> props = config.props.propertyNames();
        while(props.hasMoreElements()) {
        	final String key = (String)props.nextElement();
			if (key.startsWith(prefix)) {
				p.put(key, config.getString(key));
			}
		}

    	return p;
    }
    
    /** @return a Property containing all configuration variables in SystemConfig */
    public static Properties getProperties() {
    	Properties p = new Properties();
    	Enumeration<?> props = config.props.propertyNames();
        while(props.hasMoreElements()) {
        	final String key = (String)props.nextElement();
        	final String value = config.getString(key);
        	p.put(key, value);
        }
    	return p;
    }
    
	/**
	 * Gets all keys for properties that match the given regular expresssion
	 * @param regex a regular expression that describes the keys we want
	 * @return a list of keys that match the regular expression
	 */
	public static String[] getPropertyKeysThatMatch(String regex) {
		Enumeration<?> keys = config.props.propertyNames();
		HashSet<String> props = new HashSet<String>();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.matches(regex)) {
				props.add(key.trim());
			}
		}
		return props.toArray(new String[props.size()]);
	}
}