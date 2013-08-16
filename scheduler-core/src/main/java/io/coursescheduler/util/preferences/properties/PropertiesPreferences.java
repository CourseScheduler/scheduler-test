/**
  * @(#)PropertiesFilePreferences.java
  *
  * Provide a properties file backed Preferences implementation
  *
  * @author Mike Reinhold
  * 
  * @license GNU General Public License version 3 (GPLv3)
  *
  * This file is part of Course Scheduler, an open source, cross platform
  * course scheduling tool, configurable for most universities.
  *
  * Copyright (C) 2010-2013 Mike Reinhold
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
  */
package io.coursescheduler.util.preferences.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Provide a properties file backed Preferences implementation
 *
 * @author Mike Reinhold
 *
 */
public abstract class PropertiesPreferences extends AbstractPreferences {

	/**
	 * TODO Describe this field
	 */
	private static final String ROOT_FILE_NAME_USER = "user-root";	
	
	/**
	 * TODO Describe this field
	 */
	private static final String ROOT_FILE_NAME_SYSTEM = "system-root";
		
	/**
	 * TODO Describe this field
	 */
	public static final String PROPERTY_PATH_USER = "io.coursescheduler.util.preferences.path.user";
	
	/**
	 * TODO Describe this field
	 */
	public static final String PROPERTY_PATH_SYSTEM = "io.coursescheduler.util.preferences.path.system";
	
	/**
	 * TODO Describe this field
	 */
	private static final String DEFAULT_PATH_USER = ".";
	
	/**
	 * TODO Describe this field
	 */
	private static final String DEFAULT_PATH_SYSTEM = ".";
	
	/**
	 * The Properties object that stores the Preferences elements
	 */
	private Properties properties;
		
	/**
	 * TODO Describe this field
	 */
	private String nodeName;
	
	/**
	 * TODO Describe this field
	 */
	private boolean rootNode;
	
	/**
	 * TODO Describe this field
	 */
	private boolean systemNode;

	/**
	 * TODO Describe this field
	 */
	private PropertiesPreferences propertiesFileParent;
	
	/**
	 * TODO Describe this field
	 */
	private Map<String, PropertiesPreferences> children;
	
	/**
	 * Create a new Preferences instance as a child of the specified instance using the
	 * specified node name.
	 *
	 * @param parent the parent Preferences node
	 * @param name the preferences node name
	 */
	PropertiesPreferences(AbstractPreferences parent, String name) {
		super(parent, name);
		
		propertiesFileParent = (PropertiesPreferences)parent;
		properties = new Properties();
		children = new HashMap<>();
		nodeName = name;
		rootNode = false;
		
		try{
			sync();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
		try {
			flush();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new root Preferences instance using the specified node name. If isUserRoot
	 * is specified then a user root Preferences instance will be created, otherwise a 
	 * system root Preferences instance will be created.
	 *
	 * @param name the preferences node name
	 * @param isUserNode true if a user root instance, false if a system root
	 */
	PropertiesPreferences(String name, boolean isUserNode){
		super(null, name);
		
		propertiesFileParent = null;
		properties = new Properties();
		children = new HashMap<>();
		nodeName = name;
		rootNode = true;
		systemNode = !isUserNode;
		
		try {
			sync();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
		try {
			flush();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#putSpi(java.lang.String, java.lang.String)
	 */
	@Override
	protected void putSpi(String key, String value) {
		properties.put(key, value);
		
		try {
			flush();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#getSpi(java.lang.String)
	 */
	@Override
	protected String getSpi(String key) {
		return properties.getProperty(key);
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#removeSpi(java.lang.String)
	 */
	@Override
	protected void removeSpi(String key) {
		properties.remove(key);
		
		try {
			flush();
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#removeNodeSpi()
	 */
	@Override
	protected synchronized void removeNodeSpi() throws BackingStoreException {
		// TODO METHOD STUB

	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#keysSpi()
	 */
	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return properties.keySet().toArray(new String[properties.keySet().size()]);
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#childrenNamesSpi()
	 */
	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return children.keySet().toArray(new String[children.keySet().size()]);
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#childSpi(java.lang.String)
	 */
	@Override
	protected synchronized AbstractPreferences childSpi(String name) {
		PropertiesPreferences node = children.get(name);
		
		if(node == null){
			node = createChildNode(this, name);
			children.put(name, node);
		}
		return node;
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#syncSpi()
	 */
	@Override
	protected void syncSpi() throws BackingStoreException {
		synchronized (lock) {
			File file = getAndCreateFilePaths();
			
			try (FileInputStream fileInStream = new FileInputStream(file)){
				
				load(properties, fileInStream);
				
				//TODO anything?
				
			} catch (IOException e) {
				// TODO CATCH STUB
				//e.printStackTrace();
			}
		}
	}

	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	private File getAndCreateFilePaths() {
		File file = new File(getFullFilePathAndExtension());
		File path = file.getParentFile();
		
		newNode = !file.exists();
		if(path != null && !path.exists()){
			path.mkdirs();
		}
		
		return file;
	}

	/* (non-Javadoc)
	 * @see java.util.prefs.AbstractPreferences#flushSpi()
	 */
	@Override
	protected void flushSpi() throws BackingStoreException {
		synchronized(lock){
			File file = getAndCreateFilePaths();
			
			try(FileOutputStream fileOutStream = new FileOutputStream(file)){
				store(properties, fileOutStream);
			} catch (IOException e) {
				// TODO CATCH STUB
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return the filePath
	 */
	protected String getFilePath() {
		String filePath;
		
		if(isRootNode()){
			filePath = isSystemNode() ? getRootSystemNodeFilesystemPath() : getRootUserNodeFilesystemPath();
		}else{
			filePath = propertiesFileParent.getFilePath();
		}
		
		return filePath;
	}
	

	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	private String getRootUserNodeFilesystemPath(){
		String path = System.getProperty(PROPERTY_PATH_USER);
		
		if(path == null){
			path = DEFAULT_PATH_USER;
		}
		
		return path;
	}
	
	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	private String getRootSystemNodeFilesystemPath(){
		String path = System.getProperty(PROPERTY_PATH_SYSTEM);
		
		if(path == null){
			path = DEFAULT_PATH_SYSTEM;
		}
		
		return path;
	}

	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	protected String getFullFilePathAndExtension() {
		String filePath = getFilePath();
		String fullPathAndExtension;
		
		fullPathAndExtension = filePath + "/" + getFullNodeName() + "." + getFileExtension();
		
		return fullPathAndExtension;
	}
	
	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	protected String getFullNodeName(){
		String fullNodeName;
		
		if(propertiesFileParent == null){
			fullNodeName = isSystemNode() ? ROOT_FILE_NAME_SYSTEM : ROOT_FILE_NAME_USER;
		}else{
			String parentAbsolutePath = propertiesFileParent.absolutePath();
			
			fullNodeName = parentAbsolutePath + "/" + getNodeName();
		}
		
		return fullNodeName;
	}
	
	/**
	 * @return the nodeName
	 */
	protected String getNodeName() {
		return nodeName;
	}
	
	/**
	 * @return the rootNode
	 */
	protected boolean isRootNode() {
		return rootNode;
	}

	/**
	 * @return the systemNode
	 */
	private boolean isSystemNode() {
		return systemNode;
	}
	
	/**
	 * TODO Describe this method
	 *
	 * @return
	 */
	protected abstract String getFileExtension();

	/**
	 * TODO Describe this method
	 *
	 * @param properties
	 * @param fis
	 * @throws IOException 
	 */
	protected abstract void load(Properties properties, FileInputStream fis) throws IOException;
	
	/**
	 * TODO Describe this method
	 *
	 * @param properties
	 * @param fos
	 * @throws IOException 
	 */
	protected abstract void store(Properties properties, FileOutputStream fos) throws IOException;
	
	/**
	 * TODO Describe this method
	 *
	 * @param abstractPreferences
	 * @param name
	 * @return
	 */
	protected abstract PropertiesPreferences createChildNode(AbstractPreferences abstractPreferences, String name);
}