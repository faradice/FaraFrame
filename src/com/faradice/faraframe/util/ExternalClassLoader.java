package com.faradice.faraframe.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.prefs.Preferences;

import com.faradice.faraframe.config.SystemConfig;

/**
 * @param <T>  Type of class to load
 */
public class ExternalClassLoader<T> {
	/***/
	public static final String DEFAULT_EXTERNAL_CLASSPATH = SystemConfig.getProperty("external.classpath", "c:/gb/classes/");
	/** class path */
	public final String classPath;
	/** class name */
	public final String className;
	/** classpath urls */
	public final URL[] urls;

	private static final String CONFIG_PREFIX = "decode.external.classpath.";

	private Class<T> clazz;
	private T instance;

	static {
		File cp = new File(DEFAULT_EXTERNAL_CLASSPATH);
		cp.mkdirs();
	}
	
	/**
	 * Loads a class from the default decode classpath
	 * 
	 * @param className  full name with packege of the class
	 * @throws Exception
	 */
	public ExternalClassLoader(String className) throws Exception {
		this (DEFAULT_EXTERNAL_CLASSPATH, className);
	}

	/**
	 * @param classPath The classpath of the class
	 * @param className The full name with package of the loader class
	 * @throws Exception 
	 */
	public ExternalClassLoader(String classPath, String className) throws Exception {
		classPath = classPath.trim();
		File path = new File(classPath);
		URL url = path.toURI().toURL();
		this.className = className;
		this.classPath = classPath;
		urls = new URL[]{url};
		reload();
	}

	
	/**
	 * @throws Exception
	 */
	public ExternalClassLoader() throws Exception {
		this (DEFAULT_EXTERNAL_CLASSPATH, null);
	}

	/**
	 * @return the external loader
	 */
	public T getInstance() {
		try {
			if (instance == null) {
				instance = clazz.newInstance();
			}
			return instance;
		} catch (Exception ex) {
			throw new RuntimeException("Could not create instance for class "+clazz.getName(), ex);
		}
	}

	/**
	 * @return the class
	 */
	public Class<T> getExternalClass() {
		return clazz;
	}
	
	/**
	 * @return a new classloader with the external classpath
	 */
	public ClassLoader getClassLoader() {
		return new URLClassLoader(urls, this.getClass().getClassLoader());
	}
	
	/**
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void reload() throws Exception {
		if (className != null) {
			clazz = (Class<T>)getClassLoader().loadClass(className);
			instance = null;
		}
	}

	/**
	 * @param name Name
	 * @return the classpath
	 */
	public static String getClassPath(String name) {
		String key = CONFIG_PREFIX + name;
		key = fixKey(key);
		String defaultCP = ExternalClassLoader.DEFAULT_EXTERNAL_CLASSPATH;
		String classPath = Preferences.userRoot().get(key, defaultCP);
		return classPath.trim();
	}
	
	/**
	 * @param name the name
	 * @return name as it will be used and stored
	 */
	public static String fixKey(String name) {
        name = name.replaceAll("\\\\","/");
		name = name.toLowerCase().trim();
		name = (name.length()+1 >= Preferences.MAX_KEY_LENGTH) ? name.substring(0, Preferences.MAX_KEY_LENGTH-2) : name;
		return name;
	}
	
	/**
	 * @param name key
	 * @param cp class path
	 */
	public static void saveClassPath(String name, String cp) {
		cp = cp.trim();
		String key = CONFIG_PREFIX + name;
		key = fixKey(key);
		Preferences.userRoot().put(key, cp);
	}
	
}
